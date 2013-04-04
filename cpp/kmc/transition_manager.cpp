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
#include <numeric>
#include <stdexcept>

namespace kmc {
  LatticeTransitionManager::LatticeTransitionManager(lattice::Lattice* lattice,
                                                     std::vector<Transition*>&& transitions) :
    lattice_(lattice), transitions_(transitions), enabled_rates_(transitions.size()),
    accumulated_rates_(transitions.size()), downstream_coord_(lattice->size()) {
      
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
    for (std::size_t i = 0; i < transitions_.size(); i++) {
      for (const Condition& c : transitions_[i]->conditions()) {
        // "not" conditions don't get optimized
        // In effect, they are a condition on all states
        if (!c.condition()) {
          for (std::size_t j = 0; j < lattice::State::n_states(); j++) {
            downstream_coord_[c.coord()][j].push_back(i);
          }
        } else {
          downstream_coord_[c.coord()][c.state()->id()].push_back(i);
        }
      }
    }
      
    // Now construct a map of Transition -> Transitions
    // If Transition t is performed, which Transitions might be enabled?
    std::cout << "Collecting downstream Transitions for each Transition" << std::endl;
    std::vector<std::size_t> all_downstream;
    downstream_.reserve(transitions.size());
    for (std::size_t i = 0; i < transitions_.size(); i++) {
      all_downstream.clear();
      for (const Action& a : transitions_[i]->actions()) {
        all_downstream.insert(all_downstream.end(),
                              downstream_coord_[a.coord()][a.state()->id()].begin(),
                              downstream_coord_[a.coord()][a.state()->id()].end());
      }
      std::sort(all_downstream.begin(), all_downstream.end());
      std::vector<std::size_t>::iterator it;
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
      lattice::State* prev = lattice_->perform(a);

      // These transitions had a condition on the previous state,
      // which changed, so now they must be disabled
      if (prev != a.state()) {
        for (const std::size_t& affected : downstream_coord_[a.coord()][prev->id()]) {
          enabled_rates_[affected] = 0.0;
        }
      }
    }

    // These transitions have a condition on the new state(s),
    // and may now be enabled (if they were disabled before)
    for (const std::size_t& affected : downstream_[selected]) {
      if (enabled_rates_[affected] == 0.0) {
        update_transition(affected);
      }
    }
    
    update_accumulated_rates();
  }
  
  /**
   * Update the table of accumulated rates
   */
  void LatticeTransitionManager::update_accumulated_rates() {
    std::partial_sum(enabled_rates_.begin(),
                     enabled_rates_.end(),
                     accumulated_rates_.begin());
  }
  
  /**
   * Check whether a Transition's conditions are all satisified
   * and mark it as enabled/disabled
   */
  void LatticeTransitionManager::update_transition(const std::size_t t) {
    enabled_rates_[t] = transitions_[t]->rate();
    for (const Condition& c : transitions_[t]->conditions()) {
      if (!lattice_->satisfies(c)) {
        enabled_rates_[t] = 0.0;
        break;
      }
    }
  }
  
  /**
   * Mark all Transitions as enabled/disabled
   */
  void LatticeTransitionManager::update_all_transitions() {
    for (std::size_t t = 0; t < transitions_.size(); t++) {
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
