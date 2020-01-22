package it.cnr.speech.recognition;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.decoding.ExhaustiveDecoderParallel;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.hmm.AcousticModelHMM;
import it.cnr.speech.language.LanguageModelSphinx;
import it.cnr.speech.performance.AcousticModel;

public class HMMSpeechRecognizerNumbers {

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

		// File sampleFile = new File("ttro.wav");
		File digitsFolder = new File("digits");
		// File allWaveFiles [] = digitsFolder.listFiles();
		// File allWaveFiles [] = {new File("D:\\WorkFolder\\Experiments\\Speech
		// Recognition Speecon\\Speecon connected digits\\test_4_CNR\\SA005CN1.wav")};

		File allWaveFiles[] = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test_4_CNR\\")
						.listFiles();

		int correct = 0;

		for (File sampleFile : allWaveFiles) {

			if (sampleFile.getName().endsWith(".wav")) {
				HMMSpeechRecognizerNumbers asr = new HMMSpeechRecognizerNumbers();
				String recognized = asr.recognize(sampleFile);
				File output = new File(sampleFile.getAbsolutePath().replace(".wav", "_hmm_transcr.txt"));
				FileWriter fw = new FileWriter(output);
				fw.write(recognized);
				fw.close();
			}
			// System.out.println("Accuracy "+((correct*100d)/allWaveFiles.length));
			// System.out.println(getDigit("sil kwa sil ttro sil"));
			// System.out.println(getDigit("sil kwa ttro sil"));

		}
	}

	File languagemodel = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\language model\\conndigits.lm");
	File hmmsRepo = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\");

	public String recognize(File waveFile) throws Exception {

		LanguageModelSphinx lm = new LanguageModelSphinx(languagemodel);
		String[] allWords = lm.getAllWords();
		// ExhaustiveDecoder decoder = new ExhaustiveDecoder();
		ExhaustiveDecoderParallel decoder = new ExhaustiveDecoderParallel();
		decoder.setBeamRange(12, 30);// 16);
		decoder.setBeamConfidence(0.5);

		System.out.println("Adding lm");
		decoder.addLanguageModel(lm);

		for (String word : allWords) {
			/*
			 * if ( word.equals("u") || word.equals("no") || word.equals("due") ||
			 * word.equals("tre") || word.equals("kwa") || word.equals("ttro") ||
			 * word.equals("tSin") || word.equals("kwe") || word.equals("sei") ||
			 * word.equals("se") || word.equals("tte") || word.equals("o") ||
			 * word.equals("tto") || word.equals("dze") || word.equals("ro") //||
			 * word.equals("no") || word.equals("ve") || word.equals("sil") ||
			 * word.equals("sp") )
			 */
			{
				System.out.println("Adding word " + word);
				File hmmFile = new File(hmmsRepo, word + ".hmm");
				AcousticModel m = new AcousticModelHMM(word, hmmFile);
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

		return recognized;
	}

}
