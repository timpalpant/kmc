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
    void NObjects::configure(const boost::property_tree::ptree& pt) {
      p_ = pt.get<boost::filesystem::path>("output");
      std::string particle = pt.get<std::string>("particle");
      state_ = lattice::State::for_name(particle);
    }
    
    void NObjects::boot(kmc::lattice::Lattice* lattice) {
      lattice_ = lattice;
      of_.open(p_.c_str());
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
