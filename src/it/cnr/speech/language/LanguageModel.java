package it.cnr.speech.language;

public abstract class LanguageModel {

	
	abstract public double Start(String word);
	abstract public double P(String word1,String word2);
	abstract public double End(String word);
	abstract public String[] getAllWords();
	
}
