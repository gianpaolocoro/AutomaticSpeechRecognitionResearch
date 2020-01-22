package it.cnr.speech.hmm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;

public class ModelsTraining {
	
	private static int defaultNofStates = 2;
	
	public static void main(String[] args) throws Exception{
		
		File outputFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\");
		File trainingFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train\\");
		trainAll(trainingFolder,outputFolder);
	}

	public static List<ObservationVector> getObservations(File waveFile) throws Exception {

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
		List<ObservationVector> oseq = Conversions.features2Observations(0, X.length - 1, X);
		return oseq;
	}
	
	public static void trainHMM(int numberOfStates, File waveFilesFolder, File outputFile) throws Exception {
		
		System.out.println("Collecting features for HMM "+outputFile.getName());
		List<List<ObservationVector>> allObs = new ArrayList<List<ObservationVector>>();
		File[] waveFiles = waveFilesFolder.listFiles();
		for (File waveFile : waveFiles) {
			if (waveFile.getName().endsWith(".wav")) {
				try{
				List<ObservationVector> oseq = getObservations(waveFile);
				allObs.add(oseq);
				}catch(Exception e){
					System.out.println("Error: skipping "+waveFile+" - "+e.getMessage());
				}
			}
		}
		
		System.out.println("Training HMM "+outputFile.getName()+" with "+allObs.size()+" sets of features");
		int numFeats = allObs.get(0).get(0).dimension();

		Hmm<ObservationVector> hmm = new Hmm<ObservationVector>(numberOfStates, new OpdfMultiGaussianFactory(numFeats));
		KMeansLearner<ObservationVector> bwl = new KMeansLearner<ObservationVector>(numberOfStates, new OpdfMultiGaussianFactory(numFeats), allObs);
		hmm = bwl.learn();

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
		oos.writeObject(hmm);
		oos.close();
		System.out.println("HMM saved");

	}
	
	
	public static void trainAll(File trainingFolder, File outputFolder) throws Exception{
		
		File [] allFolders = trainingFolder.listFiles();
		for (File folder:allFolders){
			if (folder.isDirectory()){
				System.out.println();
				String hmmName = folder.getName()+".hmm";
				trainHMM(defaultNofStates, folder, new File(outputFolder,hmmName));
			}
		}
		
	}
	
	
}
