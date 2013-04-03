//
//  particle.h
//  kmc
//
//  A particle with a maximum size, that can adsorb,
//  desorb, diffuse, grow, shrink, and hop
//
//  Created by Timothy Palpant on 3/30/13.
//  Copyright (c) 2013 Timothy Palpant. All rights reserved.
//

#ifndef kmc_particle_h
#define kmc_particle_h

#include <boost/property_tree/ptree.hpp>
#include <boost/filesystem.hpp>

#include "lattice.h"
#include "transition.h"
#include "config_error.h"

namespace kmc {
  class Particle;
  
  class ParticleTransition {
  protected:
    double rate_ = 0;
    std::vector<double> potential_;
    
    /**
     * Get the rate for a particle bound from i to j
     */
    double rate(const std::size_t i, 
                const std::size_t j,
                const double beta) const;
    
  public:
    virtual void configure(const boost::property_tree::ptree& pt);
    virtual std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                                 const Particle& p,
                                                 const double beta) = 0;
  };
  
  class AdsorptionTransition : public ParticleTransition {
  public:
    virtual std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                                 const Particle& p,
                                                 const double beta) override;
  };
  
  class DesorptionTransition : public ParticleTransition {
  public:
    virtual std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                                 const Particle& p,
                                                 const double beta) override;
  };
  
  class SlideTransition : public ParticleTransition {
  private:
    int step_;
    
    double rate(const std::size_t i, const std::size_t j,
                const std::size_t i2, const std::size_t j2,
                const double beta) const;
    
  public:
    virtual void configure(const boost::property_tree::ptree& pt) override;
    virtual std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                                 const Particle& p,
                                                 const double temperature) override;
  };
  
  class UnwrapTransition : public ParticleTransition {
  private:
    double rate(const std::size_t i, const double beta) const;
  public:
    virtual std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                                 const Particle& p,
                                                 const double temperature) override;
  };
  
  class particle_error : public config_error {
  public:
    explicit particle_error(const std::string& what_arg)
      : config_error(what_arg) { }
    explicit particle_error(const char* what_arg)
      : config_error(what_arg) { }
  };
  
  class Particle {
  private:
    lattice::State* state_;
    double beta_;
    std::vector<std::shared_ptr<ParticleTransition>> transitions_;
    std::size_t size_;
    bool unwrap_ = false;

  public:
    Particle(lattice::State* state) : state_(state) { }
    void configure(const boost::property_tree::ptree& pt) throw (particle_error);
    std::vector<Transition*> transitions(const lattice::Lattice* lattice,
                                         const double temperature) const;
    
    lattice::State* state() const { return state_; }
    
    lattice::State* state(std::size_t i) const throw (particle_error) {
      if (i < 0 || i >= size()) {
        throw particle_error("Invalid substate exceeds particle size");
      }
      
      return state()->substate(i);
    }
  
    std::string name() const { return state_->name(); }
    std::size_t size() const { return size_; }
    double beta() const { return beta_; }
    bool unwrap() const { return unwrap_; }
  };
}

#endif
