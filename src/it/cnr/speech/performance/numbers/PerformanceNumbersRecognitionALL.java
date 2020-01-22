package it.cnr.speech.performance.numbers;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import it.cnr.speech.conversions.Converter;

public class PerformanceNumbersRecognitionALL {
	static File referenceRepository = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test\\");
	
	
	public static void main(String[]args) throws Exception{
		
		//performanceGoogleASR();
		
		performanceCNRASR();
		
		//performanceHMM();
		
		//performanceFHMM();
	}
	public static void performanceGoogleASR() throws Exception{
		
		File repository = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test_4_google\\");
		
		File allfiles [] = repository.listFiles(); 
		int correct = 0;
		int overall = 0;
		
		
		for (File a:allfiles) {
			
			
			
			if (a.getName().endsWith(".wav")) {
				
				String goldTranscriptionFile = new String(Files.readAllBytes(new File(referenceRepository,a.getName().replace(".wav", ".lab")).toPath()));
				goldTranscriptionFile=goldTranscriptionFile.replace("\n", "").replace("\r", "").trim();	
				
				double gold = Converter.string2num(goldTranscriptionFile);
				
				File transcription = new File(repository,a.getName().replace(".wav", ".txt"));
				double t = -1;
				
				if (transcription.exists()) {
				String transcr = new String(Files.readAllBytes(transcription.toPath()));
				
				if (transcr.equals("{}"))
					transcr = "no match";
				else {
				transcr = transcr.substring(transcr.indexOf("\"transcript\":"));
				transcr = transcr.substring(transcr.indexOf(":")+1,transcr.indexOf(",")).replace("\"", "").replace(" ", "").trim();
				try {
					t = Double.parseDouble(transcr);
				}catch(Exception e) {
					t = -2;
				}
				
				}
				System.out.println("transcription: "+goldTranscriptionFile+"="+transcr);
				System.out.println(gold+"="+t);
				if (t==-1) {
					
				}else if (t == -2) {
					
					if (goldTranscriptionFile.equals(transcr))
						correct++;
					
				}else if (t == gold)
				{
					correct ++;
				}
				
				overall++;
				}
			}
			
			
		}
		
		double accuracy = (double)correct*100d/(double)overall;
		
		System.out.println("Accuracy "+accuracy);
		
	} 
	
