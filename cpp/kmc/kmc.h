//
//  kmc.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_kmc_h
#define kmc_kmc_h

#include <random>
#include <memory>

#include "transition_manager.h"
#include "plugin.h"

namespace kmc {
  class KineticMonteCarlo {
  private:
    TransitionManager* manager_;
    std::vector<std::shared_ptr<plugin::Plugin>> plugins_;
    double t_ = 0, t_final_;
    
    typedef std::mt19937 Random;
    Random rng_;
    
  public:
    KineticMonteCarlo(TransitionManager* manager,
                      const std::vector<std::shared_ptr<plugin::Plugin>>& plugins);
    
    void run();
    void shutdown();
    
    double t_final() const { return t_final_; }
    void set_t_final(const double t_final) { t_final_ = t_final; }
    void set_seed(const Random::result_type seed) { rng_.seed(seed); }
  };
}

#endif
