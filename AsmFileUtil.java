package hw3;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import hw3.CS227Asm;
import java.io.PrintWriter;

/**
 * Class that reads an assembly language from a file and is able to create a memory image,
 * assemble a normal code with or without descriptions, and write said code to a new file.
 * @author Zach Eisele
 *
 */
public class AsmFileUtil 
{
	
	/**
	 * Constructor for the class
	 */
	public AsmFileUtil()
	{
	}
	
	/**
	 * Reads the given file and assembles the program, writing the machine code to a file.
	 * @param filename
	 * @param annotated
	 * @throws java.io.FileNotFoundException
	 */
	public static void assembleAndWriteFile(java.lang.String filename, boolean annotated) throws java.io.FileNotFoundException
	{
		/*if annotation is not needed, the operand is taken and printed to a file 
		 * (start, middle, and done all are variables used to describe the state of the same code but in different readiness to be put into the file)
		 */
		if (annotated==false)
		{
			ArrayList<String> program= new ArrayList<String>();
			ArrayList<Integer> done= new ArrayList<Integer>();
			File file=new File(filename);
			Scanner sc= new Scanner(file);
			
			//scans the file and adds the lines to the ArrayList
			while (sc.hasNextLine())
			{
				program.add(sc.nextLine());
			}
			CS227Asm start= new CS227Asm(program);
			sc.close();
			ArrayList<String> middle=start.assemble();
			
			//adds the code without descriptions to an Array List
			for (int i=0; i<middle.size(); i++)
			{
				String currentLine=middle.get(i);
				Scanner cs= new Scanner(currentLine);
				done.add(cs.nextInt());
			}
			int eliminatingStart= filename.lastIndexOf(".");
			String newFile="";
			
			//eliminates anything past the last . and allows for .mach227 to be added
			for (int i=0; i<eliminatingStart; i++)
			{
				newFile=newFile+filename.charAt(i);
			}
			newFile=newFile+".mach227";
			File fileOut=new File(newFile);
			PrintWriter pw= new PrintWriter(fileOut);
			
			//writes out the code to a new file
			for (int i=0; i<done.size(); i++ )
			{
				Integer currentLine= done.get(i);
				pw.write(currentLine);
			}
		}
		
		//prints code with description to a file
		else 
		{
			ArrayList<String> program= new ArrayList<String>();
			File file=new File(filename);
			Scanner sc= new Scanner(file);
			
			//scans the file and adds the lines to the ArrayList
			while (sc.hasNextLine())
			{
				program.add(sc.nextLine());
			}
			CS227Asm done= new CS227Asm(program);
			sc.close();
			int eliminatingStart= filename.lastIndexOf(".");
			String newFile="";
			
			//eliminates anything past the last . and allows for .mach227 to be added
			for (int i=0; i<eliminatingStart; i++)
			{
				newFile=newFile+filename.charAt(i);
			}
			newFile=newFile+".mach227";
			File fileOut=new File(newFile);
			PrintWriter pw= new PrintWriter(fileOut);
			
			//prints the code to a new file
			for (int i=0; i<done.assemble().size(); i++ )
			{
				String currentLine= done.assemble().get(i);
				pw.write(currentLine);
			}
			
		}
		
		
	}
	
	/**
	 * Reads the given file and assembles the program, returning the machine code as a list of strings (including descriptions).
	 * @param filename
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	public static ArrayList<String> assembleFromFile(String filename) throws java.io.FileNotFoundException
	{
		ArrayList<String> program= new ArrayList<String>();
		File file=new File(filename);
		Scanner sc= new Scanner(file);
		
		//puts together the assembly language from the file by scanning each line and copying it to an array list
		while (sc.hasNextLine())
		{
			program.add(sc.nextLine());
		}
		CS227Asm done= new CS227Asm(program);
		sc.close();
		return done.assemble();
		
	}
	
	/**
	 * creates a memory image from the given file
	 * @param filename
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	public static int[] createMemoryImageFromFile(String filename) throws java.io.FileNotFoundException
	{
		ArrayList<String> program= new ArrayList<String>();
		ArrayList<Integer> done= new ArrayList<Integer>();
		File file=new File(filename);
		Scanner sc= new Scanner(file);
		
		//puts together the assembly language from the file by scanning each line and copying it to an array list
		while (sc.hasNextLine())
		{
			program.add(sc.nextLine());
		}
		CS227Asm start= new CS227Asm(program);
		sc.close();
		ArrayList<String> middle=start.assemble();
		
		//puts the number codes of each line into an array list, but not the sentinel value
		for (int i=0; i<middle.size()-1; i++)
		{
			String currentLine=middle.get(i);
			Scanner cs= new Scanner(currentLine);
			done.add(cs.nextInt());
		}
		int[] memoryDone= new int[done.size()];
		
		//switches the array list to an array
		for (int i=0; i<done.size(); i++)
		{
			if (done.get(i)!=null)
			{
				memoryDone[i]=done.get(i);
			}
		}
		return memoryDone;
	}
	
}