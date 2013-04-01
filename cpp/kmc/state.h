//
//  state.h
//  kmc
//
//  A dynamic enumeration of lattice states
//
//  Created by Timothy Palpant on 3/29/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_state_h
#define kmc_state_h

#include <vector>
#include <string>

namespace kmc {
  namespace lattice {
    class State {
    private:
      std::size_t id_;
      std::string name_;
      
      explicit State(const std::string& name);
      
    public:
      static State* EMPTY;
      static State* STERIC;
      static State* for_name(const std::string& name);
      static std::vector<State*> states();
      static std::size_t n_states();
      
      std::size_t id() const { return id_; }
      std::string name() const { return name_; }      
    };
  }
}

#endif
