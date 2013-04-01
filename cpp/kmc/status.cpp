//
//  status.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "status.h"

#include <iostream>

namespace kmc {
  namespace plugin {
    Status::Status(unsigned long long interval) : interval_(interval) { }
    
    void Status::process(double time) {
      if (++nsteps_ % interval_ == 0) {
        std::cout << "t = " << time << " (" << nsteps_ << " steps)" << std::endl;
      }
    }
    
    void Status::close() {
      std::cout << "Number of steps = " << nsteps_ << std::endl;
    }
  }
}