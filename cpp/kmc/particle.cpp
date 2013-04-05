//
//  particle.cpp
//  kmc
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#include "particle.h"

#include <boost/lexical_cast.hpp>

#include <iostream>
#include <fstream>

namespace kmc {
  std::vector<double> load_potential(const boost::filesystem::path& p) {
    std::cout << "Loading potential from: " << p << std::endl;
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
    rate_ = pt.get("rate", 0.0);
  }
  
  double AdsorptionTransition::rate(const std::size_t i, const std::size_t j,
                                    const std::size_t size, const double beta) const {
    return rate_;
  }
  
  std::vector<Transition*>
  AdsorptionTransition::transitions(const lattice::Lattice* lattice,
                                    const Particle& p,
                                    const double beta) {
    std::cout << "Enumerating adsorption transitions" << std::endl;
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      // An absorption transition for each possible unwrapped fraction
      for (std::size_t j = 0; j < p.size(); j++) {
        for (std::size_t k = j+1; k <= p.size(); k++) {
          std::size_t size = k - j;
          for (std::size_t i = 0; i < lattice->size()-size+1; i++) {
            std::vector<Condition> conditions;
            std::vector<Action> actions;
            for (std::size_t l = 0; l < size; l++) {
              // Has to be empty at each position in the footprint
              conditions.push_back(Condition(i+l, lattice::State::EMPTY));
              // Will adopt particle state
              actions.push_back(Action(i+l, p.state(j+l)));
            }
            Transition* t = new Transition(std::move(conditions),
                                           std::move(actions),
                                           rate(i, i+size, p.size(), beta));
            transitions.push_back(t);
            if (transitions.size() % 100000 == 0) {
              std::cout << transitions.size() << std::endl;
            }
          }
        }
      }
    } else {
      for (std::size_t i = 0; i < lattice->size()-p.size(); i++) {
        std::vector<Condition> conditions;
        for (std::size_t j = 0; j < p.size(); j++) {
          // Has to be empty at each position in the footprint
          conditions.push_back(Condition(i+j, lattice::State::EMPTY));
        }
        std::vector<Action> actions;
        // Will adopt particle state at the first base pair
        actions.push_back(Action(i, p.state()));
        for (std::size_t j = 1; j < p.size(); j++) {
          // and steric for the rest of the footprint
          actions.push_back(Action(i+j, lattice::State::STERIC));
        }
        Transition* t = new Transition(std::move(conditions),
                                       std::move(actions),
                                       rate(i, i+p.size(), p.size(), beta));
        //t->set_name(p.name()+"-adsorption-"+boost::lexical_cast<std::string>(i));
        transitions.push_back(t);
      }
    }
    
