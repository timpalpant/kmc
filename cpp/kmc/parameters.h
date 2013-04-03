//
//  parameters.h
//  kmc
//
//  Load KMC configuration files into plain old data structures
//  This class insulates the rest of the application from the
//  implementation details of the configuration format
//  But it's also a mess. Consider having plugins and particles
//  configure themselves instead.
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_config_h
#define kmc_config_h

#include <boost/filesystem.hpp>

#include "lattice.h"
#include "particle.h"
#include "plugin.h"
#include "config_error.h"

namespace kmc {
  class Parameters {
  private:
    std::vector<Particle> particles_;
    std::vector<std::shared_ptr<plugin::Plugin>> plugins_;
    std::size_t lattice_size_;
    lattice::BoundaryCondition bc_;
    double beta_;
    double t_final_;
    unsigned int seed_;
        
  public:
    static Parameters load(const boost::filesystem::path& p) throw (config_error);
    static Parameters for_argv(const int argc, const char* argv[]) throw (config_error);
       
    std::size_t lattice_size() const { return lattice_size_; }
    void set_lattice_size(const std::size_t size) { lattice_size_ = size; }
    lattice::BoundaryCondition boundary_condition() const { return bc_; }
    void set_boundary_condition(const lattice::BoundaryCondition bc) { bc_ = bc; }
    const std::vector<Particle>& particles() const { return particles_; }
    void add_particle(const Particle& p) { particles_.push_back(p); }
    const std::vector<std::shared_ptr<plugin::Plugin>>& plugins() const { return plugins_; }
    void add_plugin(const std::shared_ptr<plugin::Plugin>& p) { plugins_.push_back(p); }
    double beta() const { return beta_; }
    void set_beta(const double beta) { beta_ = beta; }
    double t_final() const { return t_final_; }
    void set_t_final(const double t) { t_final_ = t; }
    unsigned int seed() const { return seed_; }
    void set_seed(const unsigned int seed) { seed_ = seed; }
  };
}

#endif
