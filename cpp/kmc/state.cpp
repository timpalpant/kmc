//
//  state.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "state.h"
#include <map>
#include <boost/lexical_cast.hpp>

namespace kmc {
  namespace lattice {
    std::map<std::string, State*> states_by_name_;
    std::vector<State*> states_by_id_;
    
    State* State::for_name(const std::string& name) {
      if (states_by_name_.count(name)) {
        return states_by_name_[name];
      }
      
      return new State(name);
    }
    
    const std::vector<State*>& State::states() {
      return states_by_id_;
    }
    
    State* State::substate(const std::size_t i) const {
      std::string ssname = name()+"-"+boost::lexical_cast<std::string>(i);
      State* sstate = for_name(ssname);
      (*sstate).parent_ = this;
      return sstate;
    }
    
    std::size_t State::n_states() {
      return states_by_id_.size();
    }
    
    State::State(const std::string& name)
      : id_(n_states()), name_(name) { 
      states_by_name_[name] = this;
      states_by_id_.push_back(this);
    }
    
    State* State::EMPTY = State::for_name("empty");
    State* State::STERIC = State::for_name("steric");
  }
}
