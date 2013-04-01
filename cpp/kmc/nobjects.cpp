//
//  nobjects.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "nobjects.h"

namespace kmc {
  namespace plugin {
    NObjects::NObjects(const boost::filesystem::path& p,
                       lattice::State* state) : p_(p), state_(state) { }
    
    void NObjects::boot(kmc::lattice::Lattice* lattice) {
      lattice_ = lattice;
      of_ = std::ofstream(p_.string());
      of_ << "# " << state_->name() << std::endl;
    }

    void NObjects::process(double time) {
      std::size_t count = 0;
      for (const lattice::State* state : lattice_->states()) {
        count += (state == state_);
      }
      of_ << time << '\t' << count << std::endl;
    }
    
    void NObjects::close() {
      of_.close();
    }
  }
}