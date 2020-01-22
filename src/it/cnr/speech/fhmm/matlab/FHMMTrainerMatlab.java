package it.cnr.speech.fhmm.matlab;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.features.MfccExtraction;

public class FHMMTrainerMatlab {

	public static void main(String[] args) throws Exception {

		File trainingFolder = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train\\");
		trainAll(trainingFolder);
	}

	public static void trainAll(File trainingFolder) throws Exception {

		File[] allFolders = trainingFolder.listFiles();

		for (File folder : allFolders) {
			if (folder.isDirectory()) {

				//if (folder.getName().equals("dze"))
				if (MatlabInvoker.isToTrain(folder.getName()))
				{
					System.out.println("Training " + folder);
					String hmmName = folder.getName();
					FHMMTrainerMatlab trainer = new FHMMTrainerMatlab();
					trainer.trainFHMM(folder, hmmName, 50);
				}
				 //break;
			}
		}

		
		for (File folder : allFolders) {
			if (folder.isDirectory()) {

				//if (folder.getName().equals("dze"))
				if (MatlabInvoker.isToTrain(folder.getName()))
				{
					System.out.println("Training " + folder);
					String hmmName = folder.getName();
					FHMMTrainerMatlab trainer = new FHMMTrainerMatlab();
					trainer.trainFHMM(folder, hmmName, 25);
				}
				 //break;
			}
		}
		
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

	
	public void trainFHMM(File filesFolder, String name, int percentile) throws Exception {

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
		for (File f : allfiles) {

			if (f.getName().endsWith(".wav")) {

				double[][] X = extractFeatures(f);

				int nFeatures = X.length;
				lengths.add(nFeatures);
				
				if (minFeatures > nFeatures)
					minFeatures = nFeatures;

				if (maxFeatures < nFeatures)
					maxFeatures = nFeatures;

				if (name.equals("sil") && nRowsTot > 900)
					break;

			}

		}
		
		System.out.println("25th "+percentile(lengths, 25));
		System.out.println("50th "+percentile(lengths, 50));
		System.out.println("70th "+percentile(lengths, 70));
		System.out.println("90th "+percentile(lengths, 90));
		
		System.out.println("Max Features Length " + maxFeatures);
		System.out.println("Max Features Length " + minFeatures);

		int mean = (maxFeatures + minFeatures) / 2;// (int) Math.sqrt(maxFeatures*minFeatures);

		
		int T = 0;
		/*
		if (name.equals("kwa") || name.equals("to") || name.equals("tSen") || name.equals("tSin"))
			T = Math.max(6, percentile(lengths, 50));
		else
			T = Math.max(6, percentile(lengths, 25));
		 */
		
		T = Math.max(6, percentile(lengths, percentile));
		
		System.out.println("Features to take " + T);

		System.out.println("Collecting features");
		int validcounter = 0;
		int counter = 0;
		for (File f : allfiles) {

			if (f.getName().endsWith(".wav")) {
				double[][] X = extractFeatures(f);
				counter++;

				if (X.length < T)
					continue;

				X = Arrays.copyOfRange(X, 0, T);
				allMatrices.add(X);

				nRowsTot += X.length;
				nColsTot = X[0].length;

				validcounter++;
				if (name.equals("sil") && nRowsTot > 900)
					break;
			}

		}

		double nseconds = 0.008 * nRowsTot;
		System.out.println("Collected overall " + nRowsTot + " features = " + nseconds + "s");
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

		/*
		 * FHMM fhmm = new FHMM(allX, nStates, nChains); Hmm hmm = new Hmm(fhmm);
		 * System.out.println("Training FHMM"); BaumWelchLearner bwl = new
		 * BaumWelchLearner(hmm); bwl.setOsservazioni(fhmm.getX(), T, TIndex,
		 * fhmm.getXX()); hmm = bwl.learnAndSave(nOfIterations, name);
		 * 
		 * if (outputFile.exists()) outputFile.delete();
		 * 
		 * ObjectOutputStream oos = new ObjectOutputStream(new
		 * FileOutputStream(outputFile)); oos.writeObject(hmm); oos.close();
		 */

		System.out.println("FHMM saved");

	}

}
