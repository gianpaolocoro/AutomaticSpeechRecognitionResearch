package it.cnr.speech.hmm.kaldi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class KALDICorpusOrganizer {

	static File trainingSet = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train");
	static File testSet = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test");
	static File outputFolderSet = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\KALDI");
	static File outputFolderTrain = new File(outputFolderSet,"train");
	static File outputFolderTest = new File(outputFolderSet,"test");
	static String prefixTrain = "/opt/kaldi/egs/testsgp/digits_audio/train/spk_1/";
	static String prefixTest = "/opt/kaldi/egs/testsgp/digits_audio/test/spk_1/";
	
	public static void main(String args[]) throws Exception{
		organize(testSet,outputFolderTest,prefixTest);
		organize(trainingSet,outputFolderTrain,prefixTrain);
	}

	public static void organize(File folder, File outputFolder, String prefix) throws Exception{

		if (outputFolder.exists()) {
		Files.walk(outputFolder.toPath())
	    .sorted(Comparator.reverseOrder())
	    .map(Path::toFile)
	    .forEach(File::delete);
		}
		
		File[] subFolders = folder.listFiles();
		int nSpeakers = 10;
		LinkedHashMap<String, String> spk2Gender = new LinkedHashMap<>();
		LinkedHashMap<String, String> wav_scp = new LinkedHashMap<>();
		LinkedHashMap<String, String> text = new LinkedHashMap<>();
		LinkedHashMap<String, String> utt2spk = new LinkedHashMap<>();
		LinkedHashSet<String> corpus = new LinkedHashSet<>();
		
		for (File subfolder : subFolders) {
			
			if (subfolder.getName().equals("sil") || subfolder.getName().equals("sp"))
				continue;
				
			System.out.println("Analysing "+subfolder.getName());
			File[] subFiles = subfolder.listFiles();
			int spkIdx = 1;
			int filesCounter = 0;
			String transcription = subfolder.getName();
			
			int nwaves = 0;
			for (File file : subFiles) {
				
				if (file.getName().endsWith(".wav")) {
					nwaves++;
				}
				
			}
			
			System.out.println("# waves "+nwaves);
			
			int nFilesPerSpeaker = (int) (nwaves/nSpeakers);
			
			System.out.println("Distributing "+nFilesPerSpeaker+" files per speaker");
			
			if (nwaves%nSpeakers!=0)
				nFilesPerSpeaker ++;
			
			System.out.println("Distributing really "+nFilesPerSpeaker+" files per speaker");
			for (File file : subFiles) {
			
				if (file.getName().endsWith(".wav")) {
					
					System.out.println("Speaker "+spkIdx+" "+file.getName());
					String speaker = "spk_"+spkIdx;
					spk2Gender.put(speaker, "m");
					String waveCode = "spk_"+spkIdx+"_"+transcription+"_"+filesCounter;
					wav_scp.put(waveCode, file.getAbsolutePath());
					text.put(waveCode, transcription);
					utt2spk.put(waveCode, speaker);
					corpus.add(transcription);
					
					filesCounter++;
					if (filesCounter == nFilesPerSpeaker ) {
						//spkIdx++;
						//filesCounter=0;
					}

				}
			}
			
			

		}
		
		
		if (!outputFolder.exists())
			outputFolder.mkdir();
		

		File dataFolder = new File(outputFolder.getParent()+"/data/");
		if (!dataFolder.exists())
			dataFolder.mkdir();
		
		
		for (String spk:spk2Gender.keySet()) {
			
			File spkFolder = new File(outputFolder,spk);	
			
			if (!spkFolder.exists())
				spkFolder.mkdir();
			
			
			
		}
		
		for (String spk:spk2Gender.keySet()) {
			
				
			FileWriter fw = new FileWriter(new File(outputFolder,"spk2gender"),true); 
			fw.write(spk+" m"+"\n");
			fw.close();
			
		}

		for (String wavID:wav_scp.keySet()) {
			
			
			
			File source = new File(wav_scp.get(wavID));
			
			File out = new File(outputFolder,utt2spk.get(wavID));
			out = new File(out,wavID+".wav");
			Files.copy(source.toPath(), out.toPath());
			
			FileWriter fw = new FileWriter(new File(outputFolder,"wav.scp"),true);
			
			fw.write(wavID+" "+prefix+out.getName()+"\n");
			
			fw.close();
			
			
		}

		
		for (String t:text.keySet()) {
			
			FileWriter fw = new FileWriter(new File(outputFolder,"text"),true);
			
			fw.write(t+" "+text.get(t)+"\n");
			
			fw.close();
			
		}
		
		
		for (String t:utt2spk.keySet()) {
			
			FileWriter fw = new FileWriter(new File(outputFolder,"utt2spk"),true);
			
			fw.write(t+" "+utt2spk.get(t)+"\n");
			
			fw.close();
			
		}

		
		for (String t:corpus) {
			
			FileWriter fw = new FileWriter(new File(dataFolder,"corpus.txt"),true);
			
			fw.write(t+"\n");
			
			fw.close();
			
		}
	
	}

}
