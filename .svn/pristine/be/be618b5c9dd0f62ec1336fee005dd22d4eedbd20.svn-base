package it.cnr.speech.fhmm.abla;

import java.util.Vector;

import it.cnr.speech.fhmm.math.Maths;
import it.cnr.speech.fhmm.math.Matrix;

/**
 * Questa classe serve per addestrare una Hmm.
 * @author  Santo
 * @version  1.0
 */
public class BaumWelchLearner {

	private int p = 39; //// numero dei coefficienti della matrice delle features (completa di detata e doppi delta
	
	// Hmm
	/**
	 * @uml.property  name="pi"
	 * @uml.associationEnd  
	 */
	private Matrix Pi; // matrice di transizione degli stati.
	/**
	 * @uml.property  name="p"
	 * @uml.associationEnd  
	 */ 
	private Matrix P;  //matrice delle probabilit�
	/**
	 * @uml.property  name="cov"
	 * @uml.associationEnd  
	 */
	private Matrix Cov;  // matrice di covarianza
	/**
	 * @uml.property  name="mu"
	 * @uml.associationEnd  
	 */
	private Matrix Mu; // matrice delle medie
	/**
	 * @uml.property  name="xX"
	 * @uml.associationEnd  
	 */
	private Matrix XX;  // matrice del prodotto matriciale tra la matrice delle feateres e la sua trasposta.
	
	// osservazioni
	/**
	 * @uml.property  name="x"
	 * @uml.associationEnd  
	 */
	private Matrix X;
	private int[] T;
	private int[] TIndex;
	private int tMax ;
	
	// expand
	/**
	 * @uml.property  name="mub"
	 * @uml.associationEnd  
	 */
	private Matrix Mub;
	/**
	 * @uml.property  name="pib"
	 * @uml.associationEnd  
	 */
	private Matrix Pib;
	/**
	 * @uml.property  name="pb"
	 * @uml.associationEnd  
	 */
	private Matrix Pb;

	
	// parametri (struttura Hmm)
	private int K ;
	private int M  ;
	private int KM ;
	private int KxM ;
	// variabili
	private final double K1 = Math.pow((2*Math.PI) , ((double)-p/2) ) ;
	
	// tolleranza 
	private double tolleranza = 0;//0.00001;
	private double lik =0 ;
	private double likbase =0;
	
	/**
	 * @uml.property  name="alpha"
	 * @uml.associationEnd  
	 */
	private Matrix alpha;
	/**
	 * @uml.property  name="beta"
	 * @uml.associationEnd  
	 */
	private Matrix beta ;
	/**
	 * @uml.property  name="gamma"
	 * @uml.associationEnd  
	 */
	private Matrix gamma ;
	/**
	 * @uml.property  name="eta"
	 * @uml.associationEnd  
	 */
	private Matrix eta;

	/**
	 * @uml.property  name="collapse"
	 * @uml.associationEnd  
	 */
	private Matrix collapse ;
	/**
	 * @uml.property  name="collapse2"
	 * @uml.associationEnd  
	 */
	private Matrix collapse2;
	private int dd[][];
	
	/**
	 * @uml.property  name="hmm"
	 * @uml.associationEnd  
	 */
	private Hmm hmm;
	
	// Costruttori----------------------------------------------------
	/**
	 * Con questo costruttore vengono settati tutti i parametri inerenti alla hmm da addestrare.
	 * @param hmm � la hmm da addestrare.
	 */
	public BaumWelchLearner(Hmm hmm)
	{
		this.hmm = hmm;
		this.Cov = new Matrix (hmm.Cov);
		this.Mu = new Matrix (hmm.Mu);
		this.Pi = new Matrix (hmm.Pi);
		this.P = new Matrix (hmm.P);
		this.K  =hmm.K;
		this.M = hmm.M;
		this.KM = (int)Math.pow(K, M);
		this.KxM = K*M;
	}

