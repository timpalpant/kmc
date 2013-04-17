//
//  process.h
//  kmc
//
//  Defines a process that occurs on the lattice
//  Conditions and actions should be in relative coordinates
//  The transition manager will then enumerate all possible
//  transitions for the given lattice
//
//  Created by Timothy Palpant on 4/4/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_process_h
#define kmc_process_h

namespace kmc {
  class Process {
  private:
    std::vector<Condition> conditions_;
    std::vector<Action> actions_;
    std::vector<double> potential_;
    double rate_;
    
  public:
    Process(std::vector<Condition>&& conditions,
            std::vector<Action>&& actions,
            const double rate)
      : conditions_(conditions), actions_(actions), rate_(rate) { }
    
    virtual void configure(const boost::property_tree::ptree& pt) { }
    
    const std::vector<Condition>& conditions() const { return conditions_; }
    const std::vector<Action>& actions() const { return actions_; }
    double rate() const { return rate_; }
    void set_rate(const double rate) { rate_ = rate; }
    const std::vector<double>& potential() const { return potential_; }
    void set_potential(const std::vector<double>& p) { potential_ = p; }
  };
}

#endif
