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
import it.cnr.speech.deep.lstm.AcousticModelLSTMForSequentialRecognition;
import it.cnr.speech.deep.lstm.AcousticModelMLPForSequentialRecognition;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.language.LanguageModel;
import it.cnr.speech.language.LanguageModelSphinxRelaxed;
import it.cnr.speech.performance.AcousticModel;

public class SequentialDecoderLSTMNumbers {
	public int nMaxSyl = 3;
	public static double threshold = 0.99 + 1;//0.99 + 1;
	//public double threshold = 0.0 + 1;
	//public double chunksizems = 300;// 300-200;
	//public double overlapPercentage = 20; //20-50
	
	public static void main(String[] args) throws Exception {
		// File f = new File ("./digits/zero.wav");
		// File f = new File ("./digits/quattro.wav");
		//File f1 = new File("./digits/cinque.wav");
		// File f = new File ("./digits/due.wav");
		// File f = new File ("./digits/uno.wav");
		// File f = new File ("./digits/otto.wav");
		
		
		File[] files = new File("/home/gp/Desktop/ASR/Wave_Numeri/").listFiles();
		//File f1 = new File("/home/gp/Desktop/ASR/Wave_Numeri/SA037CN1.wav");
		//File f1 = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA017CI3.wav");
		//File f1 = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA005CI3.wav");
		//File f1 = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL/SA023CI1.wav");
		
		
		//File[] files = { f1 };
		
		for (File f : files) {

			if (f.getName().endsWith(".wav")) {
				
				File output = new File(f.getParentFile(), f.getName() + "_LSTM_transcription.txt");
				if (output.exists()) {
					System.out.println("Skipping "+f.getName());
					continue;
				}
				threshold = 0.7 + 1;//0.99 + 1;
				boolean recognized = false;
				double chunksizems = 300;// 300-200;
				double overlapPercentage = 20; //20-50
				
				while(!recognized) {
				System.out.println("Processing "+f.getName());
				File languagemodel = new File("conndigits.lm");

				LanguageModelSphinxRelaxed lm = new LanguageModelSphinxRelaxed(languagemodel);
				String[] allWords = lm.getAllWords();
				SequentialDecoderLSTMNumbers decoder = new SequentialDecoderLSTMNumbers();

				System.out.println("Adding lm");
				decoder.addLanguageModel(lm);

				for (String word : allWords) {
					{
						System.out.println("Adding word " + word);

						AcousticModelLSTMForSequentialRecognition m = new AcousticModelLSTMForSequentialRecognition(word);
						decoder.addAcousticModel(m);
					}
				}

				String recognition = decoder.decode(f, chunksizems, overlapPercentage);
				String cleanup = recognition.replace("sp", " " ).replace("sil", " ").replace("|", "");
				cleanup = cleanup.replaceAll(" +", " ");
				System.out.println("Rec: " + cleanup);
				
				
				
				FileWriter fw = new FileWriter(output);
				fw.write(recognition);
				fw.close();
				
				if (cleanup.trim().length()>0)
					recognized = true;
				else {
					chunksizems = chunksizems - 100;
					if (chunksizems <=100) {
						threshold = threshold-0.01;
						//recognized = true;
						chunksizems=300;
						overlapPercentage=20;
						System.out.println("trying with thr "+threshold);
					}
					}
				
				}//end while
				
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

		AcousticModelLSTMForSequentialRecognition.preCalcLikelihoods(chunks);

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
