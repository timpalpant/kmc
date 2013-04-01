//
//  config.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "parameters.h"
#include "plugin.h"
#include "status.h"
#include "trajectory.h"
#include "distribution.h"
#include "nobjects.h"

#include <boost/property_tree/xml_parser.hpp>

#include <iostream>

namespace kmc {
  void Parameters::update(const boost::property_tree::ptree& pt) throw (config_error) {
    // Load lattice parameters
    lattice_size_ = pt.get<std::size_t>("lattice.length");
    std::string bc = pt.get("lattice.bc", "fixed");
    if (bc == "fixed") {
      bc_ = lattice::BoundaryCondition::FIXED;
    } else if (bc == "periodic") {
      bc_ = lattice::BoundaryCondition::PERIODIC;
    } else {
      throw config_error("Unknown boundary condition: "+bc);
    }
    
    // Load particle parameters
    boost::property_tree::ptree particles = pt.get_child("particles");
    for (const std::pair<std::string,boost::property_tree::ptree>& ppair : particles) {
      std::string name = ppair.first;
      std::cout << "Initializing particle " << name << std::endl;
      boost::property_tree::ptree pconfig = ppair.second;
      lattice::State* state = lattice::State::for_name(name);
      Particle particle(state);
      
      // Configure the particle
      particle.set_size(pconfig.get<std::size_t>("size"));
      
      particles_.push_back(particle);
    }
    
    // Load simulation parameters
    boost::property_tree::ptree kmc = pt.get_child("kmc");
    temperature_ = kmc.get("temperature", 1.0);
    t_final_ = kmc.get<double>("last_time");
    seed_ = kmc.get<int>("seed");
    
    // Load plugins
    boost::property_tree::ptree plugins = kmc.get_child("plugins");
    plugin::Plugin* status = new plugin::Status(20000);
    plugins_.push_back(std::shared_ptr<plugin::Plugin>(status));
    
    //boost::filesystem::path trj_output("test.trj");
    //plugin::Plugin* trj = new plugin::Trajectory(trj_output);
    //params.plugins_.push_back(std::shared_ptr<plugin::Plugin>(trj));
    
    boost::filesystem::path dist_output("dist.txt");
    plugin::Plugin* dist = new plugin::Distribution(dist_output,
                                                    lattice::State::for_name("nuc"));
    plugins_.push_back(std::shared_ptr<plugin::Plugin>(dist));
    
    boost::filesystem::path nobjects_output("nobjects.txt");
    plugin::Plugin* nobjects = new plugin::NObjects(nobjects_output,
                                                    lattice::State::for_name("nuc"));
    plugins_.push_back(std::shared_ptr<plugin::Plugin>(nobjects));
  }
  
  boost::property_tree::ptree load_xml(const boost::filesystem::path& p) {
    boost::property_tree::ptree pt;
    std::cout << "Loading configuration from " << p << std::endl;
    boost::property_tree::read_xml(p.string(), pt);
    return pt;
  }
  
  Parameters Parameters::load(const boost::filesystem::path& p) throw (config_error) {
    Parameters params;
    params.update(load_xml(p));
    return params;
  }
  
  Parameters Parameters::for_argv(const int argc, const char* argv[]) throw (config_error) {
    Parameters params;
    bool is_include = false, is_cfg = false;
    for (int i = 1; i < argc; i++) {
      std::string arg(argv[i]);
      if (arg.compare("--include") == 0) {
        is_include = true;
      } else if (arg.compare("--cfg") == 0) {
        is_cfg = true;
      } else {
        if (is_include) {
          params.update(load_xml(arg));
        } else if (is_cfg) {
          boost::property_tree::ptree pt;
          std::string::size_type eq = arg.find('=');
          std::string key = arg.substr(0, eq);
          std::string value = arg.substr(eq);
          pt.put(key, value);
          params.update(pt);
        } else {
          std::cerr << "USAGE: kmc [--include ARK] [--cfg KEY=VALUE]" << std::endl;
          throw config_error("Cannot parse arguments");
        }
        
        is_include = false;
        is_cfg = false;
      }
    }
    
    return params;
  }
}