	/**
	 * Settaggio delle osservazioni che modellano la hmm.
	 * @param X � la matrice delle features. (Tutte)
	 * @param T � il vettore delle lunghezza delle osservazioni.
	 * @param TIndex � il vettore degli indici di inizio delle singole osservazioni nella matrice X.
	 * @param XX � la matrice ...
	 */
	public void setOsservazioni( Matrix X, int T[]  , int TIndex[], Matrix XX) 
	{
		this.XX = XX;
		this.X = X;
		this.T = T;
		this.TIndex = TIndex;
		this.tMax =  T [Maths.maxVectorIndex(this.T) ];
		
		// preallocazione delle matrici che servono per i calcoli. 
 		alpha = new Matrix(tMax,KM);
		beta = new Matrix(tMax,KM);
		gamma = new Matrix(tMax,KM);
		eta = new Matrix(tMax * K*M  , K*M );
		
		// matrici di permutazioni
 		collapse = new Matrix(KM,K*M);
 		collapse2 = new Matrix(KM,M*K*M*K);
 		
 		dd = Maths.generaPermutazioni(KM, K, M);
		
 		for (int i=0; i<KM; i++)
 		{
 			for (int j=0; j<M; j++)
 			{
 				collapse.set(i, (j*K) +dd[i][j]  , 1);
 				for (int l =0 ; l <M; l++)
 				{	
  					collapse2.set(i, (  (j*K) + dd[i][j] ) * M*K + ((l*K) + dd[i][l])  , 1);
 				}			
			}
 		}
	}
	

	/**
	 * Questo metodo serve per eseguire i cicli di addestramento.
	 * @param numeroDiIterazioni indica quanti cicli di addestramento eseguire.
	 * @param etichetta setta il nome della hmm.
	 * @param path � il percorso, nel file system, dove si vuole salvare la hmm binaria, testuale, ed il vettore delle distanze.
	 * @param salvataggio � il flag che determina se salvare o meno tutte le hmm durante tutte le iterazioni.
	 * @return la hmm addestrata.
	 */
	public Hmm learnAndSave(int numeroDiIterazioni,String etichetta)  
	{
		double distanze[] = new double[numeroDiIterazioni];
		
		for (int cycle=1; cycle<=numeroDiIterazioni; cycle++) // Attenzione!! il ciclo di interazione comincia da 1.
		{
			try
			{
				distanze[cycle-1] = iterate(cycle);  // assegnazione della distanza.
				hmm.iterazione = (int)cycle; // setta il num di iterazione
				hmm.nome = etichetta; 
			}
			catch(Exception toll)
			{
				System.out.println("Tolleranza raggiunta");
				cycle = numeroDiIterazioni + 1; // forzatura d'uscita.
			}
		}
		return hmm;
	}
	
