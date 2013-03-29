//
//  main.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/16/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include <iostream>
#include "ark.h"

int main(int argc, const char * argv[]) {
  if (argc < 2) {
    std::cout << "USAGE: kmc [--include ARK] [--cfg KEY=VALUE]" << std::endl;
    return 2;
  }
  
  //config::Ark ark = config::Ark::fromArgv(argv);
  ark::Ark ark;
}

