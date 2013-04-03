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

kmc::lattice::Lattice* init_lattice(const kmc::Parameters& params) {
  std::cout << "Initializing lattice" << std::endl;
  return new kmc::lattice::Lattice(params.lattice_size(),
                                   params.boundary_condition());
}

std::vector<kmc::Transition*> 
init_transitions(const kmc::Parameters& params,
                const kmc::lattice::Lattice* lattice) {
  std::cout << "Initializing transitions" << std::endl;
  std::vector<kmc::Transition*> transitions;
  for (const kmc::Particle& p : params.particles()) {
    std::cout << "Initializing transitions for particle: " << p.name() << std::endl;
    const std::vector<kmc::Transition*>& particle_transitions = p.transitions(lattice,
                                                                              params.beta());
    transitions.insert(transitions.end(),
                       particle_transitions.begin(),
                       particle_transitions.end());
  }
  
  return transitions;
}

std::vector<std::shared_ptr<kmc::plugin::Plugin>> 
init_plugins(const kmc::Parameters& params,
            kmc::lattice::Lattice* lattice) {
  std::cout << "Initializing plugins" << std::endl;
  std::vector<std::shared_ptr<kmc::plugin::Plugin>> plugins = params.plugins();
  for (std::shared_ptr<kmc::plugin::Plugin> plugin : plugins) {
    plugin->boot(lattice);
  }
  
  std::cout << "Initialized " << plugins.size() << " plugins" << std::endl;
  return plugins;
}

kmc::KineticMonteCarlo init_kmc(const kmc::Parameters& params,
                                kmc::TransitionManager* manager,
                                std::vector<std::shared_ptr<kmc::plugin::Plugin>>& plugins) {
  std::cout << "Initializing KMC simulator" << std::endl;
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
  kmc::lattice::Lattice* lattice = init_lattice(params);
  kmc::TransitionManager* manager
    = new kmc::LatticeTransitionManager(lattice,
                                        init_transitions(params, lattice));
  std::vector<std::shared_ptr<kmc::plugin::Plugin>> plugins = init_plugins(params, lattice);
  kmc::KineticMonteCarlo kmc = init_kmc(params, manager, plugins);
  
  std::cout << "Beginning simulation (last_time = " << kmc.t_final() << ")" << std::endl;
  kmc.run();
  
  std::cout << "Simulation complete" << std::endl;
  kmc.shutdown();
  
  return 0;
}

