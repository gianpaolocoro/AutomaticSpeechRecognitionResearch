package it.cnr.speech.hmm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;

public class SingleHMM {

	public Hmm<ObservationVector> hmm = null;

	public SingleHMM(File binaryHMM) throws Exception {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binaryHMM));
		Object hmmobs = ois.readObject();
		if (hmmobs instanceof Hmm)
			hmm = (Hmm<ObservationVector>) hmmobs;
		ois.close();
	}

	public double calcLike(double[][] X) {

		List<ObservationVector> oseq = Conversions.features2Observations(0, X.length - 1, X);

		// apply Viterbi
		double like = hmm.lnProbability(oseq);

		return like;
	}

	public double calcLikeWave(File waveFile) throws Exception {

		
		AudioBits audio = new AudioBits(waveFile);
		short[] shortaudio = audio.getShortVectorAudio();
		AudioFormat af = audio.getAudioFormat();

		float sf = af.getSampleRate();
		audio.deallocateAudio();
		// features matrix
		double X[][] = null;
		MfccExtraction mfcc = new MfccExtraction(sf);
		X = mfcc.extractMFCC(shortaudio);
		return calcLike(X);
	}

	
	public static void main(String[] args) throws Exception{
		
		File test = new File("281.wav");
		File binaryHMM = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\di.hmm");
		File binaryHMM2 = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\due.hmm");
		SingleHMM hmm = new SingleHMM(binaryHMM);
		System.out.println(hmm.calcLikeWave(test));
		SingleHMM hmm2 = new SingleHMM(binaryHMM2);
		System.out.println(hmm2.calcLikeWave(test));
	}
	
}
