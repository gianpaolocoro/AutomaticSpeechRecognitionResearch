package it.cnr.speech.fhmm.matlab;

import java.io.File;

import it.cnr.speech.performance.AcousticModel;

public class AcousticModelFHMMMatlab  extends AcousticModel{

	
	public AcousticModelFHMMMatlab(String modelName, File binaryHMM) throws Exception{
		super(modelName);
	}
	
	@Override
	public double calcLikelihood(double[][] features) {

			double like = 0;
			
			try{
				like = MatlabInvoker.runFHMMTest(features, modelName) +10000;
			}catch(Exception e) {
				
			}
			return like;
	}

}
