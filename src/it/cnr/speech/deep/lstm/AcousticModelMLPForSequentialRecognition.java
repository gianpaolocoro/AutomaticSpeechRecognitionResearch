package it.cnr.speech.deep.lstm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;
import it.cnr.speech.fhmm.matlab.OSCommands;
import it.cnr.speech.performance.AcousticModel;

public class AcousticModelMLPForSequentialRecognition extends AcousticModel{

	public AcousticModelMLPForSequentialRecognition(String modelName) {
		super(modelName);		
	}
	
	public static File model = new File("/home/gp/Desktop/ASR/deeplearning/MLP/mlp_h-300_out_channels-64_lr-001_ba-256_opt-adam_w-2-4-5-6_dp-0.5/best_model.pth");
	public static File DLengine = new File("/home/gp/Desktop/ASR/syllables-classifiers/");
	
	public static String templateCall = "python3.6 test_single_csv.py -l -ck #MODEL# -f #INPUT# -o #OUTPUT#";
	
	@Override
	public double calcLikelihood(double[][] features) {
		File tempMFCCFile = new File("temp_mfcc"+UUID.randomUUID()+".csv");
		
		String additionalInfo = features.length+"_"+features[0][0]+"_"+features[0][1]+"_"+features[1][0]+"_"+features[1][1]+"_"+features[features.length-1][0]+"_"+features[features.length-1][1];
		Double score = getScore(modelName+";"+additionalInfo); 
		if (score!=null)
			return score.doubleValue();
		
		File tempOutputFile = new File("o_"+additionalInfo+".csv");
		
		if (tempOutputFile.exists()) {
			String output = "";
			try {
				output = new String (Files.readAllBytes(tempOutputFile.toPath()));
			} catch (IOException e) {

				e.printStackTrace();
			}
			parseOutput(output, additionalInfo);
			double modelscore = getScore(modelName+";"+additionalInfo);
			//System.out.println(modelscore);
			return  modelscore;
		}else {
			System.out.println("WARNING: Precalculation not present for "+additionalInfo);
		}
		
		
		try {
			Conversions.saveMFCCs(features, tempMFCCFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//String call = "cd "+DLengine.getAbsolutePath()+"| "+templateCall.replace("#MODEL#", model.getAbsolutePath()).replace("#INPUT#", tempMFCCFile.getAbsolutePath()).replace("#OUTPUT#", tempOutputFile.getAbsolutePath());
		String cd = "cd "+DLengine.getAbsolutePath();
		
		String call = templateCall.replace("#MODEL#", model.getAbsolutePath()).replace("#INPUT#", tempMFCCFile.getAbsolutePath()).replace("#OUTPUT#", tempOutputFile.getAbsolutePath());
		
		
		String execution = "";
		String [] aaa = {"sh",cd,call};
		execution = OSCommands.executeOSCommands(aaa);
		
		//System.out.println("Execution: "+execution);
		
		if (execution.contains("\"num_layers={}\"."))
		{
			String output = ""; 
					
			try {
				output = new String (Files.readAllBytes(tempOutputFile.toPath()));
				parseOutput(output, additionalInfo);
				return getScore(modelName+";"+additionalInfo); 
			} catch (IOException e) {
				e.printStackTrace();
			}
				//System.out.println("Output "+output);
			
			
		}
		
		
		
		tempMFCCFile.delete();
		tempOutputFile.delete();
		
		return 0;
	}
	
	
	public static String prepareLikelihood(double[][] features) {
		File tempMFCCFile = new File("temp_mfcc"+UUID.randomUUID()+".csv");
		
		String additionalInfo = features.length+"_"+features[0][0]+"_"+features[0][1]+"_"+features[1][0]+"_"+features[1][1]+"_"+features[features.length-1][0]+"_"+features[features.length-1][1];
		File tempOutputFile = new File("o_"+additionalInfo+".csv");
		
		try {
			Conversions.saveMFCCs(features, tempMFCCFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String call = templateCall.replace("#MODEL#", model.getAbsolutePath()).replace("#INPUT#", tempMFCCFile.getAbsolutePath()).replace("#OUTPUT#", tempOutputFile.getAbsolutePath());
		return call;
	}
	
	public static void preCalcLikelihoods(List<double[][]> features) throws Exception{
		String sh[] = {"sh","rm *.csv"};
		
		File shFile = new File("precalc.sh");
		OSCommands.executeOSCommands(sh);
		if (shFile.exists())
			shFile.delete();
		
		
			
			FileWriter fw1 = new FileWriter(shFile, true);
			fw1.write("#!/bin/sh\n");
			fw1.write("cd "+DLengine.getAbsolutePath()+"\n");
			fw1.close();
			int counter = 0;
			
			for(double[][] matrix:features) {
				
				String call = prepareLikelihood(matrix);
				FileWriter fw = new FileWriter(shFile, true);
				fw.write(call+" &\n");
				fw.close();
				if (counter % 15 == 0) {
					fw = new FileWriter(shFile, true);
					fw.write("wait\n");
					fw.close();
				}
				counter ++;
			}
			FileWriter fw = new FileWriter(shFile, true);
			fw.write("wait\n");
			fw.close();
		System.out.println("Calculated "+(counter-1)+" combinations ");
		System.out.println("Running..");
		String sh2[] = {"sh","chmod 777 "+shFile.getAbsolutePath(),shFile.getAbsolutePath()};
		//String sh2[] = {"sh","chmod 777 "+shFile.getAbsolutePath()};
		OSCommands.executeOSCommands(sh2);
		System.out.println("All executed");
		
	}
	
	
	static HashMap<String,Double> scores = new HashMap<>(); 
	public static synchronized void addScore(String key,double score) {
		scores.put(key, score);
	}
	
	public static synchronized Double getScore(String key) {
		return scores.get(key);
	}
	
	public void parseOutput(String output, String additionalInfo) {
		
		String[] lines = output.split("\n");
		
		for (String line:lines) {
			if (line.contains(" class ")) {
				line = line.replaceAll(" +", " ");
				String [] elements = line.split(" ");
				String syllClass = elements[2];
				String prob = elements[4];
				//String prob = elements[0];
				Double p = Double.parseDouble(prob);
					//p = p+100;
				p = p+1;
				if (p<=0)
					p = 0d;
				
				String key = syllClass+";"+additionalInfo;
				addScore(key,p);
			}
		}
		
	}
	

}
