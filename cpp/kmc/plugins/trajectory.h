//
//  trajectory.h
//  kmc
//
//  Created by Timothy Palpant on 3/31/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_trajectory_h
#define kmc_trajectory_h

#include "plugin.h"
#include <boost/filesystem.hpp>
#include <fstream>

namespace kmc {
  namespace plugin {
    class Trajectory : public Plugin {
    private:
      boost::filesystem::path p_;
      std::ofstream of_;
      kmc::lattice::Lattice* lattice_;
      
    public:
      virtual void configure(const boost::property_tree::ptree& pt) override;      
      virtual void boot(kmc::lattice::Lattice* lattice) override;
      virtual void process(double time) override;
      virtual void close() override;
    };
  }
}

#endif
