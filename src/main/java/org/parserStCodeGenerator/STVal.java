package org.parserStCodeGenerator;
import org.checkerframework.checker.initialization.qual.*;
import org.checkerframework.checker.nullness.qual.*;

/*********************************************************************************
 * @author somyataneja Programming Language: Java 8 , IDE Eclipse, JDK 1.8 
 * Class STVal represent declaration of element for SymbolTable. This struct
 * is used between classes scanner, parser and Symbol table to store
 * name, type, location, is constant and scope information of variable.
 ********************************************************************************/

public class STVal {
	@Nullable String name;
	@Nullable String type;
	int location;
	boolean isConst;
	int scopeNo;

}
