package it.cnr.speech.fhmm.matlab;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.features.MfccExtraction;

public class FHMMTrainerMatlabFullMatrix {

	public static void main(String[] args) throws Exception {

		File trainingFolder = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train\\");
		trainAll(trainingFolder);
	}

	static double overallseconds = 0;
	public static void trainAll(File trainingFolder) throws Exception {
		
		
		OSCommands.executeCommandForceIS(
				"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\\"*.mat");
		OSCommands.executeCommandForceIS(
				"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\training_\"*");
				
		File[] allFolders = trainingFolder.listFiles();

		for (File folder : allFolders) {
			if (folder.isDirectory()) {
				if (folder.getName().equals("sil"))
					continue;
				//if (folder.getName().equals("dze"))
				if (MatlabInvoker.isToTrain(folder.getName()))
				{
					System.out.println("Training " + folder);
					String hmmName = folder.getName();
					FHMMTrainerMatlabFullMatrix trainer = new FHMMTrainerMatlabFullMatrix();
					trainer.trainFHMM(folder, hmmName);
				}
				 //break;
			}
		}
		
		System.out.println("Overall s : " + overallseconds);
	}

	public double[][] extractFeatures(File waveFile) throws Exception {

		AudioBits audio = new AudioBits(waveFile);
		short[] shortaudio = audio.getShortVectorAudio();
		AudioFormat af = audio.getAudioFormat();

		float sf = af.getSampleRate();
		// System.out.println("Sample Rate: " + sf);
		audio.deallocateAudio();

		// features matrix
		double X[][] = null;
		MfccExtraction mfcc = new MfccExtraction(sf);

		X = mfcc.extractMFCC(shortaudio);

		return X;
	}

	public static int percentile(List<Integer> values, double percentile) {
		Collections.sort(values);
		int index = (int) Math.ceil((percentile / 100) * values.size());
		return values.get(index - 1);
	}

	
	public void trainFHMM(File filesFolder, String name) throws Exception {

		File[] allfiles = filesFolder.listFiles();
		double[][] allX = null;
		List<double[][]> allMatrices = new ArrayList<>();
		int nRowsTot = 0;
		int nColsTot = 0;

		// calculating T
		int minFeatures = Integer.MAX_VALUE;
		int maxFeatures = 0;

		System.out.println("Calculating features");
		List<Integer> lengths = new ArrayList<>();
		int maxSil = 150;
		
		int nFiles = 0;
		for (File f : allfiles) {

			if (f.getName().endsWith(".wav")) {

				double[][] X = extractFeatures(f);

				int nFeatures = X.length;
				lengths.add(nFeatures);
				if (name.equals("sil") && nFiles > maxSil)
					break;

				nFiles++;
			}

		}
		
		Integer [] T = new Integer[lengths.size()];
		T = lengths.toArray(T);
		
		System.out.println("Collecting features");
		int validcounter = 0;
		int counter = 0;
		 nFiles = 0;
		 
		for (File f : allfiles) {

			if (f.getName().endsWith(".wav")) {
				double[][] X = extractFeatures(f);
				counter++;
				allMatrices.add(X);

				nRowsTot += X.length;
				nColsTot = X[0].length;

				validcounter++;
				if (name.equals("sil") && nFiles > maxSil)
					break;

				nFiles++;
			}

		}

		double nseconds = 0.008 * nRowsTot;
		System.out.println("Collected overall " + nRowsTot + " features = " + nseconds + "s");
		overallseconds+=nseconds;
		
		System.out.println("Selected overall " + validcounter + " over " + counter + " files ");

		allX = new double[nRowsTot][nColsTot];

		int i = 0;
		for (double[][] mat : allMatrices) {

			for (int k = 0; k < mat.length; k++) {

				allX[i] = mat[k];
				i++;

			}

		}

		System.out.println("Building FHMM with " + allX.length + " features");

		MatlabInvoker.runFHMMTraining(allX, T, name);

		
		System.out.println("FHMM saved");
		//System.exit(0);
	}

}
