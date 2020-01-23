package it.cnr.speech.performance.numbers;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import it.cnr.speech.recognition.HMMSpeechRecognizer;

public class LSTM {

	
	static File wavesFolder = new File("/home/gp/Desktop/ASR/Wave_Numeri/");
	static File transcriptionsFolder = new File("/home/gp/Desktop/ASR/Wave_Numeri");
	
	public static void main(String[] args) throws Exception{
		
			performanceASR();
		
		
	}

	
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
				
				List<String> goldTranscriptionList = Files.readAllLines(new File(transcriptionsFolder,a.getName().replace(".wav", ".rec")).toPath());
				String goldTranscription = "";
				for (String gold: goldTranscriptionList) {
					
					String g = gold.split(" ")[2];
					goldTranscription+=" "+g;
					
				}
				
				goldTranscription=goldTranscription.replace("sil", " ").replace("sp", "");
				goldTranscription=goldTranscription.replaceAll(" +", " ");
				goldTranscription=goldTranscription.trim();
				
				File transcription = new File(a.getParentFile(), a.getName() + "_LSTM_transcription.txt");
				
				if (transcription.exists()) {
					String transcr = new String(Files.readAllBytes(transcription.toPath()));
					transcr = transcr.replace("|", " ").replace("sil", " ").replace("sp", " ");
					transcr=transcr.replaceAll(" +", " ");
					transcr=transcr.trim();
					String gtrans []= goldTranscription.split(" ");
					int overlap = 0;
					for (String g:gtrans)
					{
						if (transcr.contains(g))
							overlap++;
					}					
					String transcrTransf = (transcr);
					
				
					
					double over = (double)overlap/gtrans.length;
					
					if (gtrans.length==2 && over >0.4)
						over = 1;
					if (gtrans.length==1)
						over = 1;
				if (over>0.85) {
					//System.out.println(" OK");
					correct ++;
				}else {
					System.out.print("Overlap "+overlap+" / "+gtrans.length+" ");
					System.out.print("transcription: "+goldTranscription+"="+transcr);
					
					//System.out.print("transcription transformed: "+goldTranscription+"="+transcrTransf);
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
