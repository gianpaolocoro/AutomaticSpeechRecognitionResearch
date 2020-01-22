package it.cnr.speech.hmm;

import java.io.File;

import it.cnr.speech.performance.AcousticModel;

public class AcousticModelHMM extends AcousticModel{
	SingleHMM hmm = null;
	
	public AcousticModelHMM(String modelName, File binaryHMM) throws Exception{
		super(modelName);
		hmm = new SingleHMM(binaryHMM);
	}
	
	
	@Override
	public double calcLikelihood(double[][] features) {
		return hmm.calcLike(features)+10000;
	}

}