    std::cout << "Initialized " << transitions.size() << " adsorption transitions" << std::endl;
    return transitions;
  }

  double DesorptionTransition::rate(const std::size_t i,
                                    const std::size_t j,
                                    const std::size_t size,
                                    const double beta) const {
    if (potential_.size() > 0) {
      double total = 0;
      for (std::size_t k = i; k < j; k++) {
        total += potential_[i];
      }
      return rate_*std::exp(beta*total);
    }

    return std::pow(rate_, double(size-(j-i)+1)/size);
  }
  
  std::vector<Transition*>
  DesorptionTransition::transitions(const lattice::Lattice* lattice,
                                    const Particle& p,
                                    const double beta) {
    std::cout << "Enumerating desorption transitions" << std::endl;
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      // A desorption transition for each possible unwrapped fraction
      // There is one desorption transition for each adsorption transition
      for (std::size_t j = 0; j < p.size(); j++) {
        for (std::size_t k = j+1; k <= p.size(); k++) {
          std::size_t size = k - j;
          for (std::size_t i = 0; i < lattice->size()-size+1; i++) {
            std::vector<Condition> conditions;
            // Don't desorb a partial particle in the middle of a larger particle
            // Has to be unbound to the left
            if (j > 0 && i > 0) { 
              conditions.push_back(Condition(i-1, p.state(j-1), false));
            }
            // Has to be unbound to the right
            if (k < p.size() && i+size < lattice->size()) { 
              conditions.push_back(Condition(i+size, p.state(k), false));
            }
            std::vector<Action> actions;
            for (std::size_t l = 0; l < size; l++) {
              // Has to be bound at each site in the footprint
              conditions.push_back(Condition(i+l, p.state(j+l)));
              // Will be empty after desorption
              actions.push_back(Action(i+l, lattice::State::EMPTY));
            }
            Transition* t = new Transition(std::move(conditions),
                                           std::move(actions),
                                           rate(i, i+size, p.size(), beta));
            transitions.push_back(t);
            if (transitions.size() % 100000 == 0) {
              std::cout << transitions.size() << std::endl;
            }
          }
        }
      }
    } else {
      for (std::size_t i = 0; i < lattice->size()-p.size(); i++) {
        std::vector<Condition> conditions;
        // Has to be bound starting at i
        conditions.push_back(Condition(i, p.state()));
        std::vector<Action> actions;
        for (std::size_t j = 0; j < p.size(); j++) {
          // Will be empty at each site in the (former) footprint
          actions.push_back(Action(i+j, lattice::State::EMPTY));
        }
        Transition* t = new Transition(std::move(conditions),
                                       std::move(actions),
                                       rate(i, i+p.size(), p.size(), beta));
        //t->set_name(p.name()+"-desorption-"+boost::lexical_cast<std::string>(i));
        transitions.push_back(t);
      }
    }
    
    std::cout << "Initialized " << transitions.size() << " desorption transitions" << std::endl;
    return transitions;
  }
  
  void SlideTransition::configure(const boost::property_tree::ptree& pt) {
    ParticleTransition::configure(pt);
    step_ = pt.get("step", 1);
  }
  
  double SlideTransition::rate(const std::size_t i, const std::size_t j,
                               const std::size_t i2, const std::size_t j2,
                               const std::size_t size, const double beta) const {
    // If we are on a potential, then the rate
    // is determined by the difference in energies
    // between (i,j) and (i2,j2)
    if (potential_.size() > 0) {
      double v1 = 0;
      for (std::size_t k = i; k < j; k++) {
        v1 += potential_[k];
      }
      double v2 = 0;
      for (std::size_t k = i2; k < j2; k++) {
        v2 += potential_[k];
      }
      return rate_*std::exp(beta*(v1-v2)/2);
    }

    return std::pow(rate_, double((j2-i2)-(j-i))/size);
  }
  
  std::vector<Transition*>
  SlideTransition::transitions(const lattice::Lattice* lattice,
                               const Particle& p,
                               const double beta) {
    std::cout << "Enumerating slide (" << step_ << ") transitions" << std::endl;
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      // Slide to the right
      if (step_ > 0) {
        for (std::size_t j = 0; j < p.size(); j++) {
          for (std::size_t k = j+1; k < p.size(); k++) {
            std::size_t size = k - j;
            for (std::size_t i = 0; i < lattice->size()-size-step_; i++) {
              std::vector<Condition> conditions;
              // Don't slide a partial particle in the middle of a larger particle
              // Has to be unbound to the left
              if (j > 0 && i > 0) { 
                conditions.push_back(Condition(i-1, p.state(j-1), false));
              }
              // Has to be bound at each site in the footprint
              for (std::size_t l = 0; l < size; l++) {
                conditions.push_back(Condition(i+l, p.state(j+l)));
              }
              // Has to be empty to the right as far as we are sliding
              for (int l = 0; l < step_; l++) {
                conditions.push_back(Condition(i+size+l, lattice::State::EMPTY));
              }
              
              std::vector<Action> actions;
              // Will be empty where we used to be
              for (int l = 0; l < step_; l++) {
                actions.push_back(Action(i+l, lattice::State::EMPTY));
              }
              // Will now occupy a new site i+step_ to i+size+step_
              for (int l = 0; l < size; l++) {
                actions.push_back(Action(i+l+step_, p.state(j+l)));
              }
          
              Transition* t = new Transition(std::move(conditions),
                                             std::move(actions),
                                             rate(i, i+size,
                                                  i+step_, i+size+step_,
                                                  p.size(), beta));
              transitions.push_back(t);
              if (transitions.size() % 100000 == 0) {
                std::cout << transitions.size() << std::endl;
              }
            }
          }
        }
      // Slide to the left
      } else {
        for (std::size_t j = 0; j < p.size(); j++) {
          for (std::size_t k = j+1; k < p.size(); k++) {
            std::size_t size = k - j;
            for (std::size_t i = -step_; i < lattice->size()-size; i++) {
              std::vector<Condition> conditions;
              // Don't slide a partial particle in the middle of a larger particle
              // Has to be unbound to the right
              if (k+1 < p.size() && i+size < lattice->size()) { 
            	conditions.push_back(Condition(i+size, p.state(k+1), false));
              }
              // Has to be bound at each site in the footprint
              for (std::size_t l = 0; l < size; l++) {
                conditions.push_back(Condition(i+l, p.state(j+l)));
              }
              // Has to be empty to the left as far as we are sliding
              for (int l = -1; l >= step_; l--) {
                conditions.push_back(Condition(i+l, lattice::State::EMPTY));
              }
              
              std::vector<Action> actions;
              // Will be empty where we used to be
              for (int l = -1; l >= step_; l--) {
                actions.push_back(Action(i+size+l, lattice::State::EMPTY));
              }
              // Will now occupy a new site i+step_ to i+size+step_
              for (int l = 0; l < size; l++) {
                actions.push_back(Action(i+l+step_, p.state(j+l)));
              }
              
              Transition* t = new Transition(std::move(conditions),
                                             std::move(actions),
                                             rate(i, i+size,
                                                  i+step_, i+size+step_,
                                                  p.size(), beta));
              transitions.push_back(t);
              if (transitions.size() % 100000 == 0) {
                std::cout << transitions.size() << std::endl;
              }
            }
          }
        }
      }
    } else {
      // Slide to the right
      if (step_ > 0) {
        for (std::size_t i = 0; i < lattice->size()-p.size()-step_; i++) {
          std::vector<Condition> conditions;
          // Has to be bound at i
          conditions.push_back(Condition(i, p.state()));
          // Has to be free as far as we are going to slide
          for (int j = 0; j < step_; j++) {
            conditions.push_back(Condition(i+p.size()+j, lattice::State::EMPTY));
          }
          std::vector<Action> actions;
          // Will be empty where we used to be
          for (int j = 0; j < step_; j++) {
            actions.push_back(Action(i+j, lattice::State::EMPTY));
          }
          // Will now be positioned at i + step_
          actions.push_back(Action(i+step_, p.state()));
          // Steric hindrance will extend as far as we stepped
          for (int j = 0; j < step_; j++) {
            actions.push_back(Action(i+p.size()+j, lattice::State::STERIC));
          }
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         rate(i, i+p.size(),
                                              i+step_, i+p.size()+step_,
                                              p.size(), beta));
          //std::string from = boost::lexical_cast<std::string>(i);
          //std::string to = boost::lexical_cast<std::string>(i+step_);
          //t->set_name(p.name()+"-slide-"+from+"-"+to);
          transitions.push_back(t);
        }
      // Slide to the left
      } else {
        for (std::size_t i = -step_; i < lattice->size()-p.size(); i++) {
          std::vector<Condition> conditions;
          // Has to be bound at i
          conditions.push_back(Condition(i, p.state()));
          // Has to be free as far as we are going to slide
          for (int j = -1; j >= step_; j--) {
            conditions.push_back(Condition(i+j, lattice::State::EMPTY));
          }
          std::vector<Action> actions;
          // Will be empty where we used to be
          for (int j = -1; j >= step_; j--) {
            actions.push_back(Action(i+p.size()+j, lattice::State::EMPTY));
          }
          // Will now be positioned at i + step_
          actions.push_back(Action(i+step_, p.state()));
          // Steric hindrance will extend as far as we stepped
          for (int j = 0; j > step_; j--) {
            actions.push_back(Action(i+j, lattice::State::STERIC));
          }
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         rate(i, i+p.size(),
                                              i+step_, i+p.size()+step_,
                                              p.size(), beta));
          //std::string from = boost::lexical_cast<std::string>(i);
          //std::string to = boost::lexical_cast<std::string>(i+step_);
          //t->set_name(p.name()+"-slide-"+from+"-"+to);
          transitions.push_back(t);
        }
      }
    }
    
    std::cout << "Initialized " << transitions.size() << " slide ("
      << step_ << ") transitions" << std::endl;
    return transitions;
  }

  double UnwrapTransition::wrap_rate(const std::size_t i,
                                     const std::size_t size,
                                     const double beta) const {
    return rate_;
  }
  
  double UnwrapTransition::unwrap_rate(const std::size_t i, 
                                       const std::size_t size,
                                       const double beta) const {
    if (potential_.size() > 0) {
      return rate_*std::exp(beta*potential_[i]);
    }

    return std::pow(rate_, 1.0/size);
  }
  
  std::vector<Transition*>
  UnwrapTransition::transitions(const lattice::Lattice* lattice,
                                const Particle& p,
                                const double beta) {
    std::cout << "Enumerating unwrapping transitions" << std::endl;
    std::vector<Transition*> transitions;
    
    if (p.unwrap()) {
      // For each possible bound state, unwrap on the left end
      // Skip the case where a particle completely unwraps
      // since this is a desorption
      for (std::size_t j = 0; j < p.size()-1; j++) {
        for (std::size_t i = 0; i < lattice->size()-1; i++) {
          std::vector<Condition> conditions;
          // Has to be unbound to the left
          if (j > 0 && i > 0) {
            conditions.push_back(Condition(i-1, p.state(j-1), false));
          }
          // Has to have state j bound at i
          conditions.push_back(Condition(i, p.state(j)));
          // Has to have at least one contact bound to the right
          conditions.push_back(Condition(i+1, p.state(j+1)));

          std::vector<Action> actions;
          // Will be unbound at i (formerly the leftmost contact)
          actions.push_back(Action(i, lattice::State::EMPTY));
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         unwrap_rate(j, p.size(), beta));
          transitions.push_back(t);
        }
      }
      
      // For each possible bound state, unwrap on the right end
      // Skip the case where a particle completely unwraps
      // since this is a desorption
      for (std::size_t j = 1; j < p.size(); j++) {
        for (std::size_t i = 1; i < lattice->size(); i++) {
          std::vector<Condition> conditions;
          // Has to be unbound to the right
          if (j+1 < p.size() && i+1 < lattice->size()) {
            conditions.push_back(Condition(i+1, p.state(j+1), false));
          }
          // Has to have state j bound at i
          conditions.push_back(Condition(i, p.state(j)));
          // Has to have at least one contact bound to the left
          conditions.push_back(Condition(i-1, p.state(j-1)));

          std::vector<Action> actions;
          // Will be unbound at i (formerly the rightmost contact)
          actions.push_back(Action(i, lattice::State::EMPTY));
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         unwrap_rate(j, p.size(), beta));
          transitions.push_back(t);
        }
      }
      
      // For each possible bound state, wrap on the left end
      // Skip the case where a particle wraps its first base
      // since this is an adsorption
      for (std::size_t j = 1; j < p.size(); j++) {
        for (std::size_t i = 1; i < lattice->size(); i++) {
          std::vector<Condition> conditions;
          // Has to be free to the left
          conditions.push_back(Condition(i-1, lattice::State::EMPTY));
          // Has to have state j bound at i
          conditions.push_back(Condition(i, p.state(j)));

          std::vector<Action> actions;
          // Will be bound at i-1 with state j-1 (wrap)
          actions.push_back(Action(i-1, p.state(j-1)));
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         wrap_rate(j-1, p.size(), beta));
          transitions.push_back(t);
        }
      }
      
      // For each possible bound state, wrap on the right end
      // Skip the case where a particle wraps its first base
      // since this is an adsorption
      for (std::size_t j = 0; j < p.size()-1; j++) {
        for (std::size_t i = 0; i < lattice->size()-1; i++) {
          std::vector<Condition> conditions;
          // Has to be free to the right
          conditions.push_back(Condition(i+1, lattice::State::EMPTY));
          // Has to have state j bound at i
          conditions.push_back(Condition(i, p.state(j)));

          std::vector<Action> actions;
          // Will be bound at i+1 with state j+1 (wrap)
          actions.push_back(Action(i+1, p.state(j+1)));
          Transition* t = new Transition(std::move(conditions),
                                         std::move(actions),
                                         wrap_rate(j+1, p.size(), beta));
          transitions.push_back(t);
        }
      }
    }
    
    std::cout << "Initialized " << transitions.size() 
      << " unwrapping transitions" << std::endl;
    return transitions;
  }
  
  std::vector<Transition*>
  Particle::transitions(const lattice::Lattice* lattice,
                        const double beta) const {
    std::vector<Transition*> transitions;
    
    for (ParticleTransition* pt : transitions_) {
      const std::vector<Transition*>& ts = pt->transitions(lattice, *this, beta);
      transitions.insert(transitions.end(), ts.begin(), ts.end());
    }
    
    return transitions;
  }
  
  void Particle::configure(const boost::property_tree::ptree& pt) throw (particle_error) {
    size_ = pt.get<std::size_t>("size");
    std::cout << "Particle has size = " << size_ << std::endl;
    for (std::size_t i = 0; i < size_; i++) {
      substates_.push_back(state_->substate(i));
    }

    boost::optional<boost::filesystem::path> p;
    p = pt.get_optional<boost::filesystem::path>("potential");
    if (p) {
      potential_ = load_potential(p.get());
    }

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
      
      transition->set_potential(potential_);
      transition->configure(tconfig);
      transitions_.push_back(transition);
    }
  }
  
}
