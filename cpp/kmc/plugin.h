//
//  plugin.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_plugin_h
#define kmc_plugin_h

#include <map>

#include "lattice.h"

namespace kmc {
  namespace plugin {
    class Plugin {
    public:
      virtual void boot(kmc::lattice::Lattice* lattice) { }
      virtual void process(double time) = 0;
      virtual void close() { }
    };
  }
}

#endif
