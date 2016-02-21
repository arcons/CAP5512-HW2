import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class ComputerLab extends FitnessFunction{


/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	int days = 7;
	int people = 7;
	int shifts = 5;
	int [][] availability;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public ComputerLab(String availability_file){
		// TODO: availability, indicates each person's availability
		name = "Computer Lab Scheduling Problem";
		
		this.availability = new int[people][days*shifts];
		try {
			readPreferencesFile(availability_file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS ***********************************************

	public void doRawFitness(Chromo X){
	
		// TODO: Change me!
		doSimpleRawFitness(X);
		
		System.out.println(X.rawFitness + " " + X.chromo);
	}

//  COMPUTE SIMPLE RAW FITNESS **********************************************************
	
	public void doSimpleRawFitness(Chromo X){
		// TODO: Finish me!
		// -1 if unavailable, 1 if available, 2 if in top 3
		X.rawFitness = 0;
		
		// Check each assignment:
		for (int a = 0; a < Parameters.geneSize; a++){
			int assignment = X.chromo.get(a) - 1;
			int person = a / 5;
			
			// Is person available during assignment:
			if (this.availability[person][assignment] != 0){
				X.rawFitness += 1;
			} else {
				System.out.println((assignment+1) + " is not available to " + (person+1));
			}
		}
	}
	
//  COMPUTE A CHROMOSOME'S RAW FITNESS USING A STEP FUNCTION ****************************
	
	public void doRawFitnessStep(Chromo X){
		
		// -1 if unavailable, 1 if available, 2 if in top 3
		X.rawFitness = 0;
		
		System.out.println("Person 2" + " was assigned: ");
		String assin = "";
		for (int i = 5; i < 10; ++i){
			assin += X.chromo.get(i) + " ";
		}
		System.out.println(assin);
		
		// Check each assignment:
		for (int a = 0; a < Parameters.geneSize; a++){
			int assignment = X.chromo.get(a) - 1;
			int person = a / 7;
			
			
			// Is person available during assignment:
			if (this.availability[person][assignment] == 0){
				X.rawFitness -= 1;  // Unavailable
				System.out.println((person+1) + " " + (assignment + 1));
			}
			else if (this.availability[person][assignment] == 4)
				X.rawFitness += 1;  // Available, but not top 3
			else
				X.rawFitness += 2;  // Top 3 choices
		}
	}

//  READ THE SCHEDULE PREFERENCES FILE AND SET AVAILABILITY******************************
	
	public void readPreferencesFile(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(filename));
		
		// Read each person's schedule:
		for (int p = 0; p < 7; p++){
			String name = sc.nextLine();  // Not needed...
			
			// Read each Shift line:
			for (int l = 0; l < 5; l++){
				String shiftString = sc.nextLine();
				String[] shiftStrings = shiftString.split("\t");
				
				// Parse each shift:
				for (int s = 0; s < shiftStrings.length; ++s){
					int slot = Integer.parseInt(shiftStrings[s]);
					this.availability[p][s + 7*l] = slot;
				}
			}
		}
		System.out.println("\nAvailability Matrix:");
		for (int[] r: this.availability){
			String row = "";
			for (int a: r)
				row += a + " ";
			System.out.println(row);
		}
		
		System.out.println("...");
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE ************************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
		// TODO: Change me!
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.chromo.get(i), 3,output);
		}
		output.write("   RawFitness");
		output.write("\n        ");
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");
		return;
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/
}
