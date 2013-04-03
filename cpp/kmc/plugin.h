//
//  plugin.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_plugin_h
#define kmc_plugin_h

#include <boost/property_tree/ptree.hpp>

#include "lattice.h"

namespace kmc {
  namespace plugin {
    class Plugin {
    public:    
      virtual void configure(const boost::property_tree::ptree& pt) { }
      virtual void boot(kmc::lattice::Lattice* lattice) { }
      virtual void process(double time) = 0;
      virtual void close() { }
    };
  }
}

#endif
