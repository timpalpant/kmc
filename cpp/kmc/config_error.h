//
//  config_error.h
//  kmc
//
//  Created by Timothy Palpant on 4/2/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_config_error_h
#define kmc_config_error_h

namespace kmc {
  class config_error : public std::runtime_error {
  public:
    explicit config_error(const std::string& what_arg)
    : std::runtime_error(what_arg) { }
    explicit config_error(const char* what_arg)
    : std::runtime_error(what_arg) { }
  };
}

#endif
