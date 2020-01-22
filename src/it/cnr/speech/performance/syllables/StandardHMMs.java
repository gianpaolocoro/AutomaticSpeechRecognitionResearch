package it.cnr.speech.performance.syllables;

import java.io.File;
import java.util.HashMap;

import it.cnr.speech.hmm.SingleHMM;

public class StandardHMMs {

	public static void main(String[] args) throws Exception{
		File hmmsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\hmms\\");
		File [] allhmms = hmmsFolder.listFiles();
		HashMap<String,SingleHMM> map = new HashMap<>(); 
		for (File hmmf:allhmms) {
			if (hmmf.getName().endsWith(".hmm")) {
				SingleHMM hmm = new SingleHMM(hmmf);
				map.put(hmmf.getName().replace(".hmm", ""),hmm);
			}
		}
		File testFolder =  new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test\\");
		File allSubfolders [] = testFolder.listFiles();
		int overallCross = 0;
		int recognizedCross = 0;
		
		for (File subfolder:allSubfolders) {
			
			
			String goldname = subfolder.getName();
			System.out.println("Gold syllable: "+goldname);
			
			File[] subFiles = subfolder.listFiles();
			
			int overall = 0;
			int recognized = 0;
			
			for (File subf:subFiles) {
				
				if (subf.getName().endsWith(".wav")) {
					double maxLike = -Double.MAX_VALUE;
					String bestModel = "";
						
					for (String key:map.keySet()) {
						
						SingleHMM hmm = map.get(key);
						double like = -1;
						try {
							like = hmm.calcLikeWave(subf);
						}catch(Exception e) {
							System.out.println("Too short signal");
						}
						
						if (like==-1) {
							bestModel = "sp";
							break;
						}
						if (like>maxLike) {
							maxLike = like;
							bestModel = key;
							
						}
						
					}
					
					System.out.println(goldname+":"+subf.getName()+"->"+bestModel);
					
					if (bestModel.equals(goldname)) {
						recognized++;
					}
					
					overall++;
				}
				
			}
			
			overallCross += overall;
			recognizedCross += recognized;
			double accuracy = (double) recognizedCross*100d/(double) overallCross;	
			System.out.println("Partial acc: "+accuracy+" "+recognizedCross+"/"+overallCross);
		}
		
		double accuracy = (double) recognizedCross*100d/(double) overallCross;
		
		System.out.println("Accuracy "+accuracy);
		
	}

}
