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

public class ExhaustiveDecoderGreedy {

	LanguageModel M;
	int maxBeamRange = -1;
	int minBeamRange = -1;
	double LMWeight = 1; //-0.1; // -0.2;
	boolean displayV = false;
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

	public double G(double x) {
		
		double sd = (maxBeamRange-minBeamRange)/2;
		double mean = (maxBeamRange+minBeamRange)/2;
		
		double g = (1/(sd*Math.sqrt(2*Math.PI)))*Math.exp(-0.5*Math.pow( ((x-mean)/sd),2)) ;
		return g;
		
	}
	
	Stack<String> visualizationStack = new Stack<>();
	
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
		
		
		public Double valueGreedy(Integer timeIdx) {
			Double value = scorePerTimeIdx.get(timeIdx);
			if (value == null) {
				value = calcValueGreedy(timeIdx);
				scorePerTimeIdx.put(timeIdx, value);
			} else {
				// System.out.println("cached");
			}
			return value;

		}

		public Double calcValueGreedy(Integer timeIdx) {
			//System.out.println(visualizationStack.toString()+"Calculation for "+focusWord+" t:"+timeIdx);
			
			double f1 = V[0][timeIdx]; // * M.Start(focusWord);

			double vmax = 0;
			int tbest = timeIdx;
			
			boolean breaksearch = false;
			
			for (int t1 = timeIdx; t1 > 0; t1--) {
				
				for (int t0 = timeIdx; t0 > 0; t0--) {
					
					if (t0>=tbest)
						continue;
					
					double v = V[t0][t1];
					if (v == 0)
						continue;
					
					//System.out.println(focusWord+"_"+t0+"_"+t1+"="+v);
					
					if (v >= vmax) {
						vmax = v;
						tbest = t0;
					}else 
						if ( (vmax-v )/vmax > 0.045 )  {
						//System.out.println("------------");
						tbest = t0;
						breaksearch = true;
						break;
					}
				}
				if (breaksearch)
					break;
			}
			
			if (!breaksearch) {
				tbest = 0;
				f1 = vmax;
			}else {
				tbest = tbest+1;
			}
			
			/*
			for (int t = 0; t < timeIdx; t++) {
				double v = V[t + 1][timeIdx];
				System.out.println(focusWord+"_"+t+"_"+timeIdx+"="+v);
				if (v > vmax) {
					vmax = v;
					tbest = t;
				}
			}
			*/
			//tbest=tbest+1;
			
			//System.out.println(visualizationStack.toString()+"Best boundary "+focusWord+" : "+tbest+" <-> "+vmax);
			//System.exit(0);
			
			System.out.println(visualizationStack.toString()+"***************** START " + focusWord + " " + tbest + "-" + timeIdx + " (" + vmax + ")" + "**********************");
			
			double fmax = 0;
			String prevWord = "";

			if ((tbest>0) && (timeIdx - tbest) > minBeamRange) {

				
				
				for (String word : allWords) {
					
					if (M.P(word, focusWord) != 0) {
						
						//System.out.println(visualizationStack.toString()+"Evaluating " + word + "<-" + focusWord + " between " + 0 + "-" + tbest);
						System.out.println(visualizationStack.toString()+"Backwards " + word + "<-" + focusWord + " at " + tbest);
						double LMScore = Math.pow(M.P(word, focusWord), LMWeight);
						double V = vmax;
							
						double K =  LMScore * vmax;
						
						visualizationStack.push("\t");
						
						fmodel f = wordperF.get(word);
						double f_w_t = f.valueGreedy(tbest);
						double f2 = f_w_t * K;
						visualizationStack.pop();
						
						//System.out.println(visualizationStack.toString()+"Partial Result: "+focusWord + "_" + tbest + "_" + timeIdx + "->" + word + "_" + tbest + "="
								//+ f_w_t + " X " + LMScore + " X " + V + "=" + f2);
						System.out.println(visualizationStack.toString()+"Result " + word + "<-" + focusWord + " at " + tbest+"= "+f2+" [ "
								+ "F|"+ f_w_t + " X LM|" + LMScore + " X V|" + V +" ]"
								);

						if (f2 > fmax) {
							fmax = f2;
							prevWord = word;
						}
						
					}
					
					
				}
				
				
				//System.exit(0);
			}else
			{
				if ((timeIdx - tbest) <= minBeamRange)
					System.out.println(visualizationStack.toString()+"Beam too short");
			}
			
			if (Double.isInfinite(fmax))
				fmax = 0;

			double fbest = 0;
			if (f1 >= fmax || prevWord.length() == 0) {
				fbest = f1;
				System.out.println(visualizationStack.toString()+"*" + focusWord + "_" + tbest + "_" + timeIdx + "->|= "+V[0][timeIdx]+" X "+ M.Start(focusWord)+" = "+fbest);
			} else {
				fbest = fmax;
				System.out.println(visualizationStack.toString()+
						"*" + focusWord + "_" + tbest + "_" + timeIdx + "->" + prevWord + "_" + tbest + "=" + fbest);
				bestBoundaries.put(focusWord + ";" + timeIdx, prevWord + ";" + tbest);
			}
			System.out.println(visualizationStack.toString()+
					"***************** END " + focusWord + " " + tbest + "-" + timeIdx + "**********************");
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

				// double score = f.calcValue(T) * M.End(word);
				double score = f.calcValueGreedy(T) * M.End(word);

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