	public static void performanceCNRASR() throws Exception{
		
		File repository = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test_4_CNR\\");
		
		File allfiles [] = repository.listFiles(); 
		int correct = 0;
		int overall = 0;
		int correctSyl = 0;
		int allSyl = 0;
		
		for (File a:allfiles) {
			
			
			
			if (a.getName().endsWith(".wav")) {
				
				List<String> goldTranscriptionList = Files.readAllLines(new File(referenceRepository,a.getName().replace(".wav", ".rec")).toPath());
				String goldTranscription = "";
				for (String gold: goldTranscriptionList) {
					
					String [] line = gold.split(" ");
					goldTranscription+=" "+line[2];
					
				}
				
				goldTranscription=goldTranscription.replace("sil", " ").replace("sp", "");
				goldTranscription=goldTranscription.replaceAll(" +", " ");
				goldTranscription=goldTranscription.trim();
				
				File transcription = new File(repository,a.getName().replace(".wav", "_transcription.txt"));
				
				if (transcription.exists()) {
				String transcr = new String(Files.readAllBytes(transcription.toPath()));
				
				System.out.println("transcription: "+goldTranscription+"="+transcr);
				
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
				}
			}
			
			
		}
		
		double accuracy = (double)correct*100d/(double)overall;
		
		double saccuracy = (double)correctSyl*100d/(double)allSyl;
		
		System.out.println("Accuracy "+accuracy);
		
		System.out.println("Accuracy Syllables "+saccuracy);
		
	} 

public static void performanceHMM() throws Exception{
		
		File repository = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test_4_CNR\\");
		
		File allfiles [] = repository.listFiles(); 
		int correct = 0;
		int overall = 0;
		for (File a:allfiles) {
			
			
			
			if (a.getName().endsWith(".wav")) {
				
				List<String> goldTranscriptionList = Files.readAllLines(new File(referenceRepository,a.getName().replace(".wav", ".rec")).toPath());
				String goldTranscription = "";
				for (String gold: goldTranscriptionList) {
					
					String [] line = gold.split(" ");
					goldTranscription+=" "+line[2];
					
				}
				
				goldTranscription=goldTranscription.replace("sil", " ").replace("sp", "");
				goldTranscription=goldTranscription.replaceAll(" +", " ");
				goldTranscription=goldTranscription.trim();
				
				File transcription = new File(repository,a.getName().replace(".wav", "_hmm_transcr.txt"));
				
				if (transcription.exists()) {
				String transcr = new String(Files.readAllBytes(transcription.toPath()));
				transcr=transcr.replace("sil", " ").replace("sp", "");
				transcr=transcr.replaceAll(" +", " ");
				transcr=transcr.trim();
				
				System.out.println("transcription: "+goldTranscription+"="+transcr);
				
				String goldT [] = goldTranscription.split(" ");
				String tr [] = transcr.split(" ");
				
				int overlap = 0;
				for (String gold:goldT) {
					boolean found  = false;
					for (String t:tr) {
						if (t.equals(gold)) {
							found = true;
							break;
						}
					}
					if (found)
						overlap++;
				}
				
				System.out.print("Overlap "+overlap+" / "+goldT.length);
				
				if (((double)overlap/(double)goldT.length) > 0.75 || (overlap == 1 && goldT.length == 2)) {
					System.out.println(" OK");
					correct ++;
				}else
					System.out.println(" KO");
				
				overall++;
				}
			}
			
			
		}
		
		double accuracy = (double)correct*100d/(double)overall;
		
		System.out.println("Accuracy "+accuracy);
		
	} 

public static void performanceFHMM() throws Exception{
	
	File repository = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test_4_CNR\\");
	
	File allfiles [] = repository.listFiles(); 
	int correct = 0;
	int overall = 0;
	for (File a:allfiles) {
		
		
		
		if (a.getName().endsWith(".wav")) {
			
			List<String> goldTranscriptionList = Files.readAllLines(new File(referenceRepository,a.getName().replace(".wav", ".rec")).toPath());
			String goldTranscription = "";
			for (String gold: goldTranscriptionList) {
				
				String [] line = gold.split(" ");
				goldTranscription+=" "+line[2];
				
			}
			
			goldTranscription=goldTranscription.replace("sil", " ").replace("sp", "");
			goldTranscription=goldTranscription.replaceAll(" +", " ");
			goldTranscription=goldTranscription.trim();
			
			File transcription = new File(repository,a.getName().replace(".wav", "_fhmm_transcr.txt"));
			
			if (transcription.exists()) {
			String transcr = new String(Files.readAllBytes(transcription.toPath()));
			transcr=transcr.replace("sil", " ").replace("sp", "");
			transcr=transcr.replaceAll(" +", " ");
			transcr=transcr.trim();
			
			System.out.println("transcription: "+goldTranscription+"="+transcr);
			
			String goldT [] = goldTranscription.split(" ");
			String tr [] = transcr.split(" ");
			
			int overlap = 0;
			for (String gold:goldT) {
				boolean found  = false;
				for (String t:tr) {
					if (t.equals(gold)) {
						found = true;
						break;
					}
				}
				if (found)
					overlap++;
			}
			
			System.out.print("Overlap "+overlap+" / "+goldT.length);
			
			if (((double)overlap/(double)goldT.length) > 0.75 || (overlap == 1 && goldT.length == 2)) {
				System.out.println(" OK");
				correct ++;
			}else
				System.out.println(" KO");
			
			overall++;
			}
		}
		
		
	}
	
	double accuracy = (double)correct*100d/(double)overall;
	
	System.out.println("Accuracy "+accuracy);
	
} 

}
