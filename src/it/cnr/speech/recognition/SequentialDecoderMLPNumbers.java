package it.cnr.speech.recognition;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.deep.lstm.AcousticModelMLPForSequentialRecognition;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.language.LanguageModel;
import it.cnr.speech.language.LanguageModelSphinxRelaxed;
import it.cnr.speech.performance.AcousticModel;

public class SequentialDecoderMLPNumbers {
	public int nMaxSyl = 5;
	public static double threshold = 0.99 + 1;
	// public double threshold = 0.0 + 1;
	public double chunksizems = 300;// 300-200;
	public double overlapPercentage = 20; // 20-50

	public static void main(String[] args) throws Exception {
		// File f = new File ("./digits/zero.wav");
		// File f = new File ("./digits/quattro.wav");
		// File f1 = new File("./digits/cinque.wav");
		// File f = new File ("./digits/due.wav");
		// File f = new File ("./digits/uno.wav");
		// File f = new File ("./digits/otto.wav");

		// File[] files = new File("/home/gp/Desktop/ASR/Wave_Numeri/").listFiles();

		// File f1 = new
		// File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA017CI3.wav");
		// File f1 = new
		// File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA005CI3.wav");
		// File f1 = new
		// File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA020CI3.wav");
		// mi lle no ve tSen to kwa ran ta

		// sei tSen to tSin kwan ta sei mi la o tto tSen to se ssan ta tre

		// File f1 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA058CD2.wav");
		// File f1 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA041CI1.wav");
		File f1 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA037CN1.wav");
		File f2 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA005CN2.wav");
		File f3 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA058CD1.wav");
		File f4 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA024CT1.wav");
		File f5 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA034CT1.wav");
		File f6 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA037CN2.wav");
		File f7 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA049CN3.wav");
		File f8 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA018CN3.wav");
		File f9 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA018CD2.wav");
		File f10 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA049CN1.wav");
		File f11 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA053CI3.wav");
		File f12 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA009CN3.wav");
		File f13 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA009CD1.wav");
		File f14 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA015CI1.wav");
		File f15 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA005CD1.wav");
		File f16 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA058CD2.wav");
		File f17 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA005CN1.wav");
		File f18 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA049CN2.wav");
		File f19 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA015CN3.wav");
		File f20 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA018CI2.wav");
		File f21 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA018CN2.wav");

		File[] files = { f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14,f15,f16,
				f17,f18,f19,f20,f21
				};

		for (File f : files) {

			if (f.getName().endsWith(".wav")) {
				boolean recognized = false;
				threshold = 0.7 + 1;// 0.99 + 1;
				double chunksizems = 150;// 300-200;
				double overlapPercentage = 20; // 20-50

				while (!recognized) {
					System.out.println("Processing " + f.getName());
					File languagemodel = new File("conndigits.lm");

					LanguageModelSphinxRelaxed lm = new LanguageModelSphinxRelaxed(languagemodel);
					String[] allWords = lm.getAllWords();
					SequentialDecoderMLPNumbers decoder = new SequentialDecoderMLPNumbers();

					System.out.println("Adding lm");
					decoder.addLanguageModel(lm);

					for (String word : allWords) {
						{
							System.out.println("Adding word " + word);

							AcousticModelMLPForSequentialRecognition m = new AcousticModelMLPForSequentialRecognition(
									word);
							decoder.addAcousticModel(m);
						}
					}

					String recognition = decoder.decode(f, chunksizems, overlapPercentage);
					String cleanup = recognition.replace("sp", " ").replace("sil", " ").replace("|", " ");
					cleanup = cleanup.replaceAll(" +", " ");
					System.out.println("Rec: " + cleanup);

					File output = new File(f.getParentFile(), f.getName() + "_MLP_transcription.txt");
					FileWriter fw = new FileWriter(output);
					fw.write(recognition);
					fw.close();

					if (cleanup.trim().length() > 0)
						recognized = true;
					else {
						chunksizems = chunksizems - 100;
						if (chunksizems <= 0)
							recognized = true;
					}

				} // end while

			}
		}

	}

	public String decode(File waveFile, double chunksizems, double overlapPercentage) throws Exception {

		List<double[][]> chunks = extractMFCCchunks(waveFile, chunksizems, overlapPercentage);
		generateTrellis(chunks);
		String recognized = decodeTrellis();
		return recognized;

	}

	List<AcousticModel> allModels = new ArrayList<>();
	LanguageModel M;
	List<HashMap<String, Double>> trellis = new ArrayList<>();

	public void addLanguageModel(LanguageModel M) {
		this.M = M;
	}

	public void addAcousticModel(AcousticModel m) {
		allModels.add(m);
	}

	public void generateTrellis(List<double[][]> chunks) throws Exception {

		AcousticModelMLPForSequentialRecognition.preCalcLikelihoods(chunks);

		for (double[][] chunk : chunks) {
			HashMap<String, Double> scoreTable = new HashMap<>();
			double nBest[] = new double[nMaxSyl];
			String nBestS[] = new String[nMaxSyl];
			for (AcousticModel am : allModels) {

				double lik = am.calcLikelihood(chunk);
				scoreTable.put(am.getModelName(), lik);
				for (int k = 0; k < nBest.length; k++) {
					double b = nBest[k];

					if (b < lik) {
						nBest[k] = lik;
						if (lik > threshold) {
							nBestS[k] = am.getModelName();
						} else
							nBestS[k] = "sil";
						break;
					}

				}

			}

			HashMap<String, Double> scoreTableBest = new LinkedHashMap<>();
			for (String key : scoreTable.keySet()) {
				for (String b : nBestS) {
					if (b.equals(key)) {
						scoreTableBest.put(key, scoreTable.get(key));
						break;
					}
				}

			}

			// System.out.println("T"+counter+": "+scoreTableBest);
			trellis.add(scoreTableBest);

		}

	}

