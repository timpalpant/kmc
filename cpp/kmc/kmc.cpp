//
//  kmc.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "kmc.h"
#include "transition.h"

#include <cmath>
#include <iostream>

namespace kmc {
  KineticMonteCarlo::KineticMonteCarlo(TransitionManager* manager,
                                       const std::vector<std::shared_ptr<plugin::Plugin>>& plugins)
    : manager_(manager), plugins_(plugins) { }
  
  void KineticMonteCarlo::run() {
    std::uniform_real_distribution<double> u(0,1);
    while (t_ < t_final_) {
      for (const std::shared_ptr<plugin::Plugin>& p : plugins_) {
        p->process(t_);
      }
      
      double r1 = u(rng_);
      manager_->move(r1);
      
      double r2 = u(rng_);
      double dt = -std::log(r2) / manager_->rate_total();
      t_ += dt;
    }
    
    // Excecute each of the plugins for the last frame
    for (const std::shared_ptr<plugin::Plugin>& p : plugins_) {
      p->process(t_);
    }
  }
  
  void KineticMonteCarlo::shutdown() {
    for (const std::shared_ptr<plugin::Plugin>& p : plugins_) {
      p->close();
    }
  }
}