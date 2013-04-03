//
//  main.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/16/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include <iostream>

#include "parameters.h"
#include "lattice.h"
#include "particle.h"
#include "kmc.h"

kmc::lattice::Lattice* initLattice(const kmc::Parameters& params) {
  return new kmc::lattice::Lattice(params.lattice_size(),
                                   params.boundary_condition());
}

std::vector<kmc::Transition*> 
initTransitions(const kmc::Parameters& params,
                const kmc::lattice::Lattice* lattice) {
  std::vector<kmc::Transition*> transitions;
  for (const kmc::Particle& p : params.particles()) {
    const std::vector<kmc::Transition*>& particle_transitions = p.transitions(lattice);
    transitions.insert(transitions.end(),
                       particle_transitions.begin(),
                       particle_transitions.end());
  }
  
  return transitions;
}

std::vector<std::shared_ptr<kmc::plugin::Plugin>> 
initPlugins(const kmc::Parameters& params,
            kmc::lattice::Lattice* lattice) {
  std::vector<std::shared_ptr<kmc::plugin::Plugin>> plugins = params.plugins();
  for (std::shared_ptr<kmc::plugin::Plugin> plugin : plugins) {
    plugin->boot(lattice);
  }
  
  std::cout << "Initialized " << plugins.size() << " plugins" << std::endl;
  return plugins;
}

kmc::KineticMonteCarlo initKMC(const kmc::Parameters& params,
                               kmc::TransitionManager* manager,
                               std::vector<std::shared_ptr<kmc::plugin::Plugin>>& plugins) {
  kmc::KineticMonteCarlo kmc(manager, plugins);
  
  kmc.set_seed(params.seed());
  kmc.set_t_final(params.t_final());
  
  return kmc;
}

int main(int argc, const char* argv[]) {
  if (argc < 3) {
    std::cerr << "USAGE: kmc [--include ARK] [--cfg KEY=VALUE]" << std::endl;
    return 2;
  }
  
  std::cout << "Loading configuration" << std::endl;
  kmc::Parameters params = kmc::Parameters::for_argv(argc, argv);
  
  std::cout << "Initializing simulation" << std::endl;
  kmc::lattice::Lattice* lattice = initLattice(params);
  kmc::TransitionManager* manager = new kmc::TransitionManager(lattice, initTransitions(params, lattice));
  std::vector<std::shared_ptr<kmc::plugin::Plugin>> plugins = initPlugins(params, lattice);
  kmc::KineticMonteCarlo kmc = initKMC(params, manager, plugins);
  
  std::cout << "Beginning simulation (last_time = " << kmc.t_final() << ")" << std::endl;
  kmc.run();
  
  std::cout << "Simulation complete" << std::endl;
  kmc.shutdown();
  
  return 0;
}

