package it.cnr.speech.language;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LanguageModelSphinx extends LanguageModel {

	public static void main(String[] args) throws Exception {
		File model = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\language model\\conndigits.lm");
		LanguageModelSphinx lms = new LanguageModelSphinx(model);
		System.out.println("Model loaded\n"+Arrays.toString(lms.getAllWords()));
	}

	public LanguageModelSphinx(File sphinxLM) throws Exception {
		importSphinxLanguageModel(sphinxLM);
	}

	HashMap<String, Double> monograms = new HashMap<>();
	HashMap<String, Double> bigrams = new HashMap<>();
	HashMap<String, Double> endingWords = new HashMap<>();
	HashMap<String, Double> startingWords = new HashMap<>();

	public void importSphinxLanguageModel(File sphinxLanguageModel) throws Exception {

		List<String> allLines = Files.readAllLines(sphinxLanguageModel.toPath());

		boolean monogramState = false;
		boolean bigramState = false;
		int counter = 0;

		for (String line : allLines) {

			if (counter >= 50) {
				if (monogramState) {
					if (line.length() > 0) {
						String[] elements = line.split("\t");
						if (elements.length > 1) {
							//System.out.println("->" + elements[0]);
							String wordStr[] = elements[0].split(" ");

							String word = wordStr[1];
							String prob = wordStr[0];

							Double logprob = Double.parseDouble(prob);
							monograms.put(word, logprob);
						}
					}
				} else if (bigramState) {
					if (line.length() > 0) {
						String[] elements1 = line.split("\t");
						String[] elements = elements1[0].split(" ");
						if (elements.length > 2) {
							String word1 = elements[1];
							String word2 = elements[2];
							Double logprob = Double.parseDouble(elements[0]);

							bigrams.put(word1 + ";" + word2, logprob);

							if (word2.equals("</s>"))
								endingWords.put(word1, logprob);
							if (word1.equals("<s>"))
								startingWords.put(word2, logprob);
						}
					}
				}

				if (line.startsWith("\\1-grams:"))
					monogramState = true;
				else if (line.startsWith("\\2-grams:")) {
					monogramState = false;
					bigramState = true;
				}

			}
			counter++;
		}
	}

	@Override
	public double Start(String word) {
		
		if (word.equals("sil"))
			return 1;
		
		Double prob = startingWords.get(word);
		if (prob == null)
			return 0d;
		else
			return Math.exp(prob);
	}

	@Override
	public double P(String word1, String word2) {
		
		if (word1.equals("sil") && word2.equals("sp"))
			return 0;
		
		if (word1.equals("sil") && word2.equals("sil"))
			return 1;
		
		if (word1.equals("sp") && word2.equals("sp"))
			return 0;
		
		if (word1.equals("sp") && word2.equals("sil"))
			return 0;
		
		if (word1.equals("sil") || word2.equals("sil"))
			return 1;
		
		Double prob = bigrams.get(word1 + ";" + word2);
		if (prob == null)
			return 0d;
		else
			return Math.exp(prob);
	}

	@Override
	public double End(String word) {
		Double prob = endingWords.get(word);
		if (prob == null)
			return 0d;
		else
			return Math.exp(prob);
	}

	@Override
	public String[] getAllWords() {
		List<String> vocabulary = new ArrayList<String>(monograms.keySet());
		vocabulary.remove("<s>");
		vocabulary.remove("</s>");
		String[] voc = new String[vocabulary.size()];
		voc = vocabulary.toArray(voc);
		return voc;
	}

}
