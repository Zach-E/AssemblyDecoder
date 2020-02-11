package hw3;
import java.util.ArrayList;
import java.util.Scanner;
import api.Instruction;
import api.NVPair;
import api.SymbolTable;


/**
 * class that reads an assembly language and generates code
 * @author Zach Eisele
 *
 */
public class CS227Asm 
{
	//variable that holds the symbol table for the data (stands for Data's symbol table)
	private SymbolTable stData= new SymbolTable();
	
	//variable that holds the symbol table for the labels (stands for Labels' symbol table)
	private SymbolTable stLabels= new SymbolTable();
	
	//variable that holds the ArrayList for the Instruction Stream. (stands for Instruction array list)
	private ArrayList<Instruction> alInstruct= new ArrayList<Instruction>();
	
	//variable that holds the ArrayList for the assembly language (stands for assembly language)
	private ArrayList<String> asLang; 
	
	//variable for the line number of the assembly language (stands for line number)
	private int lineNum= 1;
	
	
	/**
	 * Constructs an assembler for the given assembly language program, 
	 * given as an ArrayList of strings (one per line of the program).
	 * @param program
	 */
	public CS227Asm(ArrayList<String> program) 
	{
		asLang= program;
	}

	/**
	 * For each instruction in the instruction stream that is a jump target,
	 *  adds the label to the instruction's description.
	 */
	public void addLabelAnnotations()
	{
		int instructionNumber=0;
		
		//checks to see if the labels value is currently on the same instruction number and adds that label to th description
		for (int i=0; i<alInstruct.size(); i++)
		{
			/*
			 * goes through the labels symbol table and checks the value of each,
			 * if it matches with the current instruction number then the description is added to that instruction
			 */
			for (int j=0; j<stLabels.size(); j++)
			{
				if (instructionNumber==stLabels.getByIndex(j).getValue())
				{
					alInstruct.get(i).addLabelToDescription(stLabels.getByIndex(j).getName());
				}		
			}
			instructionNumber=instructionNumber+1;
		}	
	}
	
	/**
	 * Assembles the source program represented by this assembler instance 
	 * and returns the generated machine code and data as an array of strings.
	 * @return code with descriptions
	 */
	public ArrayList<String> assemble()
	{
		parseData();
		parseLabels();
		parseInstructions();
		setOperandValues();
		addLabelAnnotations();
		return writeCode();
	}
	
	/**
	 * Returns the symbol table for data (variables).
	 * @return the symbol table for the data.
	 */
	public SymbolTable getData()
	{
		return stData;
	}
	
	/**
	 * Returns the symbol table for the instructions
	 * @return the symbol table for the instructions.
	 */
	public ArrayList<Instruction> getInstructionStream()
	{
		return alInstruct;
	}
	
	/**
	 * Returns the symbol table for the labels.
	 * @return the symbol table for the labels.
	 */
	public SymbolTable getLabels()
	{
		return stLabels;
	}
	
	/**
	 * creates the symbol table for the data (variables).
	 */
	public void parseData()
	{
		String currentLine;
		currentLine= asLang.get(lineNum);
		
		//for all data variables,  adds the NVPair to the Symbol Table by reading the values and variable name
		while (asLang.get(lineNum)!="labels:")
		{
			currentLine= asLang.get(lineNum);
			Scanner sc= new Scanner(currentLine);
			String varName=sc.next();
			int varInt=0;
			if (sc.hasNextInt())
			{	
				varInt=sc.nextInt();
			}
			stData.add(varName, varInt);
			lineNum=lineNum+1;
			sc.close();
		}
	}
	
	/**
	 * creates the array list for the Instruction stream.
	 */
	public void parseInstructions()
	{
		String currentLine;
		lineNum= lineNum+1;
		int instructLine=0;
		
		//for all Instructions, adds to the Instruction stream
		while (lineNum<asLang.size())
		{
			currentLine= asLang.get(lineNum);
			
			//if the "instruction" is actually a jump point, it sets the value of said point of the current position it is in the line
			if (stLabels.containsName(currentLine))
			{
				NVPair indexName= stLabels.findByName(currentLine);	
				int index= stLabels.indexOf(indexName);
				NVPair target=stLabels.getByIndex(index);
				target.setValue(instructLine);
				lineNum=lineNum+1;
			}
			
			//if the Instruction is actually an Instruction, then the instruction is added to the array list
			else 
			{
				Instruction instruct= new Instruction(currentLine);
				alInstruct.add(instruct);
				instructLine=instructLine+1;
				lineNum=lineNum+1;
			}

		}
	}
	
	/**
	 * creates the symbol table for the labels.
	 */
	public void parseLabels()
	{
		String currentLine;
		lineNum= lineNum+1;
		
		//for all the Labels, each label is added to the Symbol table with a value of 0
		while (asLang.get(lineNum)!="instructions:")
		{
			currentLine= asLang.get(lineNum);	
			Scanner sc= new Scanner(currentLine);
			String varName=sc.next();
			stLabels.add(varName);
			lineNum=lineNum+1;
			sc.close();
		}
	}
	
	/**
	 * fixes the operand values for the instructions depending on if it is a data address or jump target.
	 */
	public void setOperandValues()
	{
		//adjusts the Operand values for all Instructions depending if it is a jump point or has a data address
		for (int i=0; i<alInstruct.size(); i++)
		{
			/*if the instruction is a jump, then it passes by the unfixed operand and jump command to find
			 * the jump point and then finds the value associated with that point and adds that value to the operand
			 */
			if (alInstruct.get(i).requiresJumpTarget())
			{
				Instruction currentPos= alInstruct.get(i);
				String read= currentPos.toString();
				Scanner arsc= new Scanner(read);
				arsc.nextInt();
				arsc.next();
				String whatPoint= arsc.next();
				NVPair neededPair= stLabels.findByName(whatPoint);
				int indexOp= stLabels.indexOf(neededPair);
				int addOp=stLabels.getByIndex(indexOp).getValue();
				alInstruct.get(i).setOperand(addOp);
				arsc.close();
			}
			
			/*if the instruction requires a data address, the data variable is read and
			 * adds the value associated with that variable and the size of instruction stream to the operand.
			 */
			else if(alInstruct.get(i).requiresDataAddress())
			{
				String read= alInstruct.get(i).toString();
				Scanner arsc= new Scanner(read);
				arsc.nextInt();
				arsc.next();
				String dataPos=arsc.next();
				NVPair neededPair= stData.findByName(dataPos);
				int addOp= stData.indexOf(neededPair) + alInstruct.size();
				alInstruct.get(i).setOperand(addOp);
				arsc.close();
			}
		}
	}
	
	/**
	 * generates the machine code and data for the assembler's program and terminates it with -99999.
	 * @return the final code
	 */
	public ArrayList<String> writeCode()
	{
		ArrayList<String> finalCode= new ArrayList<String>(); 
		
		//adds the Instruction stream to the final code
		for (int i=0; i<alInstruct.size(); i++)
		{
			finalCode.add(alInstruct.get(i).toString());
		}
		
		//adds the value and name of data variables below the Instructions
		for (int i=0; i<stData.size(); i++)
		{
			int dataNumber=stData.getByIndex(i).getValue();
			finalCode.add(String.format("%+05d", dataNumber) + " " + stData.getByIndex(i).getName());
		}
		finalCode.add("-99999");
		return finalCode;
	}
	
}