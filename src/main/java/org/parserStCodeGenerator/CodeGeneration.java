package org.parserStCodeGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/******************************************************************************
 * @author somyataneja, Prog Language: Java 8, IDE- Eclipse, JDK 1.8 
 * Class CodeGeneration contains file IO functions to write generated assembly
 * language(MIPS) code to Output file mipsOutput.s kept in folder 
 * mentioned by user. Output file: mipsOutput.s can be executed directly
 * on SPIM/QtSpim. 
 *  @ImportantRoutines:
 *  1) writeProlog()
 *  @function: Write MIPS instruction 
 *  2) codeGen(String text)
 *  @function: Write String input(MIPS instructions)
 *  3) writeProlog()
 *  @function: Writes postlog and List to store string label values 
 *  (strLabelLst) for put statement to print at last .data section 
 *  4)openFile()
 *  @funciton: perform IO function. Opens file mipsOutput.s if it exist 
 *  else create new and open it
 ****************************************************************************/
public class CodeGeneration {
	/*********************************************************************** 
	 @params
	 1) outputFile: Stores output file name
	************************************************************************/
	 String outputFile; // Stores output file name
	 BufferedWriter bw;
	 FileWriter fileWriter;
	 File f;


	@SuppressWarnings("initialization.fields.uninitialized")
        //fields are initialized in function openFile() which is called by class Parser
	 CodeGeneration(String outFilePath) {

		outputFile = (outFilePath + "/mipsOutput.s"); // generate file in path provided
	}
	
	/*********************************************************************** 
	 Open file if present else create new with name mipsOutput.s and open 
	************************************************************************/
	public void openFile(){
		try {
			this.f = new File(outputFile);
			if (!f.exists()) {
				f.createNewFile();
				System.out.println("File mipsOutput.s created!");
			}

			fileWriter = new FileWriter(f);
			bw = new BufferedWriter(fileWriter);

		} catch (IOException e) {

			try {
				if (bw != null)
					bw.close();
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}

			e.printStackTrace();

		}

	}
	
	/*********************************************************************** 
	 Method to close file after compilation
	************************************************************************/
	public void closeFile() {

		try {

			if (bw != null)
				bw.close();

			if (fileWriter != null)
				fileWriter.close();
			System.out.println("File succesfully closed");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	/*********************************************************************** 
	 Method to write MIPS instruction to output file.
	************************************************************************/
	public void codeGen(String text) {
		try {
			bw.write(text);
			bw.newLine();
		} catch (IOException e) {
			closeFile();
			e.printStackTrace();
		}
	}
	
	/*********************************************************************** 
	 Method to write MIPS prolog to output file
	************************************************************************/
	public void writeProlog() {

		try {
			bw.write("#Prolog:");
			bw.newLine();
			bw.write(".text");
			bw.newLine();
			bw.write(".globl main");
			bw.newLine();
			bw.write("main:");
			bw.newLine();
			bw.write("move $fp $sp");
			bw.newLine();
			bw.write("la $a0 ProgBegin");
			bw.newLine();
			bw.write("li $v0 4");
			bw.newLine();
			bw.write("syscall");
			bw.newLine();
			bw.write("#End of Prolog");
			bw.newLine();
			bw.write("#Program MIPS code begin here");
			bw.newLine();

		} catch (IOException e) {

			closeFile();
			e.printStackTrace();
		}

	}
	/*********************************************************************** 
	 Method to write MIPS postlog to output file
	************************************************************************/
	public void writePostlog(List<String> strLabelLst) {
		try {
			bw.write("#Program execution code ends:");
			bw.newLine();
			bw.write("#Postlog:");
			bw.newLine();
			bw.write("la $a0 ProgEnd");
			bw.newLine();
			bw.write("li $v0 4");
			bw.newLine();
			bw.write("syscall");
			bw.newLine();
			bw.write("li $v0 10");
			bw.newLine();
			bw.write("syscall");
			bw.newLine();
			bw.write("li $v0 4");
			bw.newLine();
			bw.write(".data");
			bw.newLine();
			bw.write("ProgBegin: .asciiz \"Program Begin\\n\"");
			bw.newLine();
			bw.write("ProgEnd: .asciiz \"\\nProgram End\\n\"");
			bw.newLine();
			bw.write("NewLine: .asciiz \"\\n\"");
			bw.newLine();
			for (String str : strLabelLst) {
				bw.write(str);
				bw.newLine();
			}

		} catch (IOException e) {

			closeFile();
			e.printStackTrace();
		}

	}

}
