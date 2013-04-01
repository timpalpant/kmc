//
//  state.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "state.h"
#include <map>

namespace kmc {
  namespace lattice {
    std::map<std::string, State*> states_;
    
    State* State::for_name(const std::string& name) {
      if (states_.count(name)) {
        return states_[name];
      }
      
      State* state = new State(name);
      states_[name] = state;
      return state;
    }
    
    std::vector<State*> State::states() {
      std::vector<State*> states;
      states.reserve(states_.size());
      for (const std::pair<std::string, State*>& pair : states_) {
        states.push_back(pair.second);
      }
      return states;
    }
    
    std::size_t State::n_states() {
      return states_.size();
    }
    
    State::State(const std::string& name)
      : id_(n_states()), name_(name) { }
    
    State* State::EMPTY = State::for_name("empty");
    State* State::STERIC = State::for_name("steric");
  }
}
