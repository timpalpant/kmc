//
//  particle.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "particle.h"

#include <boost/lexical_cast.hpp>
#include <fstream>

namespace kmc {
  std::vector<double> load_potential(const boost::filesystem::path& p) {
    std::vector<double> potential;
    std::ifstream f(p.c_str());
    std::string line;
    while (std::getline(f, line)) {
      potential.push_back(boost::lexical_cast<double>(line));
    }
    f.close();
    return potential;
  }
  
  void ParticleTransition::configure(const boost::property_tree::ptree& pt) {
    boost::optional<double> rate = pt.get_optional<double>("rate");
    if (rate) {
      rate_ = rate.get();
    }
    
    boost::optional<boost::filesystem::path> p = pt.get_optional<boost::filesystem::path>("potential");
    if (p) {
      potential_ = load_potential(p.get());
    }
  }
  
  double ParticleTransition::rate(const std::size_t i, const std::size_t j,
                                  const double beta) const {
    if (potential_.size() > 0) {
      double total = 0;
      for (std::size_t k = i; k < j; k++) {
        total += potential_[k];
      }
      return std::exp(-beta*total);
    }
    
    return rate_;
  }
  
  std::vector<Transition*>
  AdsorptionTransition::transitions(const lattice::Lattice* lattice,
                                    const Particle& p,
                                    const double beta) {
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      throw particle_error("Unwrapping is not yet supported");
    } else {
      for (std::size_t i = 0; i < lattice->size()-p.size(); i++) {
        std::vector<Condition> conditions;
        for (std::size_t j = 0; j < p.size(); j++) {
          conditions.push_back(Condition(i+j, lattice::State::EMPTY));
        }
        std::vector<Action> actions;
        actions.push_back(Action(i, p.state()));
        for (std::size_t j = 1; j < p.size(); j++) {
          actions.push_back(Action(i+j, lattice::State::STERIC));
        }
        Transition* t = new Transition(std::move(conditions),
                                       std::move(actions),
                                       rate(i, i+p.size(), beta));
        t->set_name("adsorption-"+boost::lexical_cast<std::string>(i));
        transitions.push_back(t);
      }
    }
    
