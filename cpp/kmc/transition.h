//
//  transition.h
//  kmc
//
//  A Monte Carlo move
//  The Transition has upstream dependencies (conditions)
//  and downstram effects (actions).
//  If all conditions are not met, then the Transition should
//  be marked disabled. If the Transition is not enabled,
//  then its rate = 0.
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_transition_h
#define kmc_transition_h

#include <vector>

#include "contingency.h"

namespace kmc {
  class Transition {
  private:
    std::vector<Condition> conditions_;
    std::vector<Action> actions_;
    double rate_;
    bool enabled_ = false;
    
  public:
    Transition(std::vector<Condition>&& conditions,
               std::vector<Action>&& actions,
               const double rate)
      : conditions_(conditions), actions_(actions), rate_(rate) { }
    
    const std::vector<Condition>& conditions() const { return conditions_; }
    const std::vector<Action>& actions() const { return actions_; }
    double rate() const { return rate_; }
    void set_rate(const double rate) { rate_ = rate; }
    bool enabled() const { return enabled_; }
    void set_enabled(const bool enabled) { enabled_ = enabled; }
  };
}

#endif
