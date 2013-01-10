package us.palpant.science;

import com.beust.jcommander.Parameter;

public class Parameters {
	// Statistical positioning parameters
	@Parameter(names = { "--nuc-size" }, description = "Nucleosome size (bp)")
	private int nucSize = 147;
	@Parameter(names = { "--beta" }, description = "Thermodynamic beta (inverse temperature)")
	private double beta = 1;
	@Parameter(names = { "--k-on" }, description = "Nucleosome adsorption rate (min^-1)")
	private double kOn = 0.2;
	@Parameter(names = { "--diffusion" }, description = "Thermal diffusion rate (bp^2/min)")
	private double diffusion = 60.0;

	// Remodeler parameters
	@Parameter(names = { "--step-size" }, description = "Remodeler step size (bp)")
	private int remodelerStepSize = 13;
	@Parameter(names = { "--k0" }, description = "Remodeler base rate (min^-1)")
	public double k0 = 0.0059 * 2 / 2.11;
	@Parameter(names = { "--linker-dependent" }, description = "Use linker-dependent remodeler rate")
	private boolean linkerDependentRate = true;
	@Parameter(names = { "--a" }, description = "Remodeler exponential linker constant")
	private double a = 0.0911;
	@Parameter(names = { "--lmin" }, description = "Minimum linker for remodeler (bp)")
	private int lMin = 20;
	@Parameter(names = { "--lmax" }, description = "Maximum linker for remodeler (bp)")
	private int lMax = 60;

	public String toString() {
	  StringBuilder s = new StringBuilder();
	  s.append("nucleosome size = " + nucSize);
	  s.append(", beta = " + beta);
	  s.append(", k_on = " + kOn);
	  s.append(", diffusion = " + diffusion);
	  s.append(", remodeler step size = " + remodelerStepSize);
	  s.append(", k0 = " + k0);
	  s.append(", use linker-dependent rate = " + linkerDependentRate);
	  s.append(", a = " + a);
	  s.append(", l_min = " + lMin);
	  s.append(", l_max = " + lMax);
	  return s.toString();
	}
	
	public int getNucSize() {
		return nucSize;
	}

	public void setNucSize(int nucSize) {
		this.nucSize = nucSize;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public double getKOn() {
		return kOn;
	}

	public void setKOn(double kOn) {
		this.kOn = kOn;
	}

	public double getDiffusion() {
		return diffusion;
	}

	public void setDiffusion(double diffusion) {
		this.diffusion = diffusion;
	}

	public int getRemodelerStepSize() {
		return remodelerStepSize;
	}

	public void setRemodelerStepSize(int remodelerStepSize) {
		this.remodelerStepSize = remodelerStepSize;
	}

	public double getK0() {
		return k0;
	}

	public void setK0(double k0) {
		this.k0 = k0;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public int getLMin() {
		return lMin;
	}

	public void setLMin(int lMin) {
		this.lMin = lMin;
	}

	public int getLMax() {
		return lMax;
	}

	public void setLMax(int lMax) {
		this.lMax = lMax;
	}

	public boolean useLinkerDependentRate() {
		return linkerDependentRate;
	}

	public void setLinkerDependentRate(boolean linkerDependentRate) {
		this.linkerDependentRate = linkerDependentRate;
	}

}
