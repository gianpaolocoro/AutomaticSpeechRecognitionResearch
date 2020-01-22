package it.cnr.speech.fhmm.matlab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class MatlabInvoker {

	// line commands examples
	// matlab -nodisplay -nosplash -nodesktop -r
	// "cd('D:\WorkFolder\Experiments\Speech Recognition
	// Speecon\factorialhmm_GHAHRAMANI'); trainfhmm('1.csv',10,'test'); exit;"
	// matlab -nodisplay -nosplash -nodesktop -r
	// "cd('D:\WorkFolder\Experiments\Speech Recognition
	// Speecon\factorialhmm_GHAHRAMANI'); testfhmm('1.csv','test');exit;"

	public static String scriptsFolder = "D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\factorialhmm_GHAHRAMANI";

	public static String generateTrainingInstruction(String file, int chunksLength, String hmmname) {
		String command = "matlab -nodisplay -nosplash -nodesktop -r \"cd('" + scriptsFolder + "'); trainfhmm('" + file
				+ "'," + chunksLength + ",'" + hmmname + "'); exit;\"";
		return command;
	}

	public static String generateTrainingInstruction(String file, String chunksLength, String hmmname) {
		String command = "matlab -nodisplay -nosplash -nodesktop -r \"cd('" + scriptsFolder + "'); trainfhmm_varT('" + file
				+ "','" + chunksLength + "','" + hmmname + "'); exit;\"";
		return command;
	}
	
		
	public static String generateTestInstruction(String file, String hmmname) {
		String command = "matlab -nodisplay -nosplash -nodesktop -r \"cd('" + scriptsFolder + "'); testfhmm('" + file
				+ "','" + hmmname + "'); exit;\"";
		return command;
	}

	public static String generateTestInstruction(String file, List<String> hmmnames) {
		String command = "matlab -nodisplay -nosplash -nodesktop -r \"cd('" + scriptsFolder + "');";

		for (String hmmname : hmmnames)
			command += "testfhmm('" + file + "','" + hmmname + "');";

		command += " exit;\"";
		return command;
	}

	public static String generateInstruction(String instruction) {
		String command = "matlab -nodisplay -nosplash -nodesktop -r \"cd('" + scriptsFolder + "');";

		command += instruction;

		command += " exit;\"";
		return command;
	}

	public static String generateTestBucket(String file, List<String> hmmnames) {
		String command = "";

		for (String hmmname : hmmnames)
			command += "testfhmm('" + file + "','" + hmmname + "');\n";

		command += "";
		return command;
	}

	public static boolean isToTrain(String hmmname) {

		File checkFile = new File(scriptsFolder, "Mu_" + hmmname + ".mat");

		if (checkFile.exists())
			return false;
		else
			return true;

	}

	
	public static void runFHMMTraining(double[][] mat, Integer [] chunksLength, String hmmname) throws Exception {

		File file = new File(scriptsFolder, "training_" + hmmname + ".csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int nrows = mat.length;

		int ncols = mat[0].length;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mat[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();
		
		File fileT = new File(scriptsFolder, "training_" + hmmname + "_T.csv");
		bw = new BufferedWriter(new FileWriter(fileT));
		nrows = chunksLength.length;
		ncols = 1;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");

		
		for (int i = 0; i < nrows; i++) {

			 
			
				bw.write("" + chunksLength[i]);
			
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();

		String train = generateTrainingInstruction(file.getName(), fileT.getName(), hmmname);
		
		String commands = "cmd /c " + train;
		File checkFile = new File(scriptsFolder, "Mu_" + hmmname + ".mat");

		if (checkFile.exists())
			checkFile.delete();

		OSCommands.executeCommandForce(commands);
		int waitingTime = 0;

		while (!checkFile.exists()) {
			Thread.sleep(500);
			waitingTime += 500;

			if (waitingTime % 5000 == 0)
				System.out.println("Waiting for training to finish");

			if (waitingTime > 60 * 1 * 1000) {
				System.out.println("Timeout");
				break;
			}
		}

	}

	
	public static void runFHMMTraining(double[][] mat, int chunksLength, String hmmname) throws Exception {

		File file = new File(scriptsFolder, "training_" + hmmname + ".csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int nrows = mat.length;

		int ncols = mat[0].length;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mat[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();

		String train = generateTrainingInstruction(file.getName(), chunksLength, hmmname);
		String commands = "cmd /c " + train;
		File checkFile = new File(scriptsFolder, "Mu_" + hmmname + ".mat");

		if (checkFile.exists())
			checkFile.delete();

		OSCommands.executeCommandForce(commands);
		int waitingTime = 0;

		while (!checkFile.exists()) {
			Thread.sleep(500);
			waitingTime += 500;

			if (waitingTime % 5000 == 0)
				System.out.println("Waiting for training to finish");

			if (waitingTime > 60 * 1 * 1000) {
				System.out.println("Timeout");
				break;
			}
		}

	}

	public static double runFHMMTest(double[][] mat, String hmmname) throws Exception {

		File file = new File(scriptsFolder, "test_" + hmmname + ".csv");

		if (file.exists())
			file.delete();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int nrows = mat.length;

		int ncols = mat[0].length;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mat[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();

		String train = generateTestInstruction(file.getName(), hmmname);

		String commands = "cmd /c " + train;

		File checkFile = new File(scriptsFolder, "Like_" + hmmname + "_" + file.getName());

		if (checkFile.exists())
			checkFile.delete();

		OSCommands.executeCommandForce(commands);
		int waitingTime = 0;

		while (!checkFile.exists()) {
			Thread.sleep(10);
			waitingTime += 10;

			if (waitingTime % 5000 == 0)
				System.out.println("Waiting for test to finish");

			if (waitingTime > 60 * 1 * 1000) {
				System.out.println("Timeout");
				break;
			}
		}

		double like = Double.parseDouble(new String(Files.readAllBytes(checkFile.toPath())));
		return like;
	}

	public static String prepareFHMMTest(double[][] mat, List<String> hmmnames) throws Exception {

		File file = new File(scriptsFolder, "test_" + UUID.randomUUID() + ".csv");

		if (file.exists())
			file.delete();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int nrows = mat.length;

		int ncols = mat[0].length;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mat[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();

		String train = generateTestBucket(file.getName(), hmmnames);

		return train;
	}

	public static String runFHMMTest(double[][] mat, List<String> hmmnames) throws Exception {

		File file = new File(scriptsFolder, "test_" + UUID.randomUUID() + ".csv");

		if (file.exists())
			file.delete();

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		int nrows = mat.length;

		int ncols = mat[0].length;

		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mat[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();

		String train = generateTestInstruction(file.getName(), hmmnames);

		String commands = "cmd /c " + train;

		File checkFile = new File(scriptsFolder, "Like_" + hmmnames.get(hmmnames.size() - 1) + "_" + file.getName());

		if (checkFile.exists())
			checkFile.delete();

		OSCommands.executeCommandForce(commands);
		int waitingTime = 0;

		while (!checkFile.exists()) {
			Thread.sleep(10);
			waitingTime += 10;

			if (waitingTime % 5000 == 0)
				System.out.println("Waiting for test to finish");

			if (waitingTime > 60 * 5 * 1000) {
				System.out.println("Timeout");
				break;
			}
		}

		double like = Double.parseDouble(new String(Files.readAllBytes(checkFile.toPath())));

		file.delete();

		String prefix = scriptsFolder + "/Like_#_" + file.getName();

		return prefix;
	}

}
