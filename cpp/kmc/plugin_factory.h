//
//  plugin_factory.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_plugin_factory_h
#define kmc_plugin_factory_h

#include "plugin.h"
#include "config_error.h"
#include "status.h"
#include "trajectory.h"
#include "distribution.h"
#include "nobjects.h"
#include "twobody.h"

namespace kmc {
  namespace plugin {
    class plugin_error : public config_error {
    public:
      explicit plugin_error(const std::string& what_arg)
        : config_error(what_arg) { }
      explicit plugin_error(const char* what_arg)
        : config_error(what_arg) { }
    };
    
    Plugin* for_type(const std::string& name) throw (plugin_error) {
      Plugin* p;
      if (name == "distribution") {
        p = new Distribution();
      } else if (name == "nobjects") {
        p = new NObjects();
      } else if (name == "trajectory") {
        p = new Trajectory();
      } else if (name == "status") {
        p = new Status();
      } else if (name == "twobody") {
        p = new TwoBody();
      } else {
        throw plugin_error("Unknown plugin type: "+name);
      }
      
      return p;
    }
  }
}

#endif
