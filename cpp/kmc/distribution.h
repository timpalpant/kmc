//
//  distribution.h
//  kmc
//
//  Collect the one-particle distribution over the course of
//  the simulation
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_distribution_h
#define kmc_distribution_h

#include "plugin.h"
#include <boost/filesystem.hpp>
#include <fstream>

namespace kmc {
  namespace plugin {
    class Distribution : public Plugin {
    private:
      boost::filesystem::path p_;
      std::ofstream of_;
      lattice::Lattice* lattice_;
      lattice::State* state_;
      
      double last_time_ = 0;
      std::vector<lattice::State*> last_state_;
      std::vector<double> dist_;
      
    public:
      Distribution(const boost::filesystem::path& p,
                   lattice::State* state);
      
      virtual void boot(kmc::lattice::Lattice* lattice) override;
      virtual void process(double time) override;
      virtual void close() override;
    };
  }
}

#endif