    return transitions;
  }
  
  std::vector<Transition*>
  DesorptionTransition::transitions(const lattice::Lattice* lattice,
                                    const Particle& p,
                                    const double beta) {
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      throw particle_error("Unwrapping is not yet supported");
    } else {
      for (std::size_t i = 0; i < lattice->size()-p.size(); i++) {
        std::vector<Condition> conditions;
        conditions.push_back(Condition(i, p.state()));
        std::vector<Action> actions;
        for (std::size_t j = 0; j < p.size(); j++) {
          actions.push_back(Action(i+j, lattice::State::EMPTY));
        }
        Transition* t = new Transition(std::move(conditions),
                                       std::move(actions),
                                       rate(i, i+p.size(), beta));
        t->set_name("desorption-"+boost::lexical_cast<std::string>(i));
        transitions.push_back(t);
      }
    }
    
    return transitions;
  }
  
  void SlideTransition::configure(const boost::property_tree::ptree& pt) {
    ParticleTransition::configure(pt);
    step_ = pt.get("step", 1);
  }
  
  double SlideTransition::rate(const std::size_t i, const std::size_t j,
                               const std::size_t i2, const std::size_t j2,
                               const double beta) const {
    double rate1 = ParticleTransition::rate(i, j, beta);
    double rate2 = ParticleTransition::rate(i2, j2, beta);
    double potential1 = -std::log(rate1) / beta;
    double potential2 = -std::log(rate2) / beta;
    return std::exp(-beta*(potential2-potential1));
  }
  
  std::vector<Transition*>
  SlideTransition::transitions(const lattice::Lattice* lattice,
                               const Particle& p,
                               const double beta) {
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      throw particle_error("Unwrapping is not yet supported");
    } else {
      if (step_ > 0) {
        for (std::size_t i = 0; i < lattice->size()-p.size()-step_; i++) {
          std::vector<Condition> conditions;
          conditions.push_back(Condition(i, p.state()));
          for (int j = 0; j < step_; j++) {
            conditions.push_back(Condition(i+p.size()+j, lattice::State::EMPTY));
          }
          std::vector<Action> actions;
          for (int j = 0; j < step_; j++) {
            actions.push_back(Action(i+j, lattice::State::EMPTY));
          }
          actions.push_back(Action(i+step_, p.state()));
          for (int j = 0; j < step_; j++) {
            actions.push_back(Action(i+p.size()+j, lattice::State::STERIC));
          }
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         rate(i, i+p.size(),
                                              i+step_, i+p.size()+step_,
                                              beta));
          t->set_name("slide-"+boost::lexical_cast<std::string>(i));
          transitions.push_back(t);
        }
      } else {
        for (std::size_t i = -step_; i < lattice->size()-p.size(); i++) {
          std::vector<Condition> conditions;
          conditions.push_back(Condition(i, p.state()));
          for (int j = -1; j >= step_; j--) {
            conditions.push_back(Condition(i+j, lattice::State::EMPTY));
          }
          std::vector<Action> actions;
          for (int j = -1; j >= step_; j--) {
            actions.push_back(Action(i+p.size()+j, lattice::State::EMPTY));
          }
          actions.push_back(Action(i+step_, p.state()));
          for (int j = 0; j > step_; j--) {
            actions.push_back(Action(i+j, lattice::State::STERIC));
          }
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         rate(i, i+p.size(),
                                              i+step_, i+p.size()+step_,
                                              beta));
          t->set_name("slide-"+boost::lexical_cast<std::string>(i));
          transitions.push_back(t);
        }
      }
    }
    
    return transitions;
  }
  
  void UnwrapTransition::configure(const boost::property_tree::ptree& pt) {
    
  }
  
  std::vector<Transition*>
  UnwrapTransition::transitions(const lattice::Lattice* lattice,
                                const Particle& p,
                                const double beta) {
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      throw particle_error("Unwrapping is not yet supported");
    }
    
    return transitions;
  }
  
  std::vector<Transition*>
  Particle::transitions(const lattice::Lattice* lattice,
                        const double beta) const {
    std::vector<Transition*> transitions;
    
    for (const std::shared_ptr<ParticleTransition>& pt : transitions_) {
      const std::vector<Transition*>& ts = pt->transitions(lattice, *this, beta);
      transitions.insert(transitions.end(), ts.begin(), ts.end());
    }
    
    return transitions;
  }
  
  void Particle::configure(const boost::property_tree::ptree& pt) throw (particle_error) {
    size_ = pt.get<std::size_t>("size");
    
    const boost::property_tree::ptree& transitions = pt.get_child("transitions");
    for (const std::pair<std::string,boost::property_tree::ptree>& pair : transitions) {
      const boost::property_tree::ptree& tconfig = pair.second;
      const std::string& type = tconfig.get<std::string>("type");
      ParticleTransition* transition;
      if (type == "adsorption") {
        transition = new AdsorptionTransition();
      } else if (type == "desorption") {
        transition = new DesorptionTransition();
      } else if (type == "slide") {
        transition = new SlideTransition();
      } else if (type == "unwrap") {
        unwrap_ = true;
        transition = new UnwrapTransition();
      } else {
        throw particle_error("Unknown particle transition type: "+type);
      }
      
      transition->configure(tconfig);
      transitions_.push_back(std::shared_ptr<ParticleTransition>(transition));
    }
  }
  
  lattice::State* Particle::state(std::size_t i) const {
    std::string state_name = name() + "-" + boost::lexical_cast<std::string>(i);
    return lattice::State::for_name(state_name);
  }
  
  lattice::State* Particle::state() const {
    return lattice::State::for_name(name());
  }
  
}
