//
//  nobjects.h
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_nobjects_h
#define kmc_nobjects_h

#include "plugin.h"
#include <boost/filesystem.hpp>
#include <fstream>

namespace kmc {
  namespace plugin {
    class NObjects : public Plugin {
    private:
      boost::filesystem::path p_;
      std::ofstream of_;
      kmc::lattice::Lattice* lattice_;
      kmc::lattice::State* state_;
      
    public:
      NObjects(const boost::filesystem::path& p,
               lattice::State* state);
      
      virtual void boot(kmc::lattice::Lattice* lattice) override;
      virtual void process(double time) override;
      virtual void close() override;
    };
  }
}

#endif
