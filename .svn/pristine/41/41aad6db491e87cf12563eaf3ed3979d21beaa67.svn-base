package it.cnr.speech.hmm.kaldi;

import java.io.File;
import java.nio.file.Files;

import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class PerformanceDigitsRecognition {

	
	static File wavesFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Wave_Cifre_Isolate_4_google");
	static File transcriptionsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Trascrizioni_Cifre_Isolate");
	
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
				
				File recognizedTranscr = new File(wavesFolder,wave.getName().replace(".wav", ".txt"));
				if (recognizedTranscr.exists()) {
					System.out.println("Wave file "+wave.getName());
					String recognized = new String(Files.readAllBytes(recognizedTranscr.toPath()));
					if (recognized .equals("{}"))
						recognized = "-";
					else {
					recognized = recognized.substring(recognized.indexOf("\"transcript\""));
					recognized = recognized.substring(recognized.indexOf(":")+1);
					recognized = recognized.substring(0,recognized.indexOf(",")).replace("\"", "").trim();
					recognized = transform(recognized);
					}
				System.out.print("Transcription="+transcription+" ; Recognition="+recognized+" ");
				
				if (transcription.equals(recognized)) {
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
		else
			return recognized;
		
		
	}
	
	
}
