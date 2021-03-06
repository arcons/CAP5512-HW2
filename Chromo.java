/******************************************************************************
*  Based on Chromo.java: 
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*  
*  
*  SequentialChromo.java 	          by Brandon Jones
*  February 18, 2016
*  
*  Added Integer and Float chromosome representations
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Chromo
{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public List<Integer> chromo;
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

		//  Generate and shuffle a sequence of integers (separated by spaces):
		getRandomTimeChromosome();

		this.rawFitness = -1;   //  Fitness not yet evaluated
		this.sclFitness = -1;   //  Fitness not yet scaled
		this.proFitness = -1;   //  Fitness not yet proportionalized

	}


/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/
	
	// Retrieve a Persons X Shifts matrix, indicating what time is assigned
	// to each person for each of there shifts
	
	public int[][] getTimeRepresentation(){
		// TODO: corresponds to getRandomTimeChromosome
		int[][] timeSlots = new int[7][5];
		
		for (int i = 0; i < Parameters.geneSize; ++i){
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
		chromo = new ArrayList<Integer>();
		for (int i = 1; i <= Parameters.geneSize; ++i){
			chromo.add(i);
		}
		Collections.shuffle(chromo, Search.r);
	}
	
	// Generate a string representing a new, random chromosome
	// Values represent people (1-7); D1S1, D1S2 ... D7S5
	
	public void getRandomPersonChromosome(){
		chromo = new ArrayList<Integer>();
		for (int i = 0; i < Parameters.geneSize; ++i){
			chromo.add((i % 7) + 1);
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
		int val1 = chromo.get(a);
		int val2 = chromo.get(b);
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

		case 2:     //  Tournament Selection

			/* Added by Brandon Jones
			 * Randomly select 2 parents; return the parent with the
			 * higher fitness.
			 */
			
			int p1 = Search.r.nextInt(Search.member.length);
			double f1 = Search.member[p1].proFitness;
			int p2 = Search.r.nextInt(Search.member.length);
			while(p1 == p2)
				p2 = Search.r.nextInt(Search.member.length);
			double f2 = Search.member[p2].proFitness;
			if (f1 > f2){
				return p1;
			}else if(f2 > f1){
				return p2;
			}else{
				int randInt = Search.r.nextInt(2);
				if (randInt == 0)
					return p1;
				else
					return p2;
			}
			
		case 4:    // BJ: Rank Selection
			
			// Create a sorted list of fitnesses:
			ArrayList<SortPair> to_sort = new ArrayList<SortPair>();
			for (j=0; j < Parameters.popSize; ++j){
				to_sort.add(new SortPair(Search.member[j].proFitness, j));
			}
			to_sort.sort(new MyComparator());
			
			// Create a Roulette wheel with slices based on rank for each member
			ArrayList<Integer> rankWheel = new ArrayList<Integer>();
			for (j=0; j < Parameters.popSize; ++j){
				// The jth item in to_sort will get j+1 slices
				for (int i = 0; i < j+1; ++i){
					rankWheel.add(to_sort.get(j).position);
				}
			}
			
			// Select a parent!
			int p = Search.r.nextInt(rankWheel.size());
			return rankWheel.get(p);
			
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
			Collections.copy(child1.chromo, parent1.chromo);
			Collections.copy(child2.chromo, parent2.chromo);
			sequenceCrossover(child1.chromo, child2.chromo, xoverPoint1);
			break;

		case 2:     //  Two Point Crossover
			// TODO: To implement, just call crossLists() twice...

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
		Collections.copy(child.chromo, parent.chromo);

		//  Set fitness values back to zero
		child.rawFitness = -1;   //  Fitness not yet evaluated
		child.sclFitness = -1;   //  Fitness not yet scaled
		child.proFitness = -1;   //  Fitness not yet proportionalized
	}

	//  Copy one chromosome to another  ***************************************

	public static void copyB2A (Chromo targetA, Chromo sourceB){

		targetA.chromo = sourceB.chromo;
		Collections.copy(targetA.chromo, sourceB.chromo);

		targetA.rawFitness = sourceB.rawFitness;
		targetA.sclFitness = sourceB.sclFitness;
		targetA.proFitness = sourceB.proFitness;
		return;
	}
	
	// BJ: Perform 1-point cross-over on two lists *****************************
	
	public static <T> void crossLists(List<T> l1, List<T> l2, int crossPoint){
		for (int i = 0; i < l1.size(); ++i){
			if (i >= crossPoint){
				T temp = l1.get(i);
				l1.set(i, l2.get(i));
				l2.set(i, temp);
			}
		}
	}
	
	// BJ: Perform 1-point cross-over on two lists AND preserve sequences ******
	
	public static  void sequenceCrossover(List<Integer> l1, List<Integer> l2, int crossPoint){
		HashSet<Integer> seen1 = new HashSet<Integer>();
		HashSet<Integer> seen2 = new HashSet<Integer>();
		HashMap<Integer, Integer> duplicates1 = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> duplicates2 = new HashMap<Integer, Integer>();
		// Blindly cross lists:
		crossLists(l1, l2, crossPoint);
		// Check for duplicates:
		for (int i = 0; i < l1.size(); ++i){
			if (seen1.contains(l1.get(i)))
				duplicates1.put(l1.get(i), i);
			else
				seen1.add(l1.get(i));
			if (seen2.contains(l2.get(i)))
				duplicates2.put(l2.get(i), i);
			else
				seen2.add(l2.get(i));
		}
		// Replace duplicates (in l1):
		for (int dup: duplicates1.keySet()){
			// Find the first num not in seen1 in l2
			for (int j = 0; j < l2.size(); ++j){
				int in2 = l2.get(j);
				if (!seen1.contains(in2)){
					l1.set(duplicates1.get(dup), in2);
					seen1.add(in2);
					break;
				}
			}
		}
		// Replace duplicates (in l2):
		for (int dup: duplicates2.keySet()){
			// Find the first num not in seen1 in l2
			for (int j = 0; j < l1.size(); ++j){
				int in1 = l1.get(j);
				if (!seen2.contains(in1)){
					l2.set(duplicates2.get(dup), in1);
					seen2.add(in1);
					break;
				}
			}
		}
	}

}   // End of SequentialChromo.java ********************************************


/* For Rank Selection */
class SortPair{
	double fitness;
	int position;
	
	public SortPair(double fitness, int position){
		this.fitness = fitness;
		this.position = position;
	}
	
	public String toString(){
		return fitness + ", " + position;
	}
}

class MyComparator implements Comparator<SortPair> {
    public int compare(SortPair a, SortPair b) { 
          return Double.compare(a.fitness, b.fitness);
    }
}
