//
//  status.h
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_status_h
#define kmc_status_h

#include "plugin.h"

namespace kmc {
  namespace plugin {
    class Status : public Plugin {
    private:
      unsigned long long interval_ = 1;
      unsigned long long nsteps_ = 0;
      
    public:
      virtual void configure(const boost::property_tree::ptree& pt) override;      
      virtual void process(double time) override;
      virtual void close() override;
    };
  }
}

#endif
