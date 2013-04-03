//
//  config.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "parameters.h"
#include "plugin_factory.h"

#include <boost/property_tree/xml_parser.hpp>

#include <iostream>
#include <queue>

namespace kmc {
  Parameters parse(const boost::property_tree::ptree& pt) throw (config_error) {
    Parameters params;
    
    // Load lattice parameters
    params.set_lattice_size(pt.get<std::size_t>("lattice.length"));
    std::string bc = pt.get("lattice.bc", "fixed");
    if (bc == "fixed") {
      params.set_boundary_condition(lattice::BoundaryCondition::FIXED);
    } else if (bc == "periodic") {
      params.set_boundary_condition(lattice::BoundaryCondition::PERIODIC);
    } else {
      throw config_error("Unknown boundary condition: "+bc);
    }
    
    // Load simulation parameters
    const boost::property_tree::ptree& kmc = pt.get_child("kmc");
    params.set_beta(kmc.get("beta", 1.0));
    params.set_t_final(kmc.get<double>("last_time"));
    params.set_seed(kmc.get<unsigned int>("seed"));
    
    // Load particle parameters
    const boost::property_tree::ptree& particles = pt.get_child("particles");
    for (const std::pair<std::string,boost::property_tree::ptree>& ppair : particles) {
      std::string name = ppair.first;
      lattice::State* state = lattice::State::for_name(name);
      std::cout << "Initializing particle " << name 
        << " (state " << state->id() << ")" << std::endl;
      boost::property_tree::ptree pconfig = ppair.second;
      Particle particle(state);
      particle.configure(pconfig);
      params.add_particle(particle);
    }
    
    // Load plugins
    const boost::property_tree::ptree& plugins = kmc.get_child("plugins");
    for (const std::pair<std::string,boost::property_tree::ptree>& ppair : plugins) {
      std::string name = ppair.first;
      boost::property_tree::ptree pconfig = ppair.second;
      std::string type = pconfig.get<std::string>("type");
      plugin::Plugin* plugin = plugin::for_type(type);
      plugin->configure(pconfig);
      params.add_plugin(std::shared_ptr<plugin::Plugin>(plugin));
    }
    
    return params;
  }

  /**
  * Merge one property tree into another
  */  
  void merge(boost::property_tree::ptree& rptFirst, 
             const boost::property_tree::ptree& rptSecond) {
    // Keep track of keys and values (subtrees) in second property tree
    std::queue<std::string> qKeys;
    std::queue<boost::property_tree::ptree> qValues;
    qValues.push(rptSecond);

    // Iterate over second property tree
    while(!qValues.empty()) {
      // Setup keys and corresponding values
      boost::property_tree::ptree ptree = qValues.front();
      qValues.pop();
      std::string keychain = "";
      if(!qKeys.empty()) {
        keychain = qKeys.front();
        qKeys.pop();
      }

      // Iterate over keys level-wise
      for(const boost::property_tree::ptree::value_type& child : ptree) {
        if(child.second.size() == 0) { // Leaf
          // No "." for first level entries
          std::string s;
          if(keychain != "") {
            s = keychain + "." + child.first.data();
          } else {
            s = child.first.data();
          }

          // Put into combined property tree
          rptFirst.put(s, child.second.data());
        } else { // Subtree
          // Put keys (identifiers of subtrees) and all of its parents (where present)
          // aside for later iteration. Keys on first level have no parents
          if(keychain != "") {
            qKeys.push(keychain + "." + child.first.data());
          } else {
            qKeys.push(child.first.data());
          }
        
          // Put values (the subtrees) aside, too
          qValues.push( child.second );
        }
      }
    }
  }

  boost::property_tree::ptree load_cfg_file(const boost::filesystem::path& p) {
    boost::property_tree::ptree pt;
    std::cout << "Loading configuration from " << p << std::endl;
    boost::property_tree::read_xml(p.string(), pt);
    return pt;
  }
  
  Parameters Parameters::load(const boost::filesystem::path& p) throw (config_error) {
    return parse(load_cfg_file(p));
  }
  
  Parameters Parameters::for_argv(const int argc, const char* argv[]) throw (config_error) {
    boost::property_tree::ptree pt;
    bool is_include = false, is_cfg = false;
    for (int i = 1; i < argc; i++) {
      std::string arg(argv[i]);
      if (arg.compare("--include") == 0) {
        is_include = true;
      } else if (arg.compare("--cfg") == 0) {
        is_cfg = true;
      } else {
        if (is_include) {
          merge(pt, load_cfg_file(arg));
        } else if (is_cfg) {
          std::string::size_type eq = arg.find('=');
          std::string key = arg.substr(0, eq);
          std::string value = arg.substr(eq+1);
          pt.put(key, value);
        } else {
          std::cerr << "USAGE: kmc [--include ARK] [--cfg KEY=VALUE]" << std::endl;
          throw config_error("Cannot parse arguments");
        }
        
        is_include = false;
        is_cfg = false;
      }
    }
    
    return parse(pt);
  }
}
