//
//  particle.h
//  kmc
//
//  A particle with a maximum size, that can adsorb,
//  desorb, diffuse, grow, shrink, and hop
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_particle_h
#define kmc_particle_h

#include <boost/property_tree/ptree.hpp>
#include <boost/filesystem.hpp>

#include "lattice.h"
#include "process.h"
#include "config_error.h"

namespace kmc {
  class particle_error : public config_error {
  public:
    explicit particle_error(const std::string& what_arg)
      : config_error(what_arg) { }
    explicit particle_error(const char* what_arg)
      : config_error(what_arg) { }
  };
  
  class Particle {
  private:
    lattice::State* state_;
    std::vector<lattice::State*> substates_;
    std::vector<double> potential_;
    std::vector<Process*> processes_;
    std::size_t size_;
    bool unwrap_ = false;

  public:
    Particle(lattice::State* state) : state_(state) { }
    void configure(const boost::property_tree::ptree& pt) throw (particle_error);
    const std::vector<Process*>& processes() const { return processes_; }
    
    lattice::State* state() const { return state_; }
    lattice::State* state(std::size_t i) const {
      return substates_[i];
    }
  
    std::string name() const { return state_->name(); }
    std::size_t size() const { return size_; }
    bool unwrap() const { return unwrap_; }
    const std::vector<double>& potential() const { return potential_; }
  };
}

#endif
