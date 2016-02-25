/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

//This code is based off of homework 1
//and SequentialChromo by Brandon Jones

import java.io.*;
import java.util.*;
import java.text.*;

public class Chromo
{
/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public List<String> chromo; //keep string as binary representation like the example code
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	private static double randnum;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Chromo(){

		//  Set gene values to a randum sequence of 1's and 0's
		getRandomTimeChromosome();

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized
	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

	//  Get Alpha Represenation of a Gene **************************************

	public String[][] getTimeRepresentations()
	{

		// TODO: corresponds to getRandomTimeChromosome
		String[][] timeSlots = new String[7][5];
		
		for (int i = 0; i < Parameters.geneSize; ++i)
		{
			int shift = i % 5;
			int person = i / 5;
			timeSlots[person][shift] = this.chromo.get(i);
		}
		
		return timeSlots;
	}

	public int [][] getPersonRepresentation(){
		// TODO: corresponds to getRandomPersonChromosome
		return null;
	}
	
	// Generate a string representing a new, random chromosome
	// Values represent which time slots (1-35); P1S1, P1S2... P7S5
	
	public void getRandomTimeChromosome(){
		chromo = new ArrayList<String>();
		for (int i = 1; i <= Parameters.geneSize; ++i)
		{
			chromo.add(Integer.toBinaryString(i));
		}
		Collections.shuffle(chromo, Search.r);
	}
	
	// Generate a string representing a new, random chromosome
	// Values represent people (1-7); D1S1, D1S2 ... D7S5
	
	public void getRandomPersonChromosome(){
		chromo = new ArrayList<String>();
		for (int i = 0; i < Parameters.geneSize; ++i)
		{
			chromo.add(Integer.toBinaryString((i % 7) + 1));
		}
		Collections.shuffle(chromo, Search.r);
	}

	//  Mutate a Chromosome Based on Mutation Type *****************************

	public void doMutation(){


		switch (Parameters.mutationType){

		case 1:     //  Randomly swap two genes (for chromosomes representing sequences)

			for (int j=0; j<(Parameters.geneSize * Parameters.numGenes); j++){
				randnum = Search.r.nextDouble();
				if (randnum < Parameters.mutationRate){
					randomSwap();
				}
			}
			break;

		default:
			System.out.println("ERROR - No mutation method selected");
		}


	}
		public void randomSwap(){
		// Randomly swap two genes:
		int a = Search.r.nextInt(Parameters.numGenes);
		int b = Search.r.nextInt(Parameters.numGenes);
		String val1 = chromo.get(a);
		String val2 = chromo.get(b);
		chromo.set(a, val2);
		chromo.set(b, val1);
		
	}


/*******************************************************************************
*                             STATIC METHODS                                   *	
*******************************************************************************/

	//  Select a parent for crossover ******************************************

	public static int selectParent(){

		double rWheel = 0;
		int j = 0;
		int k = 0;

		switch (Parameters.selectType){

		case 1:     // Proportional Selection
			randnum = Search.r.nextDouble();
			for (j=0; j<Parameters.popSize; j++){
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel) return(j);
			}
			break;

		case 3:     // Random Selection
			randnum = Search.r.nextDouble();
			j = (int) (randnum * Parameters.popSize);
			return(j);

		case 2:     //Tournament Selection
		
			//start based on the random selection before the tournament begins
			//randomize the tournament 
			randnum = Search.r.nextDouble();
			//intialize the best of the best individual
			//grab one for the comparison
			int individualRand1 = (int)(randnum *Parameters.popSize);
			int bestInd = individualRand1;
			//each individual competes against another individual
			int tournamentSize = 2;
			//loop through all the data for the tournament
			for(int i = 0; i < tournamentSize-1; i++)
			{
				//get another random number
				randnum = Search.r.nextDouble();
				int individualRand2 = (int)(randnum *Parameters.popSize);
				//determine the winner
				if(Search.member[individualRand1].rawFitness < Search.member[individualRand2].rawFitness)
				{
					bestInd = individualRand2;
				}
			}
			return  bestInd;

		case 4:     //Rank Selection
					//This is based off of the roulette/proportional selection method above
					// Simply set the scaling type to 2
			for (j=0; j<Parameters.popSize; j++)
			{
				rWheel = rWheel + Search.member[j].proFitness;
				if (randnum < rWheel) return(j);
			}
			break;

		default:
			System.out.println("ERROR - No selection method selected");
		}
	return(-1);
	}

