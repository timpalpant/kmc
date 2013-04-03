//
//  lattice.h
//  kmc
//
//  A one-dimensional lattice of States
//
//  Created by Timothy Palpant on 3/29/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_lattice_h
#define kmc_lattice_h

#include <vector>
#include <algorithm>

#include "state.h"
#include "contingency.h"

namespace kmc {
  namespace lattice {
    enum class BoundaryCondition {FIXED, PERIODIC};
    
    class Lattice {
    private:
      std::vector<State*> states_;
      BoundaryCondition bc_; 
      
    public:
      Lattice(std::size_t size, BoundaryCondition bc);
      
      const std::vector<State*>& states() const { return states_; }
      State* get(const std::size_t i) const { return states_[i]; }
      bool set(const std::size_t i, State* state);

      bool perform(const Action& a);
      bool satisfies(const Condition& c) const {
        if (c.condition()) {
          return get(c.coord()) == c.state();
        } else {
          return get(c.coord()) != c.state();
        }
      }
      
      void fill(State* state) {
        std::fill(states_.begin(), states_.end(), state);
      }
      
      std::size_t size() const { return states_.size(); }
      BoundaryCondition bc() const { return bc_; }
      
      template <typename T>
      std::size_t periodic_wrap(const T i) const;
    };
  }
}

#endif
