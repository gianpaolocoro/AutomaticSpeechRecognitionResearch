package it.cnr.speech.speecon;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PrepareTranscriptionsForLM {

	
	
	public static File TrainingCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\train");
	public static File TestCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test");
	
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("Rebuilding training");
		List<String> trainingTranscription = getTranscriptions(TrainingCorpusFolder);
		System.out.println("Rebuilding test");
		List<String> testTranscription = getTranscriptions(TestCorpusFolder);
		List<String> all = new ArrayList<>(trainingTranscription);
		all.addAll(testTranscription);
		File output = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\language model\\sentences.txt");
		System.out.println("Writing...");
		FileWriter fw = new FileWriter(output);
		for(String s:all) {
			fw.write(s+"\n");
		}
		fw.close();
		System.out.println("All done.");
	}
	
	
	public static List<String> getTranscriptions(File folder) throws Exception{
		
		
		File[]allfiles = folder.listFiles();
		
		List<String> allTranscriptions = new ArrayList<>();
		
		for (File f: allfiles) {
			
			if (f.getName().endsWith(".rec")) {
				
				List<String> lines = Files.readAllLines(f.toPath());
				StringBuffer transcription = new StringBuffer();
				
				for(String line:lines) {
					
					String[] elements = line.split(" ");
					String tr = elements[2];
					transcription.append(tr+" ");
				}
				
				allTranscriptions.add("<s> "+transcription+" </s>");
				
			}
		}
		
		return allTranscriptions;
		
	}
	
}
