//
//  particle.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "particle.h"

#include <boost/lexical_cast.hpp>

namespace kmc {
  Particle::Particle(lattice::State* state) : state_(state) { }

  void Particle::configure(const boost::property_tree::ptree& pt) {
    size_ = pt.get<std::size_t>("size");
  }
  
  std::vector<Transition*> 
  Particle::adsorption_transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;
    
    for (std::size_t i = 0; i < lattice->size()-size(); i++) {
      std::vector<Condition> conditions;
      for (std::size_t j = 0; j < size(); j++) {
        conditions.push_back(Condition(i+j, lattice::State::EMPTY));
      }
      std::vector<Action> actions;
      actions.push_back(Action(i, state_));
      for (std::size_t j = 1; j < size(); j++) {
        actions.push_back(Action(i+j, lattice::State::STERIC));
      }
      Transition* t = new Transition(std::move(conditions),
                                     std::move(actions),
                                     adsorption_rate(i,i+size()));
      t->set_name("adsorption-"+boost::lexical_cast<std::string>(i));
      transitions.push_back(t);
    }
    
    return transitions;
  }
  
  std::vector<Transition*> 
  Particle::desorption_transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;
    
    for (std::size_t i = 0; i < lattice->size()-size(); i++) {
      std::vector<Condition> conditions;
      conditions.push_back(Condition(i, state_));
      std::vector<Action> actions;
      for (std::size_t j = 0; j < size(); j++) {
        actions.push_back(Action(i+j, lattice::State::EMPTY));
      }
      Transition* t = new Transition(std::move(conditions),
                                     std::move(actions),
                                     desorption_rate(i,i+size()));
      t->set_name("desorption-"+boost::lexical_cast<std::string>(i));
      transitions.push_back(t);
    }
    
    return transitions;
  }
  
  std::vector<Transition*> 
  Particle::diffusion_transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;
    
    // Diffusion to the right
    for (std::size_t i = 0; i < lattice->size()-size()-1; i++) {
      std::vector<Condition> conditions;
      conditions.push_back(Condition(i, state_));
      conditions.push_back(Condition(i+size()+1, lattice::State::EMPTY));
      std::vector<Action> actions;
      actions.push_back(Action(i, lattice::State::EMPTY));
      actions.push_back(Action(i+1, state_));
      actions.push_back(Action(i+size(), lattice::State::STERIC));
      Transition* t = new Transition(std::move(conditions),
                                     std::move(actions),
                                     diffusion_rate(i,i+size()));
      t->set_name("rdiffusion-"+boost::lexical_cast<std::string>(i));
      transitions.push_back(t);
    }
    
    // Diffusion to the left
    for (std::size_t i = 1; i < lattice->size()-size(); i++) {
      std::vector<Condition> conditions;
      conditions.push_back(Condition(i, state_));
      conditions.push_back(Condition(i-1, lattice::State::EMPTY));
      std::vector<Action> actions;
      actions.push_back(Action(i, lattice::State::STERIC));
      actions.push_back(Action(i-1, state_));
      actions.push_back(Action(i+size()-1, lattice::State::EMPTY));
      Transition* t = new Transition(std::move(conditions),
                                     std::move(actions),
                                     diffusion_rate(i,i+size()));
      t->set_name("ldiffusion-"+boost::lexical_cast<std::string>(i));
      transitions.push_back(t);
    }
    
    return transitions;
  }

  std::vector<Transition*> 
  Particle::unwrapping_transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;

    return transitions;
  }

  std::vector<Transition*> 
  Particle::hopping_transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;

    return transitions;
  }
  
  std::vector<Transition*> 
  Particle::transitions(const lattice::Lattice* lattice) const {
    std::vector<Transition*> transitions;
    
    const std::vector<Transition*>& ads = adsorption_transitions(lattice);
    transitions.insert(transitions.end(), ads.begin(), ads.end());
    
    const std::vector<Transition*>& des = desorption_transitions(lattice);
    transitions.insert(transitions.end(), des.begin(), des.end());
    
    const std::vector<Transition*>& diff = diffusion_transitions(lattice);
    transitions.insert(transitions.end(), diff.begin(), diff.end());
    
    const std::vector<Transition*>& unwrap = unwrapping_transitions(lattice);
    transitions.insert(transitions.end(), unwrap.begin(), unwrap.end());

    const std::vector<Transition*>& hop = hopping_transitions(lattice);
    transitions.insert(transitions.end(), hop.begin(), hop.end());

    return transitions;
  }
  
}
