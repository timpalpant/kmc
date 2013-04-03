//
//  transition_manager.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "transition_manager.h"

#include <iostream>
#include <set>
#include <stdexcept>

namespace kmc {
  LatticeTransitionManager::LatticeTransitionManager(lattice::Lattice* lattice,
                                                     std::vector<Transition*>&& transitions) :
    lattice_(lattice), transitions_(transitions), accumulated_rates_(transitions.size()) {
      
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
      
    // First construct a map of coordinate -> Transitions
    // If coordinate c changes, which Transitions must be updated?
    std::cout << "Collecting dependencies on each lattice coordinate" << std::endl;
    std::vector<std::vector<Transition*>> downstream_coord(lattice_->size());
    for (Transition* t : transitions_) {
      for (const Condition& c : t->conditions()) {
        downstream_coord[c.coord()].push_back(t);
      }
    }
      
    // Now construct a map of Transition -> Transitions
    // If Transition t is performed, which Transitions must be updated?
    std::cout << "Collecting downstream Transitions for each Transition" << std::endl;
    std::set<Transition*> downstream_set;
    downstream_.reserve(transitions_.size());
    for (const Transition* t : transitions_) {
      downstream_set.clear();
      for (const Action& a : t->actions()) {
        downstream_set.insert(downstream_coord[a.coord()].begin(),
                              downstream_coord[a.coord()].end());
      }
      downstream_.push_back(std::vector<Transition*>(downstream_set.begin(),
                                                     downstream_set.end()));
    }
      
    std::cout << "Updating all transitions and rates" << std::endl;
    update_all_transitions();
    update_accumulated_rates();
  }
  
  /** 
   * Get the selected Transition for the random number r \in [0,1]
   */
  std::size_t LatticeTransitionManager::transition(const double r) const {
    std::vector<double>::const_iterator selected = std::lower_bound(accumulated_rates_.begin(),
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
      bool changed = lattice_->set(a.coord(), a.state());
      if (optimize_ && !changed) {
        std::cerr << "Action performed had no effect. Turning off optimization" << std::endl;
        optimize_ = false;
      }
    }
    
    for (Transition* t : downstream_[selected]) {
      if (optimize_ && t->enabled()) {
        t->set_enabled(false);
      } else {
        update_transition(t);
      }
    }
    
    update_accumulated_rates();
  }
  
  /**
   * Update the table of accumulated rates
   */
  void LatticeTransitionManager::update_accumulated_rates() {
    accumulated_rates_[0] = transitions_[0]->enabled() ? transitions_[0]->rate() : 0.0;
    for (std::size_t i = 1; i < transitions_.size(); i++) {
      double rate = transitions_[i]->enabled() ? transitions_[i]->rate() : 0.0;
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
