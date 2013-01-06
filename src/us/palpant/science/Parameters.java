package us.palpant.science;

import java.nio.file.Path;

public class Parameters {
	public static int NUC_SIZE = 147;
	public static double BETA = 1;
	public static double V_EFF = 0 * BETA;
	public static double K_ON = 1.0 / 720;
	public static double DIFFUSION = 60.0;
	
	public static int REMODELER_STEP_SIZE = 13;
	public static double K0 = 0.0059;
	public static double A = 0.0911;
	public static int L_MIN = 20;
	public static int L_MAX = 60;
	public static double ATP = 2;
	public static double K_M = 0.11;
	public static boolean LINKER_DEPENDENT_RATE = true;
	
	public static void loadParameters(Path paramFile) {
		
	}
}
