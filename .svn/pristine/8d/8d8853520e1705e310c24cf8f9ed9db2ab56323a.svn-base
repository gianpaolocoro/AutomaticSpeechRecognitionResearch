package it.cnr.speech.fhmm.abla;
import it.cnr.speech.fhmm.math.Matrix;


/**
 * Questa classe serve per costruire una Factorial Hmm da una matrice di feateres (mfcc).
 * @author  Santo
 * @version 1.0
 */
public class FHMM
{
	/**
	 * @uml.property  name="x"
	 * @uml.associationEnd  
	 */
	private Matrix X;
	/**
	 * @uml.property  name="xX"
	 * @uml.associationEnd  
	 */
	private Matrix XX;
	
	/**
	 * @uml.property  name="mu"
	 * @uml.associationEnd  
	 */
	private Matrix Mu;
	/**
	 * @uml.property  name="cov"
	 * @uml.associationEnd  
	 */
	private Matrix Cov;		// Matrice di Covarianza 
	/**
	 * @uml.property  name="p"
	 * @uml.associationEnd  
	 */
	private Matrix P;
	/**
	 * @uml.property  name="pi"
	 * @uml.associationEnd  
	 */
	private Matrix Pi;

	/** Numero di stati */
	public int K;
	
	/** Numero di catene */
	public int M;		 	
	
	/**
	 * Numero di coefficienti
	 * @uml.property  name="p"
	 */
	public int p;			
	
	
	/**
	 * Costruisce una Fhmm da una matrice di feateres.
	 * 
	 * @param mfcc � la matrice dei coefficienti
	 * @param numStati � il numero di stati
	 * @param numCatene � il numero di catene
	 */
	public FHMM(double mfcc[][], int numStati, int numCatene)
	{
		if (mfcc== null) return;
		
		X = new Matrix(mfcc);
		
		K	 = numStati;
		M	 = numCatene;
		
		p	 = mfcc[0].length;
		
		// setto la Matrice COV
		Cov = X.cov();
		for(int i=0; i<Cov.getColumnDimension(); i++)
			for (int j=0; j<Cov.getRowDimension(); j++)
				if (i!=j)	
					Cov.set(i, j, 0);
			
		// setto la matrice XX
		XX = (Matrix)X.transpose().times(X);
		for(int i=0; i<XX.getColumnDimension(); i++)
			for (int j=0; j<XX.getRowDimension(); j++)
				XX.set(i, j, XX.get(i, j)/X.getRowDimension());
		
		// setto la matrice della media iniziale Mu
		
		Matrix randn = new Matrix(M*K, p);
		randn.randomUniforme();
		
		Matrix sqrtm = new Matrix(Cov.getArray());
		sqrtm = sqrtm.sqrtmDiagonale();
		
		Matrix vettUnitario	 = new Matrix(K*M,1);
		vettUnitario.ones();
		
		Matrix mean	 = (Matrix)X.mean(1);
		
		Matrix tmp_a = (Matrix)randn.times(sqrtm);
		Matrix tmp_b = (Matrix)vettUnitario.times(mean);
		tmp_a.times(1/M);
		tmp_b.times(1/M);
		
		Mu = (Matrix)tmp_a.plus(tmp_b);
		
		// questa istruzione serve per effettuare i test con una MuFissa.
//		Mu = new Matrix (  InOut.loadMatrice("s:\\muFissa.txt", 14, 39)  )  ;
		
		// setto la matrice delle probabilit� a priori
		Pi = new Matrix(K,M);
		for (int j=0; j<M; j++)
			Pi.set(0, j, 1);
		
		// Matrice di transizione iniziale
		// viene impostato il modello di Bakis - left to rigth
		
		P  = new Matrix(K*M,K);
		for (int j=0; j<K; j++)
			P.set(j, j, 0.5);
		for (int j=0; j<K-1; j++)
			P.set(j, j+1, 0.5);
		P.set(K-1, K-1, 1);
		
		P.replicaRighe(K, M);
	}
	
	/**
	 * Preleva la matrice delle features.
	 * @return  le features.
	 * @uml.property  name="x"
	 */
	public Matrix getX()
	{
		return this.X;
	}
	
	/**
	 * Preleva la matrice di covarianza.
	 * @return  la matrice di covarianza.
	 * @uml.property  name="cov"
	 */
	public Matrix getCov()
	{
		return Cov;
	}
	
	/**
	 * Preleva la matrice Pi.
	 * @return
	 * @uml.property  name="pi"
	 */
	public Matrix getPi()
	{
		return Pi;
	}

	/**
	 * Preleva la matrice P.
	 * @return
	 * @uml.property  name="p"
	 */
	public Matrix getP()
	{
		return P;
	}
	
	/**
	 * Preleva la matrice Mu.
	 * @return
	 * @uml.property  name="mu"
	 */
	public Matrix getMu()
	{
		return Mu;
	}
	
	/**
	 * Preleva la matrice XX.
	 * @return
	 * @uml.property  name="xX"
	 */
	public Matrix getXX()
	{
		return XX;
	}
}