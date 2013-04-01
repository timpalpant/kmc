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

#include "lattice.h"
#include "transition.h"

namespace kmc {
  class Particle {
  private:
    lattice::State* state_;
    std::size_t size_ = 1;
    double adsorption_rate_ = 1.0;
    double desorption_rate_ = 1.0;
    double diffusion_rate_ = 1.0;
    
  public:
    Particle(lattice::State* state);
    
    std::vector<Transition*> adsorption_transitions(const lattice::Lattice* lattice) const;
    std::vector<Transition*> desorption_transitions(const lattice::Lattice* lattice) const;
    std::vector<Transition*> diffusion_transitions(const lattice::Lattice* lattice) const;
    std::vector<Transition*> transitions(const lattice::Lattice* lattice) const;
    
    std::size_t size() const { return size_; }
    void set_size(const std::size_t size) { size_ = size; }
  
    double adsorption_rate(const std::size_t i, const std::size_t j) const {
      return adsorption_rate_;
    }
    
    double desorption_rate(const std::size_t i, const std::size_t j) const {
      return desorption_rate_;
    }
    
    double diffusion_rate(const std::size_t i, const std::size_t j) const {
      return diffusion_rate_;
    }
    
    double hop_rate(const std::size_t i1, const std::size_t j1,
                    const std::size_t i2, const std::size_t j2) const {
      return 0;
    }
  };
}

#endif
