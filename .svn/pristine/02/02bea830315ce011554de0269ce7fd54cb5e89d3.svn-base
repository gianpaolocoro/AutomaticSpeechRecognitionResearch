package it.cnr.speech.recognition;

import java.io.File;
import java.util.Arrays;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.decoding.ExhaustiveDecoder;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.language.LanguageModel;
import it.cnr.speech.language.SimpleLanguageModel2;
import it.cnr.speech.performance.AcousticModel;
import it.cnr.speech.performance.SimpleAM;

public class DummySpeechRecognizer {

	
	public static void main(String[] args) throws Exception{
		File sampleFile = new File("281.wav");
		//LanguageModel lm = new SimpleLanguageModel();
		//LanguageModel lm = new SimpleLanguageModel3();
		LanguageModel lm = new SimpleLanguageModel2();
		
		String[] allWords = lm.getAllWords();
		
		ExhaustiveDecoder decoder = new ExhaustiveDecoder();
		decoder.setBeamRange(0,5);
		System.out.println("Adding lm");
		decoder.addLanguageModel(lm);
		
		for (String word:allWords) {
			System.out.println("Adding word "+word);
			AcousticModel m = new SimpleAM(word);
			decoder.addAcousticModel(m);
		}
		
		System.out.println("extracting features from wave file "+sampleFile.getName());
		AudioBits ab = new AudioBits(sampleFile);
		MfccExtraction extractor = new MfccExtraction(ab.getAudioFormat().getSampleRate());
		short[] signal = ab.getShortVectorAudio();
		double[][] features = extractor.extractMFCC(signal);

		decoder.decode(features);
		double [] seconds = decoder.getAlignment(ab.getAudioFormat().getSampleRate(), extractor.getWindowSamples());
		
		System.out.println("Alignment (s): "+Arrays.toString(seconds));
		
	}
	
	
}
