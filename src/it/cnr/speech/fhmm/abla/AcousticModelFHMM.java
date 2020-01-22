package it.cnr.speech.fhmm.abla;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import it.cnr.speech.fhmm.math.Matrix;
import it.cnr.speech.performance.AcousticModel;

public class AcousticModelFHMM  extends AcousticModel{

	Hmm factorial;
	
	public AcousticModelFHMM(String modelName, File binaryHMM) throws Exception{
		super(modelName);
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binaryHMM));
		Object hmmobs = ois.readObject();
		if (hmmobs instanceof Hmm)
			factorial = (Hmm) hmmobs;
		ois.close();
		
	}
	
	@Override
	public double calcLikelihood(double[][] features) {
		CalcLike cl = new CalcLike(new Matrix(features),factorial);
		double likelihood = cl.getLik() + 10000;
		return likelihood;
	}

}
