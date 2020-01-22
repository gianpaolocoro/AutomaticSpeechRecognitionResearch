package it.cnr.speech.speecon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import it.cnr.speech.audio.AudioBits;
import it.cnr.speech.audio.AudioWaveGenerator;
import it.cnr.speech.conversions.Conversions;
import it.cnr.speech.features.MfccExtraction;

public class CreateSillableCorpus {

	public static void main(String[] args) throws Exception {
		long t0 = System.currentTimeMillis();
		CreateSillableCorpus creator = new CreateSillableCorpus();
		creator.generateCorpus();
		long t1 = System.currentTimeMillis();
		float sec = (float)(t1-t0)/1000f;
		
		System.out.println("All done in "+sec+"s");

	}

	public static File TrainingCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\train");
	public static File TrainingSyllablesCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\train");

	public static File TestCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon connected digits\\test");
	public static File TestSyllablesCorpusFolder = new File(
			"D:\\WorkFolder\\Experiments\\Speech Recognition Speecon\\Speecon syllables\\test");

	class Interval {
		double msStart;
		double msEnd;
		long sampleStart;
		long sampleEnd;

		public Interval(double msStart, double msEnd, double samplingFrequency) {

			this.msStart = msStart;
			this.msEnd = msEnd;
			this.sampleStart = Conversions.millisecondsToSamples(samplingFrequency, msStart);
			this.sampleEnd = Conversions.millisecondsToSamples(samplingFrequency, msEnd);

		}
	}

	HashMap<String, List<Interval>> intervals = new HashMap<>();

	void reset() {
		intervals = null;
		intervals = new HashMap<>();
	}

	void parseRecFileLine(String line, double samplingFrequency) {

		String[] elements = line.split(" ");
		double msStart = Double.parseDouble(elements[0]) / 10000d;
		double msEnd = Double.parseDouble(elements[1]) / 10000d;
		String syllable = elements[2].trim();

		List<Interval> sylintervals = intervals.get(syllable);

		if (sylintervals == null) {
			sylintervals = new ArrayList<CreateSillableCorpus.Interval>();
		}

		System.out.println("Adding " + syllable + " " + msStart + ";" + msEnd);
		sylintervals.add(new Interval(msStart, msEnd, samplingFrequency));
		intervals.put(syllable, sylintervals);
	}

	void parseFile(File recFile, double samplingFrequency) throws Exception {

		List<String> lines = Files.readAllLines(recFile.toPath());
		for (String line : lines)
			parseRecFileLine(line, samplingFrequency);

	}

	void saveIntervals(short[] audiosignal, File outputFolder, AudioFormat format) throws Exception {

		for (String syllable : intervals.keySet()) {

			File outputFolderSyllable = new File(outputFolder, syllable);
			if (!outputFolderSyllable.exists())
				outputFolderSyllable.mkdir();

			List<Interval> sylIntervals = intervals.get(syllable);

			for (Interval interval : sylIntervals) {

				int s1 = (int) interval.sampleStart;
				int s2 = (int) interval.sampleEnd;
				int widx = 1;

				File wavefile = new File(outputFolderSyllable, widx + ".wav");

				while (wavefile.exists()) {
					widx++;
					wavefile = new File(outputFolderSyllable, widx + ".wav");
				}
				File mfccfile = new File(outputFolderSyllable, widx + ".csv");

				System.out.println(
						"Saving syllable " + syllable + " [" + s1 + ";" + s2 + "]" + "->" + wavefile.getName());

				AudioWaveGenerator.generateWaveFromSamples(Arrays.copyOfRange(audiosignal, s1, s2), wavefile, format);

				System.out.println("Saving mfcc for " + wavefile.getName() + " -> " + mfccfile);
				try{
					saveMFCCs(wavefile, mfccfile);
				}catch(Exception e) {
					System.out.println("Insufficient data - deleting "+wavefile);
					wavefile.deleteOnExit();
					if (mfccfile.exists())
						mfccfile.delete();
				}
			}

		}

	}

	
	int ncols = 0;

	public void saveMFCCs(File waveFile, File outputCSVFile) throws Exception {

		AudioBits ab = new AudioBits(waveFile);

		MfccExtraction extractor = new MfccExtraction(ab.getAudioFormat().getSampleRate());
		short[] signal = ab.getShortVectorAudio();
		
		
		
		double[][] mfccMatrix = null;

		try {
			mfccMatrix = extractor.extractMFCC(signal);
		} catch (Exception e) {
			System.out.println(
					"Error with wave for MFCC extraction " + waveFile.getName() + " : " + e.getLocalizedMessage());
			 mfccMatrix = new double[1][MfccExtraction.defaultNCoeff*3];
			 throw new Exception(e);
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputCSVFile));
		int nrows = mfccMatrix.length;

		
		int ncols = mfccMatrix[0].length;
		
		for (int j = 0; j < ncols; j++) {

			bw.write("F" + j);
			if (j < ncols - 1) {
				bw.write(",");
			}
		}

		bw.write("\n");
		for (int i = 0; i < nrows; i++) {

			for (int j = 0; j < ncols; j++) {

				bw.write("" + mfccMatrix[i][j]);
				if (j < ncols - 1) {
					bw.write(",");
				}

			}
			if (i < nrows - 1)
				bw.write("\n");
		}

		bw.close();
	}

	public void generateCorpus() throws Exception {

		File[] trainingFiles = TrainingCorpusFolder.listFiles();
		if (!TrainingSyllablesCorpusFolder.exists())
			TrainingSyllablesCorpusFolder.mkdir();
		File[] testFiles = TestCorpusFolder.listFiles();
		if (!TestSyllablesCorpusFolder.exists())
			TestSyllablesCorpusFolder.mkdir();

		harvestFiles(trainingFiles, TrainingSyllablesCorpusFolder);
		harvestFiles(testFiles, TestSyllablesCorpusFolder);

	}

	public void harvestFiles(File[] allFiles, File outputFolder) throws Exception {

		for (File file : allFiles) {

			if (file.getName().endsWith(".wav")) {
				System.out.println("###Analysing " + file.getName());
				reset();
				AudioBits bits = new AudioBits(file);
				AudioFormat format = bits.getAudioFormat();
				double samplingFrequency = format.getSampleRate();
				short[] audio = bits.getShortVectorAudio();
				File recFile = new File(file.getParentFile(), file.getName().replace(".wav", ".rec"));
				parseFile(recFile, samplingFrequency);
				saveIntervals(audio, outputFolder, format);
				//break;
			}

		}
	}

}
