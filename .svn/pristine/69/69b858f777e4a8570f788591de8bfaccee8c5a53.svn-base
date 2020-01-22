package it.cnr.speech.performance;

public abstract class AcousticModel {
	
	protected String modelName;
	public AcousticModel(String modelName) {
		this.modelName = modelName;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	abstract public double calcLikelihood(double[][] features);
	
}
