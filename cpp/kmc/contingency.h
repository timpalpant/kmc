//
//  contingency.h
//  kmc
//
//  A struct of coordinate+State
//  Conditions reflect upstream dependencies,
//  while Actions are downstream effects of a given Transition
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_contingency_h
#define kmc_contingency_h

#include "state.h"

namespace kmc {
  class Contingency {
  private:
    std::size_t coord_;
    lattice::State* state_;
    
  protected:
    Contingency(const std::size_t coord, lattice::State* state)
      : coord_(coord), state_(state) { }
    
  public:
    std::size_t coord() const { return coord_; }
    lattice::State* state() const { return state_; }
  };
  
  class Condition : public Contingency {
  private:
    bool condition_ = true;
    
  public:
    Condition(const std::size_t coord, lattice::State* state)
      : Condition(coord, state, true) { }
      
    Condition(const std::size_t coord, lattice::State* state, 
              bool condition) : Contingency(coord, state), 
              condition_(condition) { }
      
    bool condition() const { return condition_; }
  };

  class Action : public Contingency {
  public:
    Action(const std::size_t coord, lattice::State* state)
      : Contingency(coord, state) { }
  };
}

#endif