	//  Produce a new child from two parents  **********************************

	public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2){

		int xoverPoint1;
		int xoverPoint2;

		switch (Parameters.xoverType){

		case 1:     //  Single Point Crossover

			//  Select crossover point
			xoverPoint1 = 1 + (int)(Search.r.nextDouble() * (Parameters.numGenes * Parameters.geneSize-1));

			//  Create child chromosome from parental material
			Collections.copy(child1.chromo, parent1.chromo);
			Collections.copy(child2.chromo, parent2.chromo);
			crossLists(child1.chromo, child2.chromo, xoverPoint1);
			break;

		case 2:     //  Two Point Crossover

		case 3:     //  Uniform Crossover

		default:
			System.out.println("ERROR - Bad crossover method selected");
		}

		//  Set fitness values back to zero
		child1.rawFitness = -1;   //  Fitness not yet evaluated
		child1.sclFitness = -1;   //  Fitness not yet scaled
		child1.proFitness = -1;   //  Fitness not yet proportionalized
		child2.rawFitness = -1;   //  Fitness not yet evaluated
		child2.sclFitness = -1;   //  Fitness not yet scaled
		child2.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Produce a new child from a single parent  ******************************

	public static void mateParents(int pnum, Chromo parent, Chromo child){

		//  Create child chromosome from parental material
		child.chromo = parent.chromo;

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (Chromo targetA, Chromo sourceB)
	{

		targetA.chromo = sourceB.chromo;

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}

	public static <T> void crossLists(List<T> l1, List<T> l2, int crossPoint)
	{
		for (int i = 0; i < l1.size(); ++i){
			if (i >= crossPoint){
				T temp = l1.get(i);
				l1.set(i, l2.get(i));
				l2.set(i, temp);
			}
		}
	}

	public static  void sequenceCrossover(List<String> l1, List<String> l2, int crossPoint){
		HashSet<String> seen1 = new HashSet<String>();
		HashSet<String> seen2 = new HashSet<String>();
		HashMap<String, Integer> duplicates1 = new HashMap<String, Integer>();
		HashMap<String, Integer> duplicates2 = new HashMap<String, Integer>();
		// Blindly cross lists:
		crossLists(l1, l2, crossPoint);
		// Check for duplicates:
		for (int i = 0; i < l1.size(); ++i){
			if (seen1.contains(l1.get(i)))
				duplicates1.put(l1.get(i), i);
			else
				seen1.add(l1.get(i));
			if (seen2.contains(l2.get(i)))
				duplicates2.put(l1.get(i), i);
			else
				seen2.add(l2.get(i));
		}
		// Replace duplicates (in l1):
		for (String dup: duplicates1.keySet()){
			// Find the first num not in seen1 in l2
			for (int j = 0; j < l2.size(); ++j){
				String in2 = l2.get(j);
				if (!seen1.equals(in2)){
					l1.set(duplicates1.get(dup), in2);
					seen1.add(in2);
					break;
				}
			}
		}
		// Replace duplicates (in l2):
		for (String dup: duplicates2.keySet()){
			// Find the first num not in seen1 in l2
			for (int j = 0; j < l1.size(); ++j){
				String in1 = l1.get(j);
				if (!seen2.equals(in1)){
					l2.set(duplicates2.get(dup), in1);
					seen2.add(in1);
					break;
				}
			}
		}
	}

}   
// End of Chromo.java ******************************************************