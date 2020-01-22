package it.cnr.speech.language;

public class SimpleLanguageModel3 extends LanguageModel{

	String[] allWords = {"w1","w2","w3","w4","w5"};
	
	//w1 w3
	//w1 w5
	//w4
	//w4 w2
	//w2
	
	@Override
	public double Start(String word) {
		
		if (word.equals("w1"))
			return 0.5;
		if (word.equals("w2"))
			return 0.5;
		if (word.equals("w5"))
			return 0.5;
		if (word.equals("w4"))
			return 0.5;
			
		return 0;
	}

	@Override
	public double P(String word1, String word2) {
		
		if (word1.equals("w1") && word2.equals("w3"))
			return 0.5;
		if (word1.equals("w1") && word2.equals("w5"))
			return 0.5;
		if (word1.equals("w4") && word2.equals("w2"))
			return 0.5;
		
		return 0;
	}

	@Override
	public double End(String word) {

		if (word.equals("w3"))
			return 0.5;
		if (word.equals("w2"))
			return 0.5;
		if (word.equals("w4"))
			return 0.5;
		if (word.equals("w5"))
			return 0.5;
		
		return 0;
	}

	@Override
	public String[] getAllWords() {
		return allWords;
	}
	
	
	
	
}
