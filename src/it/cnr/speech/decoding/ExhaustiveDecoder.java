package it.cnr.speech.decoding;

import java.util.ArrayList;

/***
 * Exhaustive Decoder implementation from Coro et al. 2007
 * 
 * @author Gianpaolo Coro
 *
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.language.LanguageModel;
import it.cnr.speech.performance.AcousticModel;

public class ExhaustiveDecoder {

	LanguageModel M;
	int maxBeamRange = -1;
	int minBeamRange = -1;
	double beamConfidence = 0.5;
	
	double LMWeight = 1; //-0.1; // -0.2;
	boolean displayV = false;
	boolean displayD = false;
	
	double[][] Vmax;
	double[][] Vmean;
	
	HashMap<String, fmodel> wordperF = new HashMap<>();
	HashMap<String, AcousticModel> wordPerAcousticModel = new HashMap<>();

	HashMap<String, String> bestBoundaries = new HashMap<>();
	String allWords[];

	public void setBeamRange(int minBeamRange, int maxBeamRange) {
		this.maxBeamRange = maxBeamRange;
		this.minBeamRange = minBeamRange;
	}

	public void setBeamConfidence(double confidence) {
		this.beamConfidence = confidence;
	}
	
	public double G(double x) {
		
		double sd = (maxBeamRange-minBeamRange)/2;
		double mean = (maxBeamRange+minBeamRange)/2;
		
		double g = (1/(sd*Math.sqrt(2*Math.PI)))*Math.exp(-0.5*Math.pow( ((x-mean)/sd),2)) ;
		return g;
		
	}
	
	Stack<String> visualizationStack = new Stack<>();
	HashMap<String,double[][]> featuresSubsets = new HashMap<>();
	class fmodel {

		String focusWord;
		AcousticModel model;

		public fmodel(AcousticModel model, double[][] features) {
			this.focusWord = model.getModelName();
			this.model = model;
			initV(features);

		}

		HashMap<Integer, Double> scorePerTimeIdx = new HashMap<>();
		double[][] V;
		
		private void initV(double[][] features) {
			int T = features.length;
			System.out.println("Calculating V for model " + model.getModelName());
			
			if (Vmax==null) {
				Vmax = new double[T][T];
				Vmean = new double[T][T];
			}
			
			V = new double[T][T];
			for (int row = 0; row < T; row++) {
				for (int col = 0; col < T; col++) {

					if (row > col || (maxBeamRange > -1 && col > row + maxBeamRange - 1)
							|| (minBeamRange > -1 && (col - row)< minBeamRange)) {
						V[row][col] = 0;
						//if (displayV) System.out.print(" \t");
					} else {
						double[][] featsubset = Arrays.copyOfRange(features, row, col + 1);
						
						/*
						String searchrc = row+";"+(col+1);
						double[][] featsubset = featuresSubsets.get(searchrc);
						
						if (featsubset!=null)
						{
						}else {
							
							featsubset = Arrays.copyOfRange(features, row, col + 1);
							
							featuresSubsets.put(searchrc, featsubset);
						}
						*/
						V[row][col] = model.calcLikelihood(featsubset); //* G((double)(col-row));//Math.log((double)(col-row));
						
						//if (displayV)  System.out.print(((int) (V[row][col] * 100)) / 100d + "\t");
					}
					
					
					if (Vmax[row][col]<V[row][col])
						Vmax[row][col] = V[row][col];
					
					Vmean [row][col] += V[row][col];
					
				}
				//if (displayV) System.out.println();
			}
			//System.exit(0);
		}

		
		public void normalizeV() {
			
			for (int row = 0; row < V.length; row++) {
				for (int col = 0; col < V.length; col++) {
						V[row][col] = V[row][col]/Vmax[row][col];
						/*
						double Vm =  Vmean[row][col]/(double)allWords.length;
						V[row][col] = Math.max(0, (V[row][col]-Vm)/Vm);
						*/
						
						if (Double.isNaN(V[row][col]))
							V[row][col] = 0;
						
						if (Double.isInfinite(V[row][col]))
							V[row][col] = 0;
						
						if (V[row][col]<(1-beamConfidence))
							V[row][col] = 0;
						
						if (displayV)  {
							double todisp = ((int) (V[row][col] * 10000)) / 10000d;
							
							if (todisp == 0d) System.out.print(" \t");
							else System.out.print(todisp+ "\t");
						}
					}
				
				if (displayV) System.out.println();
				
			}
			
			//System.exit(0);
		}
		
		public Double value(Integer timeIdx) {
			Double value = scorePerTimeIdx.get(timeIdx);
			if (value == null) {
				value = calcValue(timeIdx);
				scorePerTimeIdx.put(timeIdx, value);
			} else {
				// System.out.println("cached");
			}
			return value;

		}

		public Double calcValue(Integer timeIdx) {

			double f1 = V[0][timeIdx] * M.Start(focusWord);
			if (timeIdx < minBeamRange)
				f1 = 0;

			double fmax = 0;
			String prevWord = "";
			int prevT = 0;

			for (int t = 0; t < timeIdx; t++) {

				if ((timeIdx - t) < minBeamRange)
					continue;

				if (t < minBeamRange)
					continue;

				if (V[t + 1][timeIdx] == 0)
					continue;

				for (String word : allWords) {

					if (M.P(word, focusWord) != 0) {

						//double K = Math.pow(M.P(word, focusWord), LMWeight) * V[t + 1][timeIdx];
						double K = V[t + 1][timeIdx];
						if (displayD)
							System.out.println(visualizationStack.toString()+"->>" + focusWord + "_"+t+"_"+timeIdx+"->" + word + "_" + t);
						fmodel f = wordperF.get(word);
						double f2 = 0;
						if (f != null) {
							if (displayD)
								visualizationStack.push("\t");
							double f_w_t = f.value(t);
							if (displayD)
								visualizationStack.pop();
							f2 = f_w_t * K;
							if (displayD)
								System.out.println(visualizationStack.toString()+"<<-" + focusWord + "_"+t+"_"+timeIdx+"->" + word + "_" + t+" = "+(int)(f2*100)/100d+" [ "+ "F|"+ (int)(f_w_t*100)/100d + " X V|" + ((int)(K*100))/100d +" ]");
						}

						if (f2 > fmax) {
							fmax = f2;
							prevWord = word;
							prevT = t;
						}
					}
				}

			}

			if (Double.isInfinite(fmax))
				fmax = 0;

			double fbest = 0;
			if (f1 >= fmax || prevWord.length() == 0) {
				fbest = f1;
				// in this case we leave the relation blank because there is no previous
				// relation
				if (displayD)
					System.out.println(visualizationStack.toString()+"*" + focusWord + "_" + 0 + "_" + timeIdx + "->|= "+V[0][timeIdx]+" X "+ M.Start(focusWord)+" = "+fbest);
			} else {
				fbest = fmax;
				bestBoundaries.put(focusWord + ";" + timeIdx, prevWord + ";" + prevT);
				if (displayD)
					System.out.println(visualizationStack.toString()+"*" + focusWord + "_" + prevT + "_" + timeIdx + "->" + prevWord + "_" + prevT + "=" + fbest);
			}

			return fbest;
		}

	}

	LinkedHashMap<String, String> log = new LinkedHashMap<String, String>();

	Stack<String> decoded = new Stack<String>();
	Stack<Double> seconds = new Stack<Double>();
	Stack<Integer> indices = new Stack<Integer>();

	public String decode(double[][] features) {

		String[] allLMWords = M.getAllWords();
		List<String> allAMWords = new ArrayList<>();

		for (String word : allLMWords) {
			AcousticModel m = wordPerAcousticModel.get(word);
			if (m != null) {
				fmodel fm = new fmodel(m, features);
				wordperF.put(word, fm);
				allAMWords.add(word);
			}
		}
		
		allWords = new String[allAMWords.size()];
		allWords = allAMWords.toArray(allWords);
		
		for (String word : wordperF.keySet()) {
				System.out.println("Normalising V for "+word);
				fmodel fm = wordperF.get(word);
				fm.normalizeV();
		}		
		
		//System.exit(0);
		
		

		double bestScore = 0;
		int T = features.length - 1;
		String bestWord = "";

		for (String word : allWords) {

			if (M.End(word) != 0) {
				System.out.println("Decoding from " + word);
				fmodel f = wordperF.get(word);

				 double score = f.calcValue(T) * M.End(word);
				//double score = f.calcValueGreedy(T) * M.End(word);

				if (score >= bestScore) {
					bestScore = score;
					bestWord = word;
				}

			}
		}

		String decodingSearch = bestWord + ";" + T;
		System.out.println("Time index max " + T);
		System.out.println("Starting backwards decoding from " + decodingSearch + " Total Score: " + bestScore);

		while (decodingSearch != null) {
			System.out.println("Boundary: " + decodingSearch);
			String[] de = decodingSearch.split(";");
			String word = de[0];

			int tstar = Integer.parseInt(de[1]);
			indices.push(tstar);
			decoded.push(word);

			decodingSearch = bestBoundaries.get(decodingSearch);
			if (decodingSearch == null)
				indices.push(0);
		}

		StringBuffer sb = new StringBuffer();
		int m = decoded.size();

		for (int i = 0; i < m; i++) {
			sb.append(decoded.pop() + " ");
		}

		String transcription = sb.toString().trim();

		// System.out.println(log.toString().replace(",", "\n"));
		System.out.println("Transcription: " + transcription);

		return transcription;
	}

	public void addLanguageModel(LanguageModel M) {
		this.M = M;
	}

	public void addAcousticModel(AcousticModel m) {
		wordPerAcousticModel.put(m.getModelName(), m);
	}

	public double[] getAlignment(double samplingFrequency, int windowSamples) {
		int n = indices.size();

		double[] seconds = new double[n];

		for (int i = 0; i < n; i++) {
			int index = indices.pop();
			seconds[i] = Conversions.featureIndex2Milliseconds(index, samplingFrequency, windowSamples);
		}

		return seconds;

	}

}
