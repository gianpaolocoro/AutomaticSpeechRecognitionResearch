package it.cnr.speech.performance.syllables;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import it.cnr.speech.fhmm.matlab.AcousticModelFHMMMatlab;
import it.cnr.speech.fhmm.matlab.FHMMTrainerMatlab;
import it.cnr.speech.fhmm.matlab.MatlabInvoker;
import it.cnr.speech.fhmm.matlab.OSCommands;

public class FactorialHMMsMatlab {

	public static void main(String[] args) throws Exception {
		OSCommands.executeCommandForceIS(
				"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\Like_\"*");
		OSCommands.executeCommandForceIS(
				"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\test_\"*");
		OSCommands.executeCommandForceIS(
				"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\batch_\"*");

		File hmmsFolder = new File("D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI");
		File[] allhmms = hmmsFolder.listFiles();
		HashMap<String, AcousticModelFHMMMatlab> map = new HashMap<>();
		for (File hmmf : allhmms) {
			if (hmmf.getName().startsWith("Mu_")) {
				System.out.println("Loading " + hmmf.getName());
				String hmmName = hmmf.getName().replace(".mat", "").replace("Mu_", "");
				AcousticModelFHMMMatlab hmm = new AcousticModelFHMMMatlab(hmmName, hmmf);
				map.put(hmmName, hmm);
			}
		}

		File testFolder = new File(
				"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test\\");
		File allSubfolders[] = testFolder.listFiles();
		int overallCross = 0;
		int recognizedCross = 0;
		int overallFolders = 0;

		for (File subfolder : allSubfolders) {

		 if (subfolder.getName().equals("sil") || subfolder.getName().equals("sp"))
			 continue;

			//if (!subfolder.getName().equals("to"))
				//		continue;
			
			String goldname = subfolder.getName();
			System.out.println("Gold syllable: " + goldname);
			System.out.println(goldname + "-#" + overallFolders);

			if (overallFolders < 0) {
				overallFolders++;
				continue;
			}

			// else
			// System.exit(0);

			File[] subFiles = subfolder.listFiles();

			int count = 0;
			int filesToProduce = 0;

			HashSet<String> alreadyProd = new HashSet<>();
			boolean stop = false;
			
			StringBuffer template = new StringBuffer();

			for (File subf : subFiles) {
				if (subf.getName().endsWith(".wav")) {

					if (!alreadyProd.contains(subf.getName())) {
						double[][] features = new FHMMTrainerMatlab().extractFeatures(subf);
						template.append(
								MatlabInvoker.prepareFHMMTest(features, new ArrayList<String>(map.keySet())) + "\n");
						count++;

						filesToProduce += map.keySet().size();

						//if (count > 40)
							//break;
					}
				}
			}
			
			String code = "" + UUID.randomUUID();
			code = code.replace("-", "");

			File script = new File(MatlabInvoker.scriptsFolder, "batch_" + code + ".m");

			FileWriter fw = new FileWriter(script);
			fw.write(template.toString());
			fw.close();
			String instr = "run('" + script.getAbsolutePath() + "'); ";
			OSCommands.executeCommandForce2(MatlabInvoker.generateInstruction(instr));

			boolean finished = false;
			while (!finished) {

				File[] likeFiles = new File(MatlabInvoker.scriptsFolder).listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (name.startsWith("Like_"))
							return true;
						else
							return false;
					}
				});

				System.out.println("Produced so far " + likeFiles.length + " of " + filesToProduce);
				if (likeFiles.length >= filesToProduce)
					finished = true;
				else
					Thread.sleep(1000);
			}

			System.out.println("All produced");

			HashMap<String, HashMap<String, Double>> sessions = new HashMap<>();

			File folder = new File(MatlabInvoker.scriptsFolder);
			File[] allf = folder.listFiles();

			int countAllLikeFiles = 0;

			for (File f : allf) {

				if (f.getName().startsWith("Like_")) {

					String id = f.getName().substring(f.getName().lastIndexOf("_") + 1);
					String hmm = f.getName().substring(f.getName().indexOf("_") + 1, f.getName().lastIndexOf("_"))
							.replace("_test", "");
					double like = Double.parseDouble(new String(Files.readAllBytes(f.toPath())));
					//System.out.println("ID " + id + " H: " + hmm + " L: " + like);
					HashMap<String, Double> scores = sessions.get(id);

					if (scores == null)
						scores = new HashMap<>();

					scores.put(hmm, like);
					sessions.put(id, scores);
					countAllLikeFiles++;
				}
			}

			System.out.println("All Like files " + countAllLikeFiles);
			int overall = sessions.keySet().size();
			int recognized = 0;
			HashMap<String, Integer> confusion = new HashMap<>();
			for (String id : sessions.keySet()) {

				HashMap<String, Double> session = sessions.get(id);
				double maxScore = -Double.MAX_VALUE;
				double secondMaxScore = -Double.MAX_VALUE;
				
				String bestHmm = "";
				String secondBestHmm = "";
				
				for (String hmm : session.keySet()) {

					Double score = session.get(hmm);

					if (score > maxScore) {
						maxScore = score;
						bestHmm = hmm;
					}else if (score > secondMaxScore) {
						secondMaxScore = score;
						secondBestHmm = hmm;
					}

				}

				if (bestHmm.contentEquals(goldname)) {
					recognized++;
				}else
				{
					Integer score = confusion.get(bestHmm);
					if (score == null)
						score = 0;
					
					confusion.put(bestHmm,score+1);
				}

			}

			overallCross += overall;
			recognizedCross += recognized;
			double saccuracy = (double) recognized * 100d / (double) overall;
			System.out.println("Syllable acc: " + saccuracy + " " + recognized + "/" + overall);
			System.out.println("Confusion Matrix for " + goldname+ " : " + confusion );
			double accuracy = (double) recognizedCross * 100d / (double) overallCross;
			System.out.println("Partial acc: " + accuracy + " " + recognizedCross + "/" + overallCross);
			overallFolders++;

			OSCommands.executeCommandForceIS(
					"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\Like_\"*");
			OSCommands.executeCommandForceIS(
					"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\test_\"*");
			OSCommands.executeCommandForceIS(
					"cmd /c del \"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI\\batch_\"*");

			//System.exit(0);

		}

		double accuracy = (double) recognizedCross * 100d / (double) overallCross;

		System.out.println("Accuracy " + accuracy);

	}

	}
