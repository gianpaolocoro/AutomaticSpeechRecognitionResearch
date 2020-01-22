package it.cnr.speech.performance;

public class SimpleAM extends AcousticModel{

	
	
	public SimpleAM(String modelName) {
		super(modelName);

	}

	@Override
	public double calcLikelihood(double[][] features) {
		int nFeatures = features.length;
		double nchar = (modelName.length()*modelName.charAt(0)*modelName.charAt(1));
		return (double) (nFeatures*nchar);
	}


}
