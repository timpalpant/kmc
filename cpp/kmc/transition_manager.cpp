//
//  transition_manager.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "transition_manager.h"

#include <iostream>
#include <algorithm>
#include <stdexcept>

namespace kmc {
  LatticeTransitionManager::LatticeTransitionManager(lattice::Lattice* lattice,
                                                     std::vector<Transition*>&& transitions) :
    lattice_(lattice), transitions_(transitions), accumulated_rates_(transitions.size()),
    downstream_coord_(lattice->size()) {
      
    std::cout << "Initializing transition manager with "
      << transitions_.size() << " transitions and " << lattice::State::n_states()
      << " states" << std::endl;

    if (transitions_.size() == 0) {
      throw std::runtime_error("Must have at least one transition");
    }
      
    // Since we have all conditions and actions up front
    // we can construct a minimal dependency graph:
    // If transition i is performed, which Transitions
    // are downstream and need to be updated?
      
    // First construct a map of (coordinate,state) -> Transitions
    // If (c,s) changes, which Transitions must be disabled?
    std::cout << "Collecting dependencies on each lattice coordinate" << std::endl;
    for (std::size_t i = 0; i < lattice_->size(); i++) {
      downstream_coord_[i].resize(lattice::State::n_states());
    }
    for (Transition* t : transitions_) {
      for (const Condition& c : t->conditions()) {
        // "not" conditions don't get optimized
        // In effect, they are a condition on all states
        if (!c.condition()) {
          for (std::size_t i = 0; i < lattice::State::n_states(); i++) {
            downstream_coord_[c.coord()][i].push_back(t);
          }
        } else {
          downstream_coord_[c.coord()][c.state()->id()].push_back(t);
        }
      }
    }
      
    // Now construct a map of Transition -> Transitions
    // If Transition t is performed, which Transitions might be enabled?
    std::cout << "Collecting downstream Transitions for each Transition" << std::endl;
    std::vector<Transition*> all_downstream;
    downstream_.reserve(transitions.size());
    for (const Transition* t : transitions_) {
      all_downstream.clear();
      for (const Action& a : t->actions()) {
        all_downstream.insert(all_downstream.end(),
                              downstream_coord_[a.coord()][a.state()->id()].begin(),
                              downstream_coord_[a.coord()][a.state()->id()].end());
      }
      std::sort(all_downstream.begin(), all_downstream.end());
      std::vector<Transition*>::iterator it;
      it = std::unique(all_downstream.begin(), all_downstream.end());
      all_downstream.resize(std::distance(all_downstream.begin(), it));
      downstream_.push_back(all_downstream);

      if (downstream_.size() % 1000000 == 0) {
        std::cout << downstream_.size() << std::endl;
      }
    }
      
    std::cout << "Updating all transitions and rates" << std::endl;
    update_all_transitions();
    update_accumulated_rates();
  }
  
  /** 
   * Get the selected Transition for the random number r \in [0,1]
   */
  std::size_t LatticeTransitionManager::transition(const double r) const {
    std::vector<double>::const_iterator selected;
    selected = std::lower_bound(accumulated_rates_.begin(),
                                accumulated_rates_.end(),
                                r*rate_total());
    return std::distance(accumulated_rates_.begin(), selected);
  }
  
  /**
   * For the random number r \in [0,1], get the selected Transition,
   * perform its actions, and update any downstream dependencies
   */
  void LatticeTransitionManager::move(double r) {
    std::size_t selected = transition(r);
    const Transition* t = transitions_[selected];
    for (const Action& a : t->actions()) {
      lattice::State* prev = lattice_->get(a.coord());
      bool changed = lattice_->perform(a);

      // These transitions had a condition on the previous state,
      // which changed, so now they must be disabled
      if (changed) {
        for (Transition* td : downstream_coord_[a.coord()][prev->id()]) {
          td->set_enabled(false);
        }
      }
    }

    // These transitions have a condition on the new state(s),
    // and may now be enabled (if they were disabled before)
    for (Transition* td : downstream_[selected]) {
      if (!td->enabled()) {
        update_transition(td);
      }
    }
    
    update_accumulated_rates();
  }
  
  /**
   * Update the table of accumulated rates
   */
  void LatticeTransitionManager::update_accumulated_rates() {
    accumulated_rates_[0] = transitions_[0]->enabled() * transitions_[0]->rate();
    for (std::size_t i = 1; i < transitions_.size(); i++) {
      double rate = transitions_[i]->enabled() * transitions_[i]->rate();
      accumulated_rates_[i] = accumulated_rates_[i-1] + rate;
    }
  }
  
  /**
   * Check whether a Transition's conditions are all satisified
   * and mark it as enabled/disabled
   */
  void LatticeTransitionManager::update_transition(Transition* t) {
    t->set_enabled(true);
    for (const Condition& c : t->conditions()) {
      if (!lattice_->satisfies(c)) {
        t->set_enabled(false);
        break;
      }
    }
  }
  
  /**
   * Mark all Transitions as enabled/disabled
   */
  void LatticeTransitionManager::update_all_transitions() {
    for (Transition* t : transitions_) {
      update_transition(t);
    }
  }
  
  /**
   * The total rate of all enabled Transitions
   */
  double LatticeTransitionManager::rate_total() const {
    return accumulated_rates_.back();
  }
}
