package it.cnr.speech.fhmm.abla;

import java.io.*;
/**
 * Questa classe implementa il "Hidden Markov model". Esso incapula tutte le matrici necessarie che definiscono la Hmm, ed incapsula anche informazioni sulla tipologiia, quali: il numero di stati e di catene. 
 * @author Santo
 * @version 1.0
 */
public class Hmm implements Serializable
{
	/** Matrice di Covarianza. 	 */
	public double [][] Cov;

	/** Vettori delle medie. (rappresentazione matriciale). */
	public double [][] Mu;

	/** Priors.  */
	public double [][] Pi;
	
	/** Matrice di transizione degli stati. */
	public double [][] P;
	
	/** Label della Hmm. Default = "sconosciuto";	 */
	public String nome = "sconosciuto";

	/** Livello di addestramento. 	*/
	public int iterazione =0;

	/** Numero di Stati. 	 */
	public int K ;

	/** Numero di catene.	 */
	public int M  ; 
	

	/**
	 * Questo costruttore setta le matrici della Hmm.
	 * @param Cov
	 * @param Mu
	 * @param Pi
	 * @param P
	 */
	public Hmm(double[][] Cov, double[][] Mu, double[][] Pi, double[][] P )
	{
		this.Cov = Cov;
		this.Mu = Mu;
		this.P = P;
		this.Pi = Pi;
	}
	
	/**
	 * Costruisce una Hmm da fhmm.
	 * Setta le matrici Cov, Mu, Pi, e P. 
	 * Setta il numero degli stati, il numero di catene, e il numero di coefficienti prelevando i valori dalla fhmm.
	 * @param fhmm
	 */
	public Hmm(FHMM fhmm)
	{
		this.Cov	= fhmm.getCov().getArray();
		this.Mu		= fhmm.getMu().getArray(); 
		this.Pi 	= fhmm.getPi().getArray();
		this.P 		= fhmm.getP().getArray();
		this.K		= fhmm.K;
		this.M		= fhmm.M;

	}
	
}
