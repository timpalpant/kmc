//
//  distribution.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "distribution.h"

namespace kmc {
  namespace plugin {
    Distribution::Distribution(const boost::filesystem::path& p,
                               lattice::State* state)
      : p_(p), state_(state) { }
    
    void Distribution::boot(kmc::lattice::Lattice* lattice) {
      lattice_ = lattice;
      last_state_ = std::vector<lattice::State*>(lattice_->states());
      dist_ = std::vector<double>(lattice_->size());
      of_ = std::ofstream(p_.string());
      of_ << "# Position\tProbability" << std::endl;
    }
    
    void Distribution::process(double time) {
      double dt = time - last_time_;
      for (std::size_t i = 0; i < last_state_.size(); i++) {
        if (last_state_[i] == state_) {
          dist_[i] += dt;
        }
        last_state_[i] = lattice_->get(i);
      }
      last_time_ = time;
    }
    
    void Distribution::close() {
      for (std::size_t i = 0; i < dist_.size(); i++) {
        of_ << i << '\t' << dist_[i]/last_time_ << std::endl;
      }
      of_.close();
    }
  }
}