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
  class TransitionManager {
  private:
    lattice::Lattice* lattice_;
    std::vector<Transition*> transitions_;
    std::vector<double> accumulated_rates_;
    std::vector<std::vector<Transition*>> downstream_;
    
    std::size_t transition(const double r) const;
    void update_transition(Transition* t);
    void update_all_transitions();
    void update_accumulated_rates();
    
  public:
    TransitionManager(lattice::Lattice* lattice,
                      std::vector<Transition*>&& transitions);
    
    void move(double r);
    
    double rate_total() const;
  };
}

#endif
