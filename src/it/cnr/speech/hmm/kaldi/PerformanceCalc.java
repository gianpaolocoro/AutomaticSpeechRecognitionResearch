package it.cnr.speech.hmm.kaldi;

import java.io.File;
import java.util.HashMap;

import it.cnr.speech.fhmm.matlab.OSCommands;

public class PerformanceCalc {
	
	public static String transform(String syllable) {
		
		if (syllable.equals("the"))
			return "di";
		else if (syllable.equals("[laughter]"))
			return "di";
		else if (syllable.equals("[yeah]"))
			return "dje";
		
		else return syllable;
	}
	
	public static void main(String[] args) throws Exception{
		File testFolder =  new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test\\");
		File allSubfolders [] = testFolder.listFiles();
		int overallCross = 0;
		int recognizedCross = 0;
		int overallFolders = 0;
		
		for (File subfolder:allSubfolders) {
			
			
				
			String goldname = subfolder.getName();
			System.out.println("Gold syllable: "+goldname);
			System.out.println(goldname+"-#"+overallFolders);
			
			if (overallFolders<0) {
				overallFolders++;
				continue;
			}
			
			File[] subFiles = subfolder.listFiles();
			int overall = 0;
			int recognized = 0;
			HashMap<String, Integer> confusion = new HashMap<>();
			
			for (File subf:subFiles) {
				
				
				if (subf.getName().endsWith(".wav")) {
					
					String bestModel = "";
					String syll = OSCommands.executeCommandForceAndGetResult("cmd /c sox \""+subf.getAbsolutePath()+"\" -t raw -c 1 -b 16 -r 8k -e signed-integer - | nc 146.48.87.169 5050");
					bestModel = transform(syll.trim());
					
					System.out.print("Recognized "+bestModel+" vs "+goldname);
					if (bestModel.equals(goldname)) {
						System.out.println(" OK");
						recognized++;
					}else
					{
						System.out.println(" KO");
						Integer score = confusion.get(bestModel);
						if (score == null)
							score = 0;
						
						confusion.put(bestModel,score+1);
					}
					
					overall++;
					if (overall>20)
						break;
				}
				
				
			}
			
			System.out.println(goldname+" confusion:\n"+confusion.toString());
			
			overallCross += overall;
			recognizedCross += recognized;
			double saccuracy = (double) recognized*100d/(double) overall;
			double accuracy = (double) recognizedCross*100d/(double) overallCross;
			System.out.println("Syllable acc: "+saccuracy+" "+recognized+"/"+overall);
			System.out.println("Partial acc: "+accuracy+" "+recognizedCross+"/"+overallCross);
			overallFolders++;
			//System.exit(0);
		}
		
		double accuracy = (double) recognizedCross*100d/(double) overallCross;
		
		System.out.println("Accuracy "+accuracy);
		
	}

}
