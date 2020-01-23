package it.cnr.speech.performance.digits;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class LSTM {

	
	static File wavesFolder = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL");
	static File transcriptionsFolder = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL");
	
	public static void main(String[] args) throws Exception{
			String recognized = "nonono";
			System.out.println(recognized.matches("(no)+"));
			performanceASR();

		
	}

	public static String getDigit(String recognized) {
		
		
		recognized = recognized.replace("sp", "sil");
		recognized = recognized.replace("sil", " ");
		recognized = recognized.replace("|", " ");
		recognized = recognized.replaceAll(" +", " ");
		if (recognized.matches(".*u+( )?(no)+.*"))
			return "uno";
		
		if (recognized.matches(".*(tre)+.*"))
			return "tre";
		if (recognized.matches(".*(kwa)+( )?(ttro)+.*"))
			return "quattro";
		if (recognized.matches(".*(tSin)+( )?(kwe)+.*"))
			return "cinque";
		if (recognized.matches(".*(sei)+.*"))
			return "sei";
		if (recognized.matches(".*(se)+( )?(tte)+.*"))
			return "sette";
		if (recognized.matches(".*o+( )?(tto)+.*"))
			return "otto";
		if (recognized.matches(".*(dze)+( )?(ro)+.*"))
			return "zero";
		
		if (recognized.matches(".*(kwa).*(ttro)+.*"))
			return "quattro";
		if (recognized.matches(".*(tSin).*(kwe).*"))
			return "cinque";
		if (recognized.matches(".*(due)+.*"))
			return "due";
		if (recognized.matches(".*(te) ?(ro).*"))
			return "zero";
		
		if (recognized.trim().replace(" ","").matches("(no)+"))
			return "uno";
		
		else if (recognized.trim().equals("kwe"))
			return "cinque";
		
		else if (recognized.trim().equals("ro"))
			return "zero";
		
		else if (recognized.trim().equals("kwe"))
			return "cinque";
		
		else if (recognized.trim().equals("tte"))
			return "tre";
		
		else if (recognized.trim().length()==0)
			return "";
		else
			return recognized;
	}

	
	
	//this is the reference main for performance calculation

	public static void performanceASR() throws Exception{
		
		File repository =wavesFolder;
		
		File allfiles [] = repository.listFiles(); 
		int correct = 0;
		int overall = 0;
		int correctSyl = 0;
		int allSyl = 0;
		
		List<String> wrongRecognizedFiles = new ArrayList<String>();
		
		for (File a:allfiles) {
			
			
			
			if (a.getName().endsWith(".wav")) {
				
				List<String> goldTranscriptionList = Files.readAllLines(new File(transcriptionsFolder,a.getName().replace(".wav", ".ITO")).toPath());
				String goldTranscription = "";
				for (String gold: goldTranscriptionList) {
					
					
					goldTranscription+=" "+gold;
					
				}
				
				goldTranscription=goldTranscription.replace("sil", " ").replace("sp", "");
				goldTranscription=goldTranscription.replaceAll(" +", " ");
				goldTranscription=goldTranscription.trim();
				
				File transcription = new File(a.getParentFile(), a.getName() + "_LSTM_transcription.txt");
				
				if (transcription.exists()) {
				String transcr = new String(Files.readAllBytes(transcription.toPath()));
				
				//System.out.println("transcription: "+goldTranscription+"="+transcr);
				String transcrTransf = getDigit(transcr);
				//System.out.println("transcription transformed: "+goldTranscription+"="+transcrTransf);
				
				
				//System.out.print("Overlap "+overlap+" / "+goldT.length);
				
				if (transcrTransf.equals(goldTranscription)) {
					//System.out.println(" OK");
					correct ++;
				}else {
					//System.out.println("transcription: "+goldTranscription+"="+transcr);
					
					String recognized = transcr.replace("sp", "sil");
					recognized = recognized.replace("sil", " ");
					recognized = recognized.replace("|", " ");
					recognized = recognized.replaceAll(" +", " ");
					
					System.out.println ("T = "+recognized);
					
					System.out.print("transcription transformed: "+goldTranscription+"="+transcrTransf);
					System.out.println(" KO "+a.getAbsolutePath());
					//getDigit(transcr);
					wrongRecognizedFiles.add(a.getAbsolutePath());
				}
				overall++;
				
				if (overall>500)
					break;
				}
			}
			
			
		}
		
		double accuracy = (double)correct*100d/(double)overall;
		
		double saccuracy = (double)correctSyl*100d/(double)allSyl;
		
		System.out.println("Accuracy "+accuracy);
		
		System.out.println("Accuracy Syllables "+saccuracy);
		
		System.out.println(wrongRecognizedFiles);
		
	} 

	public static String transform(String recognized) {
		
		if (recognized.equals("1"))
			return "uno";
		if (recognized.equals("2"))
			return "due";
		if (recognized.equals("3"))
			return "tre";
		if (recognized.equals("4"))
			return "quattro";
		if (recognized.equals("5"))
			return "cinque";
		if (recognized.equals("6"))
			return "sei";
		if (recognized.equals("7"))
			return "sette";
		if (recognized.equals("8"))
			return "otto";
		if (recognized.equals("9"))
			return "nove";
		if (recognized.equals("0"))
			return "zero";
		if (recognized.equals("trey"))
			return "tre";
		if (recognized.split(" ").length>1 && recognized.split(" ") [1].equals("tre"))
			return recognized.replace("tre","").trim();
		if (recognized.split(" ").length>1 && recognized.split(" ") [1].equals("a"))
			return recognized.replace("a","").trim();
		else
			return recognized;
		
		
	}
	
	
}
