//
//  lattice.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "lattice.h"

namespace kmc {
  namespace lattice {
    Lattice::Lattice(std::size_t size, BoundaryCondition bc) : states_(size), bc_(bc) {
      fill(State::EMPTY);
    }
    
    template <typename T>
    std::size_t Lattice::periodic_wrap(const T i) const {
      if (i < 0) {
        return periodic_wrap(size()+i);
      }
      
      return i % size();
    }
    
    bool Lattice::set(const std::size_t i, State* state) {
      if (get(i) == state) {
        return false;
      }
      
      states_[i] = state;
      return true;
    }
  }
}