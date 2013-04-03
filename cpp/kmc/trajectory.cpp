//
//  trajectory.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "trajectory.h"

namespace kmc {
  namespace plugin {
    void Trajectory::configure(const boost::property_tree::ptree& pt) {
      p_ = pt.get<boost::filesystem::path>("output");
    }
    
    void Trajectory::boot(kmc::lattice::Lattice* lattice) {
      lattice_ = lattice;
      of_.open(p_.c_str());
      for (const kmc::lattice::State* s : kmc::lattice::State::states()) {
        of_ << "# " << s->id() << '\t' << s->name() << std::endl;
      }
    }
    
    void Trajectory::process(double time) {
      of_ << time << '\t';
      for (const lattice::State* s : lattice_->states()) {
        of_ << s->id() << '.';
      }
      of_ << std::endl;
    }
    
    void Trajectory::close() {
      of_.close();
    }
  }
}
