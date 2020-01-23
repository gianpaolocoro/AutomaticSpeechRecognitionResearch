package it.cnr.speech.performance.digits;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class MLP {

	
	static File wavesFolder = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL");
	static File transcriptionsFolder = new File("/home/gp/Desktop/ASR/Wave_Cifre_Isolate_4_DL");
	
	public static void main(String[] args) throws Exception{
		
			performanceASR();
		
		
	}

	public static String getDigit(String recognized) {
		
		
		recognized = recognized.replace("sp", "sil");
		recognized = recognized.replace("sil", " ");
		recognized = recognized.replace("|", " ");
		recognized = recognized.replaceAll(" +", " ");
		
		//System.out.print("T = "+recognized);
		if (recognized.contains("se sei"))
			return "sei";
		if (recognized.contains("dze dze"))
			return "zero";
		if (recognized.contains("kwa ttro"))
			return "quattro";
		if (recognized.contains("se sei"))
			return "sei";
		if (recognized.contains("dze"))
			return "zero";
		if (recognized.contains("u "))
			return "uno";
		if (recognized.contains("tSin"))
			return "cinque";
		if (recognized.contains("kwa "))
			return "quattro";
		if (recognized.trim().equals("tte"))
			return "tre";
		if (recognized.contains("tre tre"))
			return "tre";
		if (recognized.contains("tte se"))
			return "sei";

		if (recognized.split("u (no)?").length>1)
			return "uno";
		if (recognized.split("no").length>1)
			return "uno";
		if (recognized.contains("ttro tre"))
			return "tre";
		if (recognized.contains("dze ro"))
			return "zero";
		if (recognized.split("o (tto)?").length>1)
			return "otto";
		if (recognized.contains("o o"))
			return "otto";
		if (recognized.split("o ?(ttro)?").length>1)
			return "otto";
		if (recognized.split("(dze) ?(ro)?").length>1)
			return "zero";
		if (recognized.split("kwa ?(ttro)?").length>1)
			return "quattro";
		if (recognized.split("(kwa)? (ttro)").length>1)
			return "quattro";
		if (recognized.split("(dze)? ?(ro)").length>1)
			return "zero";
		if (recognized.contains("dze ro"))
			return "zero";
		if (recognized.split("tSin ?(kwe)?").length>1)
			return "cinque";
		if (recognized.split("(tSin)? ?kwe").length>1)
			return "cinque";
		if (recognized.trim().equals("sei"))
			return "sei";
		if (recognized.trim().equals("dze"))
			return "zero";
		if (recognized.trim().equals("tSin"))
			return "cinque";
		if (recognized.trim().equals("o"))
			return "otto";
		if (recognized.contains("tte sei"))
			return "sei";
		if (recognized.contains("se tre"))
			return "sei";
		if (recognized.split("se ?(tte)?").length>1)
			return "sette";
		//if (recognized.trim().equals("tte sei"))
			//return "tre";
		if (recognized.trim().equals("tte"))
			return "tre";
		if (recognized.split("(se)? ?(tte)").length>1)
			return "sette";
		if (recognized.split("due").length>1)
			return "due";
		if (recognized.split("tre").length>1)
			return "tre";
		if (recognized.split("sei").length>1)
			return "sei";
		if (recognized.split("se").length>1)
			return "sei";
		if (recognized.split("tte").length>1)
			return "tre";
		if (recognized.split("tto").length>1)
			return "otto";
		else if (recognized.trim().length()==0)
			return "";
		else
			return recognized;
	}

	
	public static String getDigit2(String recognized) {
		
		
		recognized = recognized.replace("sp", "sil");
		recognized = recognized.replace("sil", " ");
		recognized = recognized.replace("|", " ");
		recognized = recognized.replaceAll(" +", " ");
		
		//System.out.print("T = "+recognized);
		if (recognized.contains("se sei"))
			return "sei";
		if (recognized.contains("dze dze"))
			return "zero";
		if (recognized.contains("kwa ttro"))
			return "quattro";
		if (recognized.contains("se sei"))
			return "sei";
		if (recognized.contains("dze"))
			return "zero";
		if (recognized.contains("u "))
			return "uno";
		if (recognized.contains("tSin"))
			return "cinque";

		if (recognized.contains("kwa "))
			return "quattro";
		if (recognized.trim().equals("tte"))
			return "sette";
		if (recognized.contains("tre tre"))
			return "tre";
		if (recognized.split("u (no)?").length>1)
			return "uno";
		if (recognized.split("no").length>1)
			return "uno";
		if (recognized.contains("ttro tre"))
			return "tre";
		if (recognized.contains("dze ro"))
			return "zero";
		if (recognized.split("o (tto)?").length>1)
			return "otto";
		if (recognized.contains("o o"))
			return "otto";
		if (recognized.split("o ?(ttro)?").length>1)
			return "otto";
		if (recognized.split("(dze) ?(ro)?").length>1)
			return "zero";
		if (recognized.split("kwa ?(ttro)?").length>1)
			return "quattro";
		if (recognized.split("(kwa)? (ttro)").length>1)
			return "quattro";
		if (recognized.split("(dze)? ?(ro)").length>1)
			return "zero";
		if (recognized.contains("dze ro"))
			return "zero";
		if (recognized.split("tSin ?(kwe)?").length>1)
			return "cinque";
		if (recognized.split("(tSin)? ?kwe").length>1)
			return "cinque";
		if (recognized.trim().equals("sei"))
			return "sei";
		if (recognized.trim().equals("dze"))
			return "zero";
		if (recognized.trim().equals("tSin"))
			return "cinque";
		if (recognized.trim().equals("o"))
			return "otto";
		if (recognized.contains("tte sei"))
			return "sei";
		if (recognized.contains("se tre"))
			return "sei";
		if (recognized.split("se ?(tte)?").length>1)
			return "sette";
		//if (recognized.trim().equals("tte sei"))
			//return "tre";
		if (recognized.trim().equals("tte"))
			return "tre";
		if (recognized.split("(se)? ?(tte)").length>1)
			return "sette";
		if (recognized.split("due").length>1)
			return "due";
		if (recognized.split("tre").length>1)
			return "tre";
		if (recognized.split("sei").length>1)
			return "sei";
		if (recognized.split("se").length>1)
			return "sei";
		if (recognized.split("tte").length>1)
			return "tre";
		if (recognized.split("tto").length>1)
			return "otto";
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
				
				File transcription = new File(a.getParentFile(), a.getName() + "_MLP_transcription.txt");
				
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
