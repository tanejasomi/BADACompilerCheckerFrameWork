package org.parserStCodeGenerator;

/*********************************************************************************
 * @author somyataneja Programming Language: Java 8 , IDE Eclipse, JDK 1.8 
 * Class STVal represent declaration of element for SymbolTable. This struct
 * is used between classes scanner, parser and Symbol table to store
 * name, type, location, is constant and scope information of variable.
 ********************************************************************************/

public class STVal {
	String name;
	String type;
	int location;
	boolean isConst;
	int scopeNo;

}
