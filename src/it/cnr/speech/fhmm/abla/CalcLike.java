package it.cnr.speech.fhmm.abla;

import java.util.Vector;

import it.cnr.speech.fhmm.math.Maths;
import it.cnr.speech.fhmm.math.Matrix;

/**
 * Serve per eseguire il riconoscimento tramite l'algoritmo di CalkLike che
 * ristituisce la stima della verosimiglianza.
 * 
 * @author Santo
 * @version 1.0
 */
public class CalcLike {

	/**
	 * @uml.property name="covarianza"
	 * @uml.associationEnd
	 */
	private Matrix covarianza;

	// Hmm
	/**
	 * @uml.property name="cov"
	 * @uml.associationEnd
	 */
	private Matrix Cov;
	/**
	 * @uml.property name="mub"
	 * @uml.associationEnd
	 */
	private Matrix Mub;
	/**
	 * @uml.property name="pib"
	 * @uml.associationEnd
	 */
	private Matrix Pib;
	/**
	 * @uml.property name="pb"
	 * @uml.associationEnd
	 */
	private Matrix Pb;

	// osservazioni
	/**
	 * @uml.property name="x"
	 * @uml.associationEnd
	 */
	private Matrix X;
	private int T;

	// parametri (struttura Hmm)
	private int K;
	private int M;
	private int p;
	private int KM;

	// variabili
	// private int numIterazioni = 2; // numero di cicli
	// private double tolleranza = 0.0001;
	private final double K1 = Math.pow((2 * Math.PI), ((double) -p / 2));
	private final double tiny = Double.MIN_VALUE;

	/**
	 * @uml.property name="lik"
	 */
	double lik = 0;

	/**
	 * @uml.property name="alpha"
	 * @uml.associationEnd
	 */
	private Matrix alpha;

	private int dd[][];

	// Costruttori----------------------------------------------------

	public CalcLike(Matrix X, Hmm hmm) {
		this.K = hmm.K;
		this.M = hmm.M;
		this.KM = (int) Math.pow(K, M);
		this.p = X.getColumnDimension();

		Matrix Cov = new Matrix(hmm.Cov);
		Matrix P = new Matrix(hmm.P);
		Matrix Mu = new Matrix(hmm.Mu);
		Matrix Pi = new Matrix(hmm.Pi);

		this.covarianza = Cov;

		this.X = X;
		this.T = X.getRowDimension();

		alpha = new Matrix(T, KM);

		dd = Maths.generaPermutazioni(KM, K, M);

		this.expand(Cov, Mu, Pi, P);
		double Scale[] = new double[T];

		Matrix B = new Matrix(T, KM);
		Matrix iCov = Cov.inverse();
		double K2 = K1 / Math.sqrt(Cov.det());

		for (int i = 0; i < T; i++) {
			for (int l = 0; l < KM; l++) {
				// calcolo della d

				// faccio la differenza tra la riga l-esima della Mub e la riga i-esima della X
				Vector vect = new Vector();
				double aaa[] = Mub.getRowPackedCopySingolaRiga(l);
				vect.add(aaa);
				Matrix tmpM = new Matrix(vect, false);

				Matrix tmpM2 = X.getMatrix(i, i, 0, (p - 1));

				Matrix d = tmpM.minus(tmpM2);

				// calcolo della B

				tmpM = d.times(iCov);
				tmpM2 = d.transpose();

				tmpM = tmpM.times(tmpM2);

				tmpM = tmpM.times(-0.5);
				tmpM.exp();

				tmpM = tmpM.times(K2);

				double num = tmpM.sum();

				B.set(i, l, num);
			}
		}

		double scale[] = new double[T];

		Matrix PibT = Pib.transpose();
		Matrix MatrRiga = PibT.timesSingolaRiga(0, 0, B); // ottengo una vettore riga
		alpha.copiaRigainMatrice(MatrRiga, 0);

		scale[0] = alpha.sumRiga(0);

		alpha.timesScalareRiga(0, (double) (1 / (scale[0] + tiny)));

		for (int i = 1; i < T; i++) {
			Matrix MatrRiga2 = alpha.getMatrix(i - 1, i - 1, 0, alpha.getColumnDimension() - 1);
			Matrix tmp = MatrRiga2.times(Pb);
			Matrix MatrRiga3 = tmp.timesSingolaRiga(0, i, B);
			alpha.copiaRigainMatrice(MatrRiga3, i);

			scale[i] = alpha.sumRiga(i);
			alpha.timesScalareRiga(i, (double) (1 / (scale[i] + tiny)));
		}

		for (int i = 0; i < scale.length; i++) {
			scale[i] = Math.log((scale[i] + tiny));
			Scale[i] += scale[i];
			lik += Scale[i];
		}

	}

	private void expand(Matrix Cov, Matrix Mu, Matrix Pi, Matrix P) {
		Mub = new Matrix(this.KM, p);

		Pb = new Matrix(this.KM, this.KM);
		Pb.ones();

		Pib = new Matrix(this.KM, 1);
		Pib.ones();

		for (int i = 0; i < this.KM; i++) {
			for (int l = 0; l < M; l++) {
				Mub.plusEqualsRiga(Mu, i, (l * K) + dd[i][l]);
				Pib.timesEqualsRiga(i, Pi.get(dd[i][l], l));
			}

			for (int j = 0; j < this.KM; j++)
				for (int l = 0; l < M; l++)
					Pb.set(i, j, Pb.get(i, j) * P.get((l * K) + dd[i][l], dd[j][l]));
		}
	}

	/**
	 * Preleva il valore di verosimiglianza.
	 * 
	 * @return il valore di verosimiglianza.
	 * @uml.property name="lik"
	 */
	public double getLik() {
		return lik;
	}

}
