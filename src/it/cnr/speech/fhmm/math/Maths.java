package it.cnr.speech.fhmm.math;

public class Maths {

   /** sqrt(a^2 + b^2) without under/overflow. **/
   public static double hypot(double a, double b) {

      double r;

      if (Math.abs(a) > Math.abs(b)) {

         r = b/a;

         r = Math.abs(a)*Math.sqrt(1+r*r);

      } else if (b != 0) {

         r = a/b;

         r = Math.abs(b)*Math.sqrt(1+r*r);

      } else {

         r = 0.0;

      }

      return r;

   }
   
	public static double eps(double num)
	{
		return Math.ulp(num);
	}

	
	  private static int[] base(int k,int m, int d)
	 	{
	 		int v[] = new int[d];
	 		int mm[] = new int[d];
	 		mm[d-1] = 1;
	 		if (d>1)	
	 			mm[d-2] = m;
	 		for (int i=d-3  ; i>-1; i--)
	 		{
	 			mm[i] =  mm[i+1] * m;   
	 		}
		   
	 		for (int i=0  ; i<d; i++)
	 		{
	 			v[i] = (int)Math.ceil(k/mm[i]);
	 			k = k - mm[i]* (int)v[i];

	 		}
		   return v;
	 	}
	   /*
	    * % function v=base(k,m,d)
		%
		% returns a vector of length d with (k base m) +1
		%
		% for example base(29,5,4)=[1 2 1 5]
		% (if d omitted returns shortest vector)

		function v=base(k,m,d)

		if nargin<3
	  	d=fix(log(k)/log(m)+1);
		end;  

		mm=m.^(d-1:-1:0);

		for i=1:d
	  	v(i)=fix(k/mm(i));
	  	k=k-mm(i)*v(i);
		end;

		v=v+1;
		*
		* @param k: 
		* @param m:
		* @param d:
		* 
	    * @return
	    */
	   
	   /**
	    * Generatore di permutazioni.
	    * @param righe
	    * @param K
	    * @param M
	    * @return la matrice di permutazioni.
	    */
		public static int[][] generaPermutazioni(int righe ,int K, int M )
		{
			int  dd[][] = new int[righe][M];
			for (int i = 0 ; i < (righe); i ++ )
				dd[i] = Maths.base(i, K, M); 
			
			return dd;
		}
		
		/**
		 * Trova l'indice che individua l'elemento massimo di un vettore di interi.
		 * @param vett � il vettore che contiene gli elementi.
		 * @return la posizione che contiene il Max valore.
		 */
		public static int maxVectorIndex(int vett[])
		{
			int maxValore = Integer.MIN_VALUE;
			int maxIndex = -1;
			
			for (int i=0; i<vett.length; i++)
				if ( vett[i] > maxValore)
				{
					maxValore = vett[i];
					maxIndex = i;
				
				}
			return maxIndex;
		}
		
		
		
}

