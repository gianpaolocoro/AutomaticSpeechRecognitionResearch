package it.cnr.speech.conversions;

import java.sql.Time;


public class Converter {

	static int ordinePrecedente=-1;
	static int ordineAttuale;
	static int ordineMassimo = -1;
	static int nmax=0;
	static int indexmax=0;
	static double numberbest=0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long start = System.nanoTime();

		//System.out.println("wey "+(5.7683009E7-9));
		//System.out.println((int)Converti("cinquantasettemilioniseicentoottantatremilanovecentonovantaquattro"));
		//System.out.println((int)Converti("cinquantasettemilioniseicentoottantatremilanove"));
		//System.out.println((int)Converti("ottomilacentoottantatre"));
		//System.out.println((int)Converti("undicimilacinquecento"));
		System.out.println((double)string2num(args[0]));
		System.out.println("elapsed "+((double)(System.nanoTime()-start))/Math.pow(10, 6)+"ms");
	}
	
	static void Misura(String numstring){
		
		int n1 = Rules.unita.length;
		int n2 = Rules.decine.length;
		int n3 = Rules.centinaia.length;
		int n4 = Rules.mille.length;
		int n5 = Rules.milioni.length;
		int n6 = Rules.miliardi.length;
		
		nmax=0;
		indexmax=0;
		numberbest=0;
		
		for(int i=0;i<n1;i++){
			if (numstring.startsWith(Rules.unita[i])){
				if (Rules.unita[i].length()>nmax){nmax = Rules.unita[i].length();indexmax=i;numberbest=Rules.iunita[i];ordineAttuale=Rules.iunita[0];}
			}
		}//end for
		for(int i=0;i<n2;i++){
			if (numstring.startsWith(Rules.decine[i])){
				if (Rules.decine[i].length()>nmax){nmax = Rules.decine[i].length();indexmax=i;numberbest=Rules.idecine[i];ordineAttuale=Rules.idecine[0];}
			}
		}//end for
		for(int i=0;i<n3;i++){
			if (numstring.startsWith(Rules.centinaia[i])){
				if (Rules.centinaia[i].length()>nmax){nmax = Rules.centinaia[i].length();indexmax=i;numberbest=Rules.icentinaia[i];ordineAttuale=Rules.icentinaia[0];}
			}
		}//end for
		for(int i=0;i<n4;i++){
			if (numstring.startsWith(Rules.mille[i])){
				if (Rules.mille[i].length()>nmax){nmax = Rules.mille[i].length();indexmax=i;numberbest=Rules.imille[i];ordineAttuale=Rules.imille[0];}
			}
		}//end for
		for(int i=0;i<n5;i++){
			if (numstring.startsWith(Rules.milioni[i])){
				if (Rules.milioni[i].length()>nmax){nmax = Rules.milioni[i].length();indexmax=i;numberbest=Rules.imilioni[i];ordineAttuale=Rules.imilioni[0];}
			}
		}//end for
		for(int i=0;i<n6;i++){
			if (numstring.startsWith(Rules.miliardi[i])){
				if (Rules.miliardi[i].length()>nmax){nmax = Rules.miliardi[i].length();indexmax=i;numberbest=Rules.imiliardi[i];ordineAttuale=Rules.imiliardi[0];}
			}
		}//end for
	}
	
	public static double string2num (String numstring){
		
		numstring = numstring.toLowerCase();
		String numeroinanalisi = numstring;
		String numeroinanalisiaux = numstring;
		int loops = 0;
		//contiene la somma di tutti gli ultimi numeri che sono stati sommati al totale
		//se l'ultimo numero non e' stato sommato , allora vale 0
		
		double numprecedente = 0;
		double numtrasformato = -1;
		try{
		while ((numeroinanalisi.length()>0)&&(loops<40)){
			Misura(numeroinanalisi);
			//System.out.println(" numeroinanalisi "+numeroinanalisi + " nmax " +nmax+ " numberbest " +numberbest+ " ordineAttuale " +ordineAttuale+ " ordinePrecedente "+ordinePrecedente+" ordineMassimo "+ordineMassimo);
			//System.out.println(" numerotrasformato "+numtrasformato);
			
			if (nmax <= numeroinanalisi.length()){
				numeroinanalisiaux = numeroinanalisi.substring(nmax);
			}else numeroinanalisi="";
			
			
			if (numtrasformato==-1)
				{numtrasformato = numberbest; 
				numeroinanalisi = numeroinanalisiaux;
				numprecedente = 0;
				ordineMassimo = ordineAttuale;
				}
			
			else if ((ordineAttuale>ordinePrecedente)&&(ordineAttuale>ordineMassimo))
				 {
				//System.out.println("odo qui!!!");
				numtrasformato *= numberbest;numeroinanalisi = numeroinanalisiaux; 
				 	ordineMassimo = ordineAttuale;
				 	numprecedente = 0;
				 	}
			
			//gestione del caso in cui abbiamo gi� detto un numero maggiore dell'attuale, ma l'attuale e' maggiore del precedente
			//in questo caso si sottraggono tutti gli ultimi numeri che erano stati aggiunti ultimamente.
			//questi numeri vengono poi moltiplicati per l'attuale
			else if ((ordineAttuale>ordinePrecedente))
			 {
				 
			 int ordo = (""+(int)numberbest).length();
			 int ordoprec = (""+(int)numprecedente).length();
			 //System.out.println("ordo "+ordo+" ordoprec "+ordoprec+" numberbest "+numberbest);
			 //se in numero e' piu' grande allora continua ad accumulare
 			 	if (ordo>ordoprec)
 			 	{
 			 		numtrasformato = (numtrasformato - numprecedente)+(numprecedente * numberbest);
 			 		//System.out.println("numtrasformato dopo ordo maggiore "+numtrasformato);
 			 		numeroinanalisi = numeroinanalisiaux;
 			 		numprecedente = numprecedente * numberbest;
 			 	}
 			 	//altrimenti accumula al numero successivo a quello precedente di ordine di grandezza piu' alto
 			 	else {
 			 		int trunked = (int)(numprecedente/Math.pow(10,ordo));
 			 		trunked *= Math.pow(10,ordo);
 			 		int rest = (int)(numprecedente%Math.pow(10,ordo));
 			 		//System.out.println("numprecedente "+numprecedente+" trunked "+trunked+" rest "+rest+" numtrasformato - rest "+(numtrasformato - rest));
 			 		numtrasformato = (numtrasformato - rest)+(rest*numberbest);
 			 		numeroinanalisi = numeroinanalisiaux;
 			 		numprecedente = rest*numberbest;
 			 	}
			 }//end if ((ordineAttuale>ordinePrecedente))
			
			else 
				{numtrasformato += numberbest;numeroinanalisi = numeroinanalisiaux;
				numprecedente +=numberbest;
				} //Converti(numeroinanalisi); numeroinanalisi="";}
			
			
			//System.out.println(" numerotrasformato "+numtrasformato+" numero precedente "+numprecedente);
			
			ordinePrecedente = ordineAttuale;
			loops++;
			}
		
		if (loops ==20) return -1;
		else
			return numtrasformato;
		}catch(Exception e){
			System.out.println("numero non scritto correttamente - controllare la scrittura e le elisioni.\nnon sono ammesse espressioni tipo \"centOtto\" ecc.");
			return -1;
		}
	}

}
