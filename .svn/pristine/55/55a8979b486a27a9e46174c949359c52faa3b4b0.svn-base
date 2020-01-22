package it.cnr.speech.decoding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.hmm.AcousticModelHMM;
import it.cnr.speech.language.LanguageModel;
import it.cnr.speech.language.LanguageModelSphinx;
import it.cnr.speech.language.LanguageModelSphinxRelaxed;
import it.cnr.speech.performance.AcousticModel;

public class SequentialDecoder {

	public static void main(String[] args) throws Exception {
		//File f = new File("./digits/due.wav");
		//File f = new File ("./digits/uno.wav");
		//File f = new File ("./digits/tre.wav");
		//File f = new File ("./digits/quattro.wav");
		//File f = new File ("./digits/cinque.wav");
		//File f = new File ("./digits/sei.wav");
		//File f = new File ("./digits/sette.wav");
		//File f = new File ("./digits/otto.wav");
		File f = new File ("./digits/zero.wav");
		double chunksizems = 100;// 100;
		double overlapPercentage = 100;
		
		File languagemodel = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\language model\\conndigits.lm");
		File hmmsRepo = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\");
		LanguageModelSphinxRelaxed lm = new LanguageModelSphinxRelaxed(languagemodel);
		String[] allWords = lm.getAllWords();
		SequentialDecoder decoder = new SequentialDecoder();

		System.out.println("Adding lm");
		decoder.addLanguageModel(lm);

		for (String word : allWords) {
			if (word.equals("u") || word.equals("no") || word.equals("due") || word.equals("tre") || word.equals("kwa")
					|| word.equals("ttro") || word.equals("tSin") || word.equals("kwe") || word.equals("sei")
					|| word.equals("se") || word.equals("tte") || word.equals("o") || word.equals("tto")
					|| word.equals("dze") || word.equals("ro")
					// || word.equals("no") || word.equals("ve")
					|| word.equals("sil") || word.equals("sp")) {
				System.out.println("Adding word " + word);
				File hmmFile = new File(hmmsRepo, word + ".hmm");
				AcousticModel m = new AcousticModelHMM(word, hmmFile);
				decoder.addAcousticModel(m);
			}
		}

		String recognition = decoder.decode(f, chunksizems, overlapPercentage);
		System.out.println("Rec: " + recognition);
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
	public int nMaxSyl = 2;
	
	public void addLanguageModel(LanguageModel M) {
		this.M = M;
	}

	public void addAcousticModel(AcousticModel m) {
		allModels.add(m);
	}

	public void generateTrellis(List<double[][]> chunks) throws Exception {

		int counter = 0;
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
						nBestS[k] = am.getModelName();
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
			counter++;
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
					}else {
						score = Math.log(wordLik) + sequenceLik+Math.log(0.001);
					}
					
					if (score<0)
						score = 0;
					
					if (t == (trellisLen - 1)) {
						
						if (M.End(word) > 0)
							score = score + Math.log(M.End(word));
						/*
						else
							score = 0;
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
		System.out.println("Scores " + sortedTrack);

		return bestTrack;
	}

	public String decodeTrellisExh() {

		int trellisLen = trellis.size();
		HashMap<String, Double> track = new HashMap<>();
		HashMap<String, Double> t0 = trellis.get(0);
		for (String word : t0.keySet()) {

			double score = t0.get(word);
			if (M.Start(word) > 0)
				track.put(word, Math.log(score) + Math.log(M.Start(word)));
		}

		System.out.println("T0: " + track);

		for (int t = 1; t < trellisLen; t++) {

			HashMap<String, Double> trellisT = trellis.get(t);

			HashMap<String, Double> newtrack = new HashMap<>();

			for (String sequence : track.keySet()) {

				double sequenceLik = track.get(sequence);

				for (String word : trellisT.keySet()) {

					double wordLik = trellisT.get(word);
					// double score =
					// wordLik*sequenceLik*M.P(sequence.substring(sequence.lastIndexOf("|")+1),
					// word);
					double score = 0;
					String latestWord = sequence.substring(sequence.lastIndexOf("|") + 1);
					// System.out.println("Latest word "+latestWord+" vs "+word+" "+M.P(latestWord,
					// word));
					if (M.P(latestWord, word) > 0 || latestWord.equals(word)) {
						// score =
						// Math.log(wordLik)+sequenceLik*Math.log(M.P(sequence.substring(sequence.lastIndexOf("|")+1),
						// word));
						score = Math.log(wordLik) + sequenceLik;
					}
					if (t == (trellisLen - 1)) {
						if (M.End(word) > 0)
							score = score + Math.log(M.End(word));
						else
							score = 0;
					}

					if (score > 0) {
						String newSeq = sequence + "|" + word;
						newtrack.put(newSeq, score);
					} else {
						String newSeq = sequence + "|sil";
						newtrack.put(newSeq, Math.log(wordLik));
					}

				}

			}

			/*
			 * System.out.println("H"+t+": "+newtrack); //greedy strategy String []
			 * bestTracks = new String[3]; double [] bestTrackScores = new double[3]; for
			 * (String seq:newtrack.keySet()) { double scoreSeq = newtrack.get(seq); for
			 * (int g =0 ;g<bestTrackScores.length;g++) { double b = bestTrackScores[g];
			 * 
			 * if(b<=scoreSeq) { bestTrackScores[g]= scoreSeq; bestTracks[g] = seq; break; }
			 * 
			 * } }
			 * 
			 * HashMap<String,Double> scoreTableBest = new LinkedHashMap<>(); for (String
			 * key:newtrack.keySet()) { for (String b:bestTracks) { if (b.equals(key)) {
			 * scoreTableBest.put(key, newtrack.get(key)); break; } }
			 * 
			 * } newtrack = scoreTableBest; System.out.println("H2_"+t+": "+newtrack);
			 */
			track = null;
			System.gc();
			track = newtrack;
			System.out.println("T" + t + ": " + track);
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
		System.out.println("Scores " + sortedTrack);

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
