//
//  transition_manager.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_transition_manager_h
#define kmc_transition_manager_h

#include "lattice.h"
#include "transition.h"

namespace kmc {
  /**
   * Interface for a generic KMC transition manager
   */
  class TransitionManager {
  public:
    virtual void move(double r) = 0;
    virtual double rate_total() const = 0;
  };
  
  /**
   * Implements a transition manager for a 1D lattice
   */
  class LatticeTransitionManager : public TransitionManager {
  private:
    lattice::Lattice* lattice_;
    std::vector<Transition*> transitions_;
    std::vector<double> accumulated_rates_;
    // Transitions that depend on a (coordinate,state)
    std::vector<std::vector<std::vector<Transition*>>> downstream_coord_;
    // Transitions downstream of another transition
    std::vector<std::vector<Transition*>> downstream_;
    
    std::size_t transition(const double r) const;
    void update_transition(Transition* t);
    void update_all_transitions();
    void update_accumulated_rates();
    
  public:
    LatticeTransitionManager(lattice::Lattice* lattice,
                             std::vector<Transition*>&& transitions);
    
    virtual void move(double r) override;
    virtual double rate_total() const override;
  };
}

#endif
