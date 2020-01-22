package it.cnr.speech.fhmm.abla;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.features.MfccExtraction;

public class FHMMTrainer {
	
	
	//public int nStates = 7;
	public int nStates = 7;
	public int nChains = 2;
	public int nOfIterations = 1000;
	
	public static void main(String[] args) throws Exception{
		
		File outputFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\fhmms_7\\");
		File trainingFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train\\");
		trainAll(trainingFolder,outputFolder);
	}
	
	
	public static void trainAll(File trainingFolder, File outputFolder) throws Exception{
		
		File [] allFolders = trainingFolder.listFiles();
		boolean skip = false;
		//true;
		for (File folder:allFolders){
			if (folder.isDirectory()){
				//if (folder.getName().equals("u"))
					//skip = false;
				
				//if (folder.getName().equals("sil"))
					//continue;
				
				if (!skip) {	
				System.out.println("Training "+folder);
				String hmmName = folder.getName()+".fhmm";
				File hmmOut = new File(outputFolder,hmmName);
				if (!hmmOut.exists()) {
					
					FHMMTrainer trainer = new FHMMTrainer();
					trainer.trainFHMM(folder, folder.getName(), hmmOut);
					//break;
					}
				}
				
			}
		}
		
	}
	public void trainFHMM(File filesFolder, String name, File outputFile) throws Exception{
		
		File [] allfiles = filesFolder.listFiles();
		double [][] allX = null;
		List<double [][]> allMatrices = new ArrayList<>();
		int nRowsTot = 0;
		int nColsTot = 0;
		List<Integer> Tarr = new ArrayList<>();
		List<Integer> TindexArr = new ArrayList<>();
		System.out.println("Collecting features");
		for (File f:allfiles) {
			
			if (f.getName().endsWith(".wav")) {
				double [][] X = extractFeatures(f);
				allMatrices.add(X);
				nRowsTot+=X.length;
				nColsTot = X[0].length;
				Tarr.add(X.length);
				TindexArr.add(nRowsTot);
				
				if (name.equals("sil")&& nRowsTot>900)
					break;
			}
			
		}
		
		double nseconds = 0.008*nRowsTot;
		System.out.println("Collected overall "+nRowsTot+" features = "+nseconds+"s");
		allX = new double [nRowsTot][nColsTot];
		
		int i = 0;
		for (double[][] mat:allMatrices) {
			
			for (int k=0;k<mat.length;k++) {
				
				allX[i] = mat[k];
				i++;
				
			}
			
		}
		
		int[] T = new int[Tarr.size()];
		int[] TIndex = new int[TindexArr.size()];
		
		int counter = 0 ;
		for (Integer t:Tarr) {
			T [counter] = t;
			counter++;
		}

		counter = 0 ;
		for (Integer t:TindexArr) {
			TIndex [counter] = t;
			counter++;
		}

		System.out.println("Building FHMM");
		FHMM fhmm = new FHMM(allX,nStates,nChains);	
		Hmm hmm = new Hmm(fhmm);
		System.out.println("Training FHMM");
		BaumWelchLearner bwl = new BaumWelchLearner(hmm);
		bwl.setOsservazioni(fhmm.getX(), T, TIndex, fhmm.getXX());
		hmm = bwl.learnAndSave(nOfIterations,name);
		
		if (outputFile.exists())
			outputFile.delete();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
		oos.writeObject(hmm);
		oos.close();
		System.out.println("FHMM saved");
		
		
	}
	
	public double[][] extractFeatures(File waveFile) throws Exception{
		
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
	
	
	
}
