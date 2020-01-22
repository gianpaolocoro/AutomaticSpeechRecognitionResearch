package it.cnr.speech.conversions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.features.MfccExtraction;

public class Conversions {

	public static long millisecondsToSamples(double samplingFrequency,double milliseconds) {
		
		long samples = Math.round(milliseconds*samplingFrequency/1000);
		
		return samples;
		
	} 
	
	public static double samples2Milliseconds(double samplingFrequency,long samples ) {
		
		double milliseconds = (double) (samples/samplingFrequency)*1000d;
		
		return milliseconds;
		
	} 

	
	public static double featureIndex2Milliseconds(int index, double samplingFrequency,int windowSamples) {
		
		double windowSeconds = (double) windowSamples/(2d*samplingFrequency);
				
		return ((double)(index+1)*windowSeconds);
		
	} 
	
	
	public static List<ObservationVector> features2Observations(int from, int to,double[][] X) {
		List<ObservationVector> sequence = new ArrayList<ObservationVector>();

		for (int i = from; i <= to; i++) {
			double vett[] = X[i];
			ObservationVector obs = new ObservationVector(vett);
			sequence.add(obs);
		}

		return sequence;
	}

	
	public static void saveMFCCs(double[][] mfccMatrix, File outputCSVFile) throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputCSVFile));
		int nrows = mfccMatrix.length;

		
		int ncols = mfccMatrix[0].length;
		
		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mfccMatrix[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();
	}
	
	
}
