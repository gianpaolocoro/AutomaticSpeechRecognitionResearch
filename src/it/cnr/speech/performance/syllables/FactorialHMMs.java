package it.cnr.speech.performance.syllables;

import java.io.File;
import java.util.HashMap;

import it.cnr.speech.fhmm.abla.AcousticModelFHMM;
import it.cnr.speech.fhmm.abla.FHMMTrainer;

public class FactorialHMMs {

	public static void main(String[] args) throws Exception{
		File hmmsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\fhmms_7\\");
		File [] allhmms = hmmsFolder.listFiles();
		HashMap<String,AcousticModelFHMM> map = new HashMap<>(); 
		for (File hmmf:allhmms) {
			if (hmmf.getName().endsWith(".fhmm")) {
				System.out.println("Loading "+hmmf.getName());
				String hmmName = hmmf.getName().replace(".fhmm", "");
				AcousticModelFHMM hmm = new AcousticModelFHMM(hmmName,hmmf);
				map.put(hmmName,hmm);
			}
		}
		
		File testFolder =  new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test\\");
		File allSubfolders [] = testFolder.listFiles();
		int overallCross = 0;
		int recognizedCross = 0;
		int overallFolders = 0;
		
		for (File subfolder:allSubfolders) {
			
			if (subfolder.getName().equals("sil"))
				continue;
			
			if (!subfolder.getName().equals("tu"))
				continue;
			
			String goldname = subfolder.getName();
			System.out.println("Gold syllable: "+goldname);
			System.out.println(goldname+"-#"+overallFolders);
			
			if (overallFolders<0) {
				overallFolders++;
				continue;
			}
			//else
				//System.exit(0);
			
			
			File[] subFiles = subfolder.listFiles();
			
			
			int overall = 0;
			int recognized = 0;
			
			
			HashMap<String, Integer> confusion = new HashMap<>();
			
			for (File subf:subFiles) {
				
				
				if (subf.getName().endsWith(".wav")) {
					double maxLike = -Double.MAX_VALUE;
					String bestModel = "";
						
					for (String key:map.keySet()) {
						
						AcousticModelFHMM hmm = map.get(key);
						double like = -1;
						try {
							double [][] features = new FHMMTrainer().extractFeatures(subf);
							like = hmm.calcLikelihood(features);
							//System.out.println(key+"->"+like);
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
					
					
					if (bestModel.equals(goldname)) {
						recognized++;
					}else
					{
						Integer score = confusion.get(bestModel);
						if (score == null)
							score = 0;
						
						confusion.put(bestModel,score+1);
					}
					
					overall++;
					
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
