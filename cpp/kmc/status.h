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
      unsigned long long interval_;
      unsigned long long nsteps_ = 0;
      
    public:
      Status(unsigned long long interval);
      
      virtual void process(double time) override;
      virtual void close() override;
    };
  }
}

#endif
