package it.cnr.speech.performance.digits;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import it.cnr.speech.recognition.FHMMSpeechRecognizer;
import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class FactorialHMMs {

	
	static File wavesFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Wave_Cifre_Isolate");
	static File transcriptionsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon digits\\Trascrizioni_Cifre_Isolate");
	
	public static void main(String[] args) throws Exception{
		
		File allwaves [] = wavesFolder.listFiles();
		int correct = 0;
		int all = 0;
		File allComparisons = new File("fhmm_comparisons.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(allComparisons));
		
		for (File wave:allwaves) {
			
			if (wave.getName().endsWith(".wav")) {
				System.out.println("Wave file "+wave.getName());
				File transcriptionFile = new File(transcriptionsFolder,wave.getName().replace(".wav", ".ITO"));
				String transcription = new String(Files.readAllBytes(transcriptionFile.toPath())).trim().toLowerCase();
				
				FHMMSpeechRecognizer asr = new FHMMSpeechRecognizer();
				String recognized = asr.recognize(wave);
				
				System.out.print("Transcription="+transcription+" ; Recognition="+recognized+" ");
				
				bw.write(transcription+"="+recognized);
				
				if (transcription.equals(recognized)) {
					correct++;
					System.out.println("OK");
				}else
					System.out.println("KO");
				
				all++;
				
				double accuracy = correct*100d/all;
				System.out.println("Partial accuracy: at "+wave.getName()+"= "+accuracy+" ["+correct+"/"+all+"]");
				if (all==100)
					break;
				
			}
			
		}
		
		
		bw.close();
		
		
		
	}
	
	
}