	public String decodeTrellis() {

		int trellisLen = trellis.size();
		HashMap<String, Double> track = new HashMap<>();
		HashMap<String, Double> t0 = trellis.get(0);
		for (String word : t0.keySet()) {

			double score = t0.get(word);
			if (M.Start(word) > 0)
				track.put(word, Math.log(score) + Math.log(M.Start(word)));
		}
		System.out.println("Tr0: " + t0);
		System.out.println("Tc0: " + track);

		for (int t = 1; t < trellisLen; t++) {

			HashMap<String, Double> trellisT = trellis.get(t);

			HashMap<String, Double> newtrack = new HashMap<>();

			for (String word : trellisT.keySet()) {

				String bestSequence = "";
				double bestScore = 0;
				double wordLik = trellisT.get(word);

				for (String sequence : track.keySet()) {

					double sequenceLik = track.get(sequence);
					String latestWord = sequence.substring(sequence.lastIndexOf("|") + 1);
					double score = 0;
					if (M.P(latestWord, word) > 0 || latestWord.equals(word)) {
						score = Math.log(wordLik) + sequenceLik;
					} else {
						score = Math.log(wordLik) + sequenceLik + Math.log(0.001);
					}

					if (score < 0)
						score = 0;

					if (t == (trellisLen - 1)) {

						if (M.End(word) > 0)
							score = score + Math.log(M.End(word));
						/*
						 * else score = 0;
						 */
					}

					if (score > bestScore) {
						bestSequence = sequence;
						bestScore = score;

					}

				}

				if (bestSequence.length() > 0) {
					String newSeq = bestSequence + "|" + word;
					newtrack.put(newSeq, bestScore);
				}
				/*
				 * else { String newSeq = bestSequence+"|sil"; newtrack.put(newSeq,
				 * Math.log(wordLik)); }
				 */

			}

			track = null;
			System.gc();
			track = newtrack;
			System.out.println("Tr" + t + ": " + trellisT);
			System.out.println("Tc" + t + ": " + track);
		}

		List<String> sortedTrack = new ArrayList<>();
		List<Double> sortedTrackScores = new ArrayList<>();

		for (String seq : track.keySet()) {
			double score = track.get(seq);
			int counter = 0;
			for (Double d : sortedTrackScores) {
				if (d < score) {
					break;
				}
				counter++;
			}
			sortedTrack.add(counter, seq);
			sortedTrackScores.add(counter, score);
		}

		String bestTrack = sortedTrack.get(0);

		System.out.println("Tracks " + sortedTrack);
		System.out.println("Scores " + sortedTrackScores);
		String allSills[] = bestTrack.split("\\|");
		for (int i = 0; i < allSills.length; i++) {
			String sil = allSills[i];

			HashMap<String, Double> d = trellis.get(i);

			Double score = d.get(sil);
			if (!sil.equals("sil") && !sil.equals("sp"))
				System.out.println(sil + "=" + score);

		}

		return bestTrack;
	}

	public List<double[][]> extractMFCCchunks(File waveFile, double chunkLengthms, double overlapPercentage)
			throws Exception {

		AudioBits ab = new AudioBits(waveFile);

		MfccExtraction extractor = new MfccExtraction(ab.getAudioFormat().getSampleRate());
		short[] signal = ab.getShortVectorAudio();

		double[][] mfccMatrix = null;

		try {
			mfccMatrix = extractor.extractMFCC(signal);
		} catch (Exception e) {
			System.out.println(
					"Error with wave for MFCC extraction " + waveFile.getName() + " : " + e.getLocalizedMessage());
			mfccMatrix = new double[1][MfccExtraction.defaultNCoeff * 3];
			throw new Exception(e);
		}

		int totalFrames = mfccMatrix.length;
		long nSamplesChunks = Conversions.millisecondsToSamples(ab.getAudioFormat().getSampleRate(), chunkLengthms);

		int frameLengthSamples = extractor.fe.frameLength;

		int nFramesforChunk = (int) ((double) nSamplesChunks * 2 / (double) frameLengthSamples);

		int totalChunks = (int) ((double) totalFrames / (double) nFramesforChunk);
		int step = (int) ((double) nFramesforChunk * (double) (overlapPercentage / 100));

		System.out.println("Total frames " + totalFrames);
		System.out.println("Chunk size in frames " + nFramesforChunk);
		System.out.println("Step " + step);
		System.out.println("Total steps " + totalChunks);

		List<double[][]> allsubfeatures = new ArrayList<double[][]>();

		for (int i = 0; i < totalFrames; i = i + step) {

			if ((i + nFramesforChunk) < mfccMatrix.length) {
				double[][] subfeatures = Arrays.copyOfRange(mfccMatrix, i, i + nFramesforChunk);
				allsubfeatures.add(subfeatures);
			}
		}

		System.out.println("All chunks extracted");
		return allsubfeatures;

	}

}
