package org.parserStCodeGenerator;

public class DummyMain {
	public static void main(String[] arg) {
		final String INPUT_FILE = "/Documents/compilers/parserDump21098.txt";
		final String OUTPUT_FILE_PATH ="/Documents/compilers";
		Parser p = new Parser(INPUT_FILE,OUTPUT_FILE_PATH);
		boolean codeOK = p.program();
		if(codeOK) {  
			System.out.println("Program successfully compiled!");
		}
		else
			System.out.println("Program compiled with errors and warnings ");
	}

}
