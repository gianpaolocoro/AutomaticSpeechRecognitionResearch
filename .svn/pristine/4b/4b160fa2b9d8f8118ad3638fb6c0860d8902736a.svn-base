package it.cnr.speech.performance.digits;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class CNRASR {

	
	static File wavesFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Wave_Cifre_Isolate_4_CNR");
	static File transcriptionsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Trascrizioni_Cifre_Isolate");
	
	public static void mainCheck(String[] args) throws Exception{
		
			performanceCNRASR();
		
		
	}
	
public static String getDigit(String recognized) {
		
		recognized = recognized.replace("sp", "sil");
		
		if (recognized.split("u( sil)* no").length>1)
			return "uno";
		if (recognized.split("un( sil)* no*").length>1)
			return "uno";
		if (recognized.split("o( sil)* tto").length>1)
			return "otto";
		if (recognized.split("o( sil)* ttro").length>1)
			return "otto";
		if (recognized.split("dze( sil)* ro").length>1)
			return "zero";
		if (recognized.split("kwa( sil)* ttro").length>1)
			return "quattro";
		if (recognized.split("tSin( sil)* kwe").length>1)
			return "cinque";
		if (recognized.split("tSin kwe").length>1)
			return "cinque";
		if (recognized.split("se( sil)* tte").length>1)
			return "sette";
		if (recognized.split("sil due sil").length>1)
			return "due";
		if (recognized.split("sil tre sil").length>1)
			return "tre";
		if (recognized.split("sil sei sil").length>1)
			return "sei";
		if (recognized.split("sil se sil").length>1)
			return "sei";
		if (recognized.split("sil tte sil").length>1)
			return "tre";
		else
			return recognized;
	}
	
	//this is the reference main for performance calculation
public static void main(String[] args) throws Exception{
		
		File allwaves [] = wavesFolder.listFiles();
		int correct = 0;
		int all = 0;
		
		for (File wave:allwaves) {
			
			if (wave.getName().endsWith(".wav")) {
				
				File transcriptionFile = new File(transcriptionsFolder,wave.getName().replace(".wav", ".ITO"));
				
				if (!transcriptionFile.exists())
					continue;
				
				String transcription = new String(Files.readAllBytes(transcriptionFile.toPath())).trim().toLowerCase();
				
				File recognizedTranscr = new File(wavesFolder,wave.getName().replace(".wav", "_transcription.txt"));
				if (recognizedTranscr.exists()) {
					System.out.println("Wave file "+wave.getName());
					String recognized = new String(Files.readAllBytes(recognizedTranscr.toPath()));
					if (recognized .equals("{}") || recognized.length ()== 0)
						recognized = "-";
					else {
					recognized = transform(recognized.trim());
					}
				System.out.print("Transcription="+transcription+" ; Recognition="+recognized+" ");
				
				//if (transcription.equals(recognized)) {
				if (!recognized.equals("-")) {
					correct++;
					System.out.println("OK");
				}else
					System.out.println("KO");
				
				all++;
				
				double accuracy = correct*100d/all;
				System.out.println("Partial accuracy: at "+wave.getName()+"= "+accuracy+" ["+correct+"/"+all+"]");
				}
				
				//if (all==160)
					//break;
				
			}
			
		}
		
		
		
	}
	

	public static void performanceCNRASR() throws Exception{
		
		File repository =wavesFolder;
		
		File allfiles [] = repository.listFiles(); 
		int correct = 0;
		int overall = 0;
		int correctSyl = 0;
		int allSyl = 0;
		
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
				
				File transcription = new File(repository,a.getName().replace(".wav", "_transcription.txt"));
				
				if (transcription.exists()) {
				String transcr = new String(Files.readAllBytes(transcription.toPath()));
				
				System.out.println("transcription: "+goldTranscription+"="+transcr);
				transcr = getDigit(transcr);
				System.out.println("transcription transformed: "+goldTranscription+"="+transcr);
				
				String goldT [] = goldTranscription.split(" ");
				String tr [] = transcr.split(" ");
				
				int overlap = 0;
				for (String gold:goldT) {
					boolean found  = false;
					for (String t:tr) {
						if (t.equals(gold)) {
							found = true;
							correctSyl++;
							break;
						}
					}
					if (found)
						overlap++;
					
					allSyl++;
				}
				
				System.out.print("Overlap "+overlap+" / "+goldT.length);
				
				if (((double)overlap/(double)goldT.length) > 0.75 || (overlap == 1 && goldT.length == 2)) {
					System.out.println(" OK");
					correct ++;
				}else
					System.out.println(" KO");
				
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
