package org.parserStCodeGenerator;

import java.util.Scanner;

/*************************************************************************
 * @author somyataneja  Prog Lang: Java 8, IDE- Eclipse, JDK 1.8
 * Class Compiler has function main() which reads input and star User
 * provides: inputFileName: file containing BABY ADA program. and output
 * file folder path. Inputs are read using Java library scanner class.
 * Parser is invoked with file names and path. Parser return codeOk flag
 * to indicate status of compilation.
 **************************************************************************/
public class Compiler {
	/*********************************************************************** 
	 @params
	 1) inputFileName: Stores output file name
	 2) outputFilePath: Stores folder location of MIPS output
	************************************************************************/
	public static void main(String[] args) {
		String inputFileName; // stores name of input file
		String outputFilePath; // stores folder location of MIPS output

		System.out.println("Enter name of input file");
		Scanner in = new Scanner(System.in);
		inputFileName = in.nextLine();
		System.out.println("Enter name of output file folder");
		outputFilePath = in.nextLine();
		in.close();
		// Generate error for empty file name and path.
		if (inputFileName.isEmpty() || outputFilePath.isEmpty()) {
			System.out.println("FATAL ERROR: FILE NAME NOT ENTERED!");
			System.exit(0);
		}

		Parser p = new Parser(inputFileName, outputFilePath);
		boolean codeOK = p.program(); // CodeOk flag to know status of compilation.
		if (codeOK) {
			System.out.println("Program successfully compiled!");
		} else
			System.out.println("Program compiled with errrors and warnings ");
	}
	
}
