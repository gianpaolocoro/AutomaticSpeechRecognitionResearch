package it.cnr.speech.recognition;

import java.io.File;
import java.util.Arrays;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.decoding.ExhaustiveDecoderParallel;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.fhmm.abla.AcousticModelFHMM;
import it.cnr.speech.language.LanguageModelSphinx;
import it.cnr.speech.performance.AcousticModel;

public class FHMMSpeechRecognizer {

	public static void main(String[] args) throws Exception {
		File sampleFile1 = new File("digits/uno.wav");
		File sampleFile2 = new File("digits/due.wav");
		File sampleFile3 = new File("digits/tre.wav");
		File sampleFile4 = new File("digits/quattro.wav");
		File sampleFile5 = new File("digits/cinque.wav");
		File sampleFile6 = new File("digits/sei.wav");
		File sampleFile7 = new File("digits/sette.wav");
		File sampleFile8 = new File("digits/otto.wav");
		File sampleFile0 = new File("digits/zero.wav");
		
		//File sampleFile = new File("ttro.wav");
		File digitsFolder = new File("digits");
		File allWaveFiles [] = digitsFolder.listFiles();
		
		int correct = 0;
		
		for (File sampleFile : allWaveFiles){
		
		FHMMSpeechRecognizer asr = new FHMMSpeechRecognizer();
		String recognized = asr.recognize(sampleFile);
		if (sampleFile.getName().contains(recognized))
			correct++;
		
			System.out.println("Final recognition for "+sampleFile.getName()+": "+recognized+" : "+sampleFile.getName().contains(recognized));
			
			//break;
		}
		
		System.out.println("Accuracy "+((correct*100d)/allWaveFiles.length));
		//System.out.println(getDigit("sil kwa sil ttro sil"));
		//System.out.println(getDigit("sil kwa ttro sil"));
		
	}
	
	File languagemodel = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\language model\\conndigits.lm");
	File hmmsRepo = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\fhmms_7\\");

	public String recognize(File waveFile) throws Exception{
		
		LanguageModelSphinx lm = new LanguageModelSphinx(languagemodel);
		String[] allWords = lm.getAllWords();
		//ExhaustiveDecoder decoder = new ExhaustiveDecoder();
		ExhaustiveDecoderParallel decoder = new ExhaustiveDecoderParallel();
		decoder.setBeamRange(12,30);//16);
		decoder.setBeamConfidence(0.5);
		
		System.out.println("Adding lm");
		decoder.addLanguageModel(lm);

		for (String word : allWords) {
			if (	
					word.equals("u") || word.equals("no")
					|| word.equals("due")
					|| word.equals("tre")
					|| word.equals("kwa") || word.equals("ttro") 
					|| word.equals("tSin") || word.equals("kwe")
					|| word.equals("sei")
					|| word.equals("se") || word.equals("tte")
					|| word.equals("o") || word.equals("tto")
					|| word.equals("dze") || word.equals("ro")
					//|| word.equals("no") || word.equals("ve")
					|| word.equals("sil") || word.equals("sp")  
					) {
				System.out.println("Adding word " + word);
				File hmmFile = new File(hmmsRepo, word + ".fhmm");
				AcousticModel m = new AcousticModelFHMM(word, hmmFile);
				decoder.addAcousticModel(m);
			}
		}

		System.out.println("extracting features from wave file " + waveFile.getName());
		AudioBits ab = new AudioBits(waveFile);
		MfccExtraction extractor = new MfccExtraction(ab.getAudioFormat().getSampleRate());
		short[] signal = ab.getShortVectorAudio();
		double[][] features = extractor.extractMFCC(signal);

		String recognized = decoder.decode(features);
		double[] seconds = decoder.getAlignment(ab.getAudioFormat().getSampleRate(), extractor.getWindowSamples());

		System.out.println("Alignment (s): " + Arrays.toString(seconds));
		
		//recognized = getDigit(recognized);
		
		return recognized;
	}
	public static String getDigit(String recognized) {
		
		recognized = recognized.replace("sp", "sil");
		
		if (recognized.split("u( sil)* no").length>1)
			return "uno";
		if (recognized.split("o( sil)* tto").length>1)
			return "otto";
		if (recognized.split("o( sil)* ttro").length>1)
			return "otto";
		if (recognized.split("dze( sil)* ro").length>1)
			return "zero";
		if (recognized.split("kwa( sil)* ttro").length>1)
			return "quattro";
		if (recognized.split("tSin( sil)* kwe").length>1)
			return "cinque";
		if (recognized.split("se( sil)* tte").length>1)
			return "sette";
		if (recognized.split("sil due sil").length>1)
			return "due";
		if (recognized.split("sil tre sil").length>1)
			return "tre";
		if (recognized.split("sil sei sil").length>1)
			return "sei";
		if (recognized.split("sil se sil").length>1)
			return "sei";
		if (recognized.split("sil tte sil").length>1)
			return "tre";
		else
			return recognized;
	}
	
	
}