	// metodo di iterazione dell'addestramento.
	private double iterate(int cycle)  throws Exception
	{	
		Matrix Gamma[] = new Matrix[T.length];
		Matrix GammaX = new Matrix(K*M,p);
		Matrix Eta = new Matrix(K*M,K*M);
		Matrix Scale = new Matrix ( tMax , 1);
		Matrix Xi = new Matrix(K*M, K);
			
		double scale[] = null;
			
		this.expand();
			
		// Ciclo su tutte le sequenze
		for (int n=0; n<T.length; n++)
		{
			Matrix B = new Matrix(T[n], KM );
			Matrix iCov = Cov.inverse();
			double K2 = K1 / Math.sqrt(Cov.det());
			for ( int l=0; l<KM ; l++)
			{
				// calcolo della d
					
				// copio T[n] volte la riga l-esima di Mub 
				Vector vect = new Vector();
				for (int h=0; h<T[n] ; h++)
				{	
					double aaa[] = Mub.getRowPackedCopySingolaRiga(l);
					vect.add(aaa);
				}
				Matrix	tmpM = new  Matrix(vect,false);
				// setto i limiti di una singola osservazione.
				int inizio = TIndex[n] - T[n];
				int fine = TIndex[n] -1;
					
				Matrix tmpM2 = X.getMatrix(TIndex[n] - T[n], TIndex[n] -1 , 0, ( p-1) );			
				Matrix d = tmpM.minus(tmpM2);
					
				// calcolo della B
				tmpM = d.times(iCov);
				tmpM2 = tmpM.timesElement(d);
				tmpM = tmpM2.sumRighe();
				tmpM = tmpM.times(-0.5);
				tmpM.exp();
				tmpM = tmpM.times(K2);
				B.setColonna(tmpM,0,l);
			}
			scale = new double[T[n]];
			
			forward(scale,n,B);  // operazione di forward setta scale e la matrice alpha.
			backward(scale,n,B); // operazione di backward setta beta.
			
//			InOut.salvaMatrice("alpha", alpha.getArray(), false);
//			InOut.salvaMatrice("beta", beta.getArray(), false);
//			InOut.salvaVettore("scale", scale);
			
			extimation(scale,n,Eta,GammaX,Scale,Gamma,Xi,B); // operazioni di addestramento per una singola osservazione.
		}
		// ok
			
		Eta.plusEquals(Eta.transpose()).timesEquals(0.5);
			
		// Quarta Pagina del codice Matlab
		double oldlik = this.lik;
		lik = Scale.sum();
		System.out.println("ciclo " + cycle + " log likelihood = " + lik);
			
		// verifica di convergenza.
			 
		if ( cycle < 3 )
			likbase = lik;
		else
		{	if (lik < oldlik-2 )
					System.out.println("Errore: Large likelihood violation ");
			else
			{
				if (lik - likbase < ( 1+tolleranza ) * (oldlik - likbase) )
					 throw new  Exception();
			}
		}
		
		// aggiornamento della hmm.
		
		// M Step
		Mu = Eta.pInv().times(GammaX);
		double div = 1/(double)X.getRowDimension();
		Matrix a1  = Mu.times(div);
		Matrix a2  = GammaX.transpose().times(a1);
		
		Cov = XX.minus(a2);
		Cov = Cov.plusEquals(Cov.transpose()).timesEquals(0.5);
		double dCov = Cov.det();
		if( dCov <=0 )
		{
			System.out.print("hh");			
			throw new Exception();
		}
		
		// calcolo la matrice P.
		for ( int i=0; i<K*M; i++)
		{
			double d1 = Xi.sumRiga(i);
			if (d1 ==0)
			{
				Matrix unaria = new Matrix(1,K);
				unaria.ones();
				unaria.timesEquals( (1/K) );
				P.setRiga(unaria, i);
				
			}
			else
			{
				Matrix tmp = Xi.times((1/d1)); // dovrebbe essere fatta su una sola riga
				P.setRighe(tmp, i, i);
			}
		}
		
		// calcolo Matrice Pi.
		Pi = new Matrix(K,M);
		for ( int i=0; i<T.length-1; i++ )
		{
			double vet[] = Gamma[i+1].getRowPackedCopySingolaRiga(0);
			Pi.plusEquals(Matrix.reshape(vet, K, M) ) ;
			
		}
		Pi.timesEquals(1/(double)T.length);
		
		// settaggi.
		hmm.Cov = Cov.getArray();
		hmm.Mu = Mu.getArray();
		hmm.P = P.getArray();
		hmm.Pi = Pi.getArray();
		
		return lik;
	}	

	
	private  void forward(double scale[],int n,Matrix B)
	{
		Matrix PibT = Pib.transpose();
		Matrix MatrRiga = PibT.timesSingolaRiga(0,0,B); // ottengo una vettore riga
		alpha.copiaRigainMatrice(MatrRiga,0);	
		
		scale[0] = alpha.sumRiga(0);
		alpha.timesScalareRiga(0, (double)(1/scale[0]));
		for (int i=1; i<T[n]; i++) 
		{
			Matrix MatrRiga2 = alpha.getMatrix(i-1, i-1, 0, alpha.getColumnDimension()-1);
			
			Matrix tmp = MatrRiga2.times(Pb);
			
			Matrix MatrRiga3 = tmp.timesSingolaRiga(0,i,B);
			alpha.copiaRigainMatrice(MatrRiga3,i);	
			scale[i] = alpha.sumRiga(i);
			alpha.timesScalareRiga(i, (double)(1/scale[i]));		
		}
	}
	
	
	private void backward(double scale[],int n, Matrix B)
	{
		//Calcolo della beta dalla fine all'inizio
		Matrix ones = new Matrix(1,KM);
		ones.ones();
		ones = ones.times(1/scale[T[n]-1]);
		beta.copiaRigainMatrice(ones, T[n]-1);
		for (int i=(T[n]-2); i>-1; i-- )
		{
			Matrix tmp = beta.timesSingolaRiga(i+1, i+1, B);
			Matrix tmp2 = tmp.times(Pb.transpose().times(1/scale[i]));
			beta.copiaRigainMatrice(tmp2, i);
		}
	}
	
