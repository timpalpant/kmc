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
    void Status::configure(const boost::property_tree::ptree& pt) {
      interval_ = pt.get<unsigned long long>("interval");
    }
    
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
