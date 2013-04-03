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

#include <exception>

#include <boost/filesystem.hpp>

#include "lattice.h"
#include "particle.h"
#include "plugin.h"

namespace kmc {
  class config_error : public std::runtime_error {
  public:
    explicit config_error(const std::string& what_arg)
      : std::runtime_error(what_arg) { }
    explicit config_error(const char* what_arg)
      : std::runtime_error(what_arg) { }
  };
  
  class Parameters {
  private:
    std::vector<Particle> particles_;
    std::vector<std::shared_ptr<plugin::Plugin>> plugins_;
    std::size_t lattice_size_;
    lattice::BoundaryCondition bc_;
    double temperature_;
    double t_final_;
    std::size_t seed_;
        
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
    double temperature() const { return temperature_; }
    void set_temperature(const double t) { temperature_ = t; }
    double t_final() const { return t_final_; }
    void set_t_final(const double t) { t_final_ = t; }
    std::size_t seed() const { return seed_; }
    void set_seed(const std::size_t seed) { seed_ = seed; }
  };
}

#endif