	private void extimation(double scale[], int n,Matrix Eta,Matrix GammaX,Matrix Scale,Matrix Gamma[],Matrix Xi, Matrix B)
	{
		for (int i=0; i<T[n]; i++)
		{
			Matrix matriceRiga = alpha.timesSingolaRiga(i, i, beta);
			gamma.setRiga(matriceRiga, i);
		}
		gamma.rDiv(T[n]);
		
		// gamma1
		Matrix gamma1 = gamma.getMatrix(0, T[n]-1, 0, gamma.getColumnDimension()-1 );
		gamma1 = gamma1.times(collapse);

		for (int i=0; i<T[n]; i++)
		{
			for(int j =0; j<M;j++)
			{
				int inizio =  j*K;
				int fine = ((j+1)*K)-1;
				
				Matrix tmp = gamma1.getMatrix(i, i, inizio, fine);
				tmp = tmp.times( 1/tmp.sumRiga(0));
				gamma1.setMatrix(i, i, inizio, fine, tmp);
			}
		}
		
		// fine gamma 1
		
		// xi
		Matrix xi = new Matrix(M*K, K);
		
		for (int i=0; i<T[n]-1; i++)
		{
			Vector v = new Vector();
			v.add(alpha.getRowPackedCopySingolaRiga(i));
			Matrix tmp = new Matrix (  v, false);
			Matrix tmp2 = tmp.times(collapse).transpose();
			
			v = new Vector();
			v.add(beta.getRowPackedCopySingolaRiga(i+1));
			tmp = new Matrix (  v, false);
			
			v = new Vector();
			v.add(B.getRowPackedCopySingolaRiga(i+1));
			Matrix tmp3 = new Matrix (  v, false);
			
			Matrix tmp4 = tmp.timesElement(tmp3).times(collapse);
			Matrix t = tmp2.times(tmp4);

			
			for(int j =0; j<M;j++)
			{
				int inizio =  j*K;
				int fine = ((j+1)*K)-1;
				
				tmp = P.getMatrix(inizio, fine, 0, P.getColumnDimension()-1);
				
				tmp2 = t.getMatrix(inizio, fine, inizio, fine);
				
				Matrix t2 = tmp.timesElement(tmp2);
				t2 = t2.times(1/t2.sum());
				Matrix xPiccola = xi.getMatrix(inizio, fine, 0, xi.getColumnDimension()-1);
				xPiccola = xPiccola.plus(t2);
				xi.setMatrix(inizio, fine, 0, xi.getColumnDimension()-1, xPiccola);
			}			
		}

		// fine xi

		
		Matrix tmp = gamma.getMatrix(0, T[n]-1 , 0, gamma.getColumnDimension()-1);
		Matrix t = tmp.times(collapse2);
		
		//-----------------
		
		for(int i=0; i<T[n];i++)
		{
			int inizioD1 =  i*K*M;
			int fineD1 = ((i+1)*K*M ) -1 ;
			double v[] = t.getRowPackedCopySingolaRiga(i);
			eta.setMatrix(inizioD1, fineD1, 0, eta.getColumnDimension()-1, Matrix.reshape(v, M*K, M*K));
		
			for (int j=0; j<M; j++)
			{
				int inizioD2 =   i*K*M + j*K;
				int fineD2 = (i*K*M ) +  ((j+1)*K)-1  ;
				for (int l=0; l<M; l++)
				{
					if (j==l)
					{
						Matrix diag = gamma1.diagDaRiga(i, j*K, ((j+1)*K)-1);
						eta.setMatrix(inizioD2, fineD2, j*K, ((j+1)*K)-1, diag);
						
					}
					else
					{
						tmp = eta.getMatrix(inizioD2, fineD2, l*K, (l+1)*K-1);
						tmp = tmp.times(tmp.sum());
						eta.setMatrix(inizioD2, fineD2, l*K, (l+1)*K-1, tmp);
					}
				}
				
			}
			Eta.plusEquals(eta.getMatrix(inizioD1, fineD1, 0, eta.getColumnDimension()-1));
			
			int h = 0;
			if ( n!=0)
				h = TIndex[n-1];
			h += i;
			tmp = X.getMatrix(h, h, 0, X.getColumnDimension()-1); 
			Matrix g = gamma1.getMatrix(i, i, 0, gamma1.getColumnDimension()-1);
			
			Matrix gamma1T = g.transpose();
			
			GammaX.plusEquals(gamma1T.times(tmp));
		}

		
		tmp = Scale.getMatrix(0,T[n]-1, 0, 0);
		Vector v = new Vector();
		v.add(scale);
		Matrix sca = new Matrix(v,false);
		sca.log();
		tmp = tmp.plus(sca.transpose());
		Scale.setMatrix(0, T[n]-1, 0, 0, tmp);
		Gamma[n] = gamma1;
		Xi.plusEquals(xi);

	}
	
	private void expand()
	{
		Mub = new Matrix(this.KM,p);
		
		Pb = new Matrix(this.KM,this.KM);
		Pb.ones();
		
		Pib = new Matrix(this.KM,1);
		Pib.ones();
		
		for ( int i = 0; i<this.KM ; i++)
		{
			for (int l = 0; l <M; l++)
			{
				Mub.plusEqualsRiga(Mu, i, (l*K) + dd[i][l] );
				Pib.timesEqualsRiga(i, Pi.get( dd[i][l] , l ));
			}
			
			for (int j = 0 ; j<this.KM; j++)
					for (int l = 0; l <M; l++)
						Pb.set(i, j, Pb.get(i, j)* P.get((l*K )+dd[i][l], dd[j][l]) );					
		}
	}	

}
