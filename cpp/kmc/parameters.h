//
//  parameters.h
//  kmc
//
//  Load KMC configuration files into a plain old data structure
//  This class insulates the rest of the application from the
//  implementation details of the configuration format
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_config_h
#define kmc_config_h

#include <boost/property_tree/ptree.hpp>
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
    int seed_;
    
    Parameters() { }
    
  public:
    static Parameters load(const boost::filesystem::path& p) throw (config_error);
    static Parameters for_argv(const int argc, const char* argv[]) throw (config_error);
    
    void update(const boost::property_tree::ptree& pt) throw (config_error);
    
    std::size_t lattice_size() const { return lattice_size_; }
    lattice::BoundaryCondition boundary_condition() const { return bc_; }
    const std::vector<Particle>& particles() const { return particles_; }
    const std::vector<std::shared_ptr<plugin::Plugin>>& plugins() const { return plugins_; }
    double temperature() const { return temperature_; }
    double t_final() const { return t_final_; }
    int seed() const { return seed_; }
  };
}

#endif
