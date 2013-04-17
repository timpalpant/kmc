//
//  twobody.h
//  kmc
//
//  Created by Timothy Palpant on 4/6/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef __kmc__twobody__
#define __kmc__twobody__

#include "plugin.h"

namespace kmc {
  namespace plugin {
    class TwoBody : public Plugin {
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

#endif /* defined(__kmc__twobody__) */
