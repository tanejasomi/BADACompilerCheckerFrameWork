package org.parserStCodeGenerator;

import java.util.HashMap;
import java.util.Map;

/****************************************************************
 * @author somyataneja Prog Lang: Java 8 , IDE Eclipse, JDK 1.8
 *  Constant class constants used in other program. It also
 *  implements mapper - HashMap to map lexeme with token id.
 *  Mapper function is static and initialized itself.
 *  Following is list of tokens and token id predefined
 *  for BABA ADA programs. 
 *	***********************************************
 *	  Tokens								TOKEN ID
 *    identifiers							1
 *    type tokens (integer,real,boolean)		2
 *    string literals                     	3
 *    addition operators(+,-,'or') 			4
 *    mult. operators(*,/,'mod','and')		5                    		
 *    3 relational operators(<,>,=)			6
 *    semicolon    					        	7           
 *    colon                                  8 
 *    assignment   					        	9      
 *    left  parentheses  				   	10         
 *    right parenthesis						11
 *    true, false 							12
 *    procedure token  		  	    			13
 *    is token  								14
 *    declare token  				    		15
 *    constant token   				   		16
 *    not token              				17
 *    if token  					            18 
 *    then token 							19
 *    while token 							20
 *    loop token								21
 *    begin token							22
 *    end token								23	
 *    write tokens 							24
 *    read token   							25 
 *    numeric literals		         		26
 *    end-of-file    					 	-1
 *****************************************************************/
public class Constant {

	public static Map<String, String> mapper;
	public static final String PROCTOK = "13";
	public static final String IDTOK = "1";
	public static final String ISTOK = "14";
	public static final String BEGINTOK = "22";
	public static final String ENDTOK = "23";
	public static final String BASTYPTOK = "2"; // type token
	public static final String CONSTTOK = "16";
	public static final String ASTOK = "9";
	public static final String LITTOK = "26";
	public static final String IFTOK = "18";
	public static final String THENTOK = "19";
	public static final String READTOK = "25";
	public static final String WRITETOK = "24";
	public static final String DECTOK = "15";
	public static final String WHILETOK = "20";
	public static final String LOOPTOK = "21";
	public static final String ADDOPTOK = "4";
	public static final String MULOPTOK = "5";
	public static final String NOTTOK = "17";
	public static final String STRLITTOK = "3";
	public static final String RELTOK = "6";
	public static final String ESTOK = "7"; // End of Statement token ";"
	public static final String OBTOK = "10"; // Open Bracket token "("
	public static final String CBTOK = "11";// Close Bracket token ")"
	public static final String SCTOK = "8"; // Semicolon

	static {
		mapper = createMapper();
	}
	
	/***********************************************************************
	   Implement HashMap to store predefined token and token id pair
	 ************************************************************************/
	public static Map<String, String> createMapper() {
		HashMap<String, String> keywords = new HashMap<String, String>();
		keywords.put("IDENTIFIER", "1");
		keywords.put("BOOLEAN", "2");
		keywords.put("INTEGER", "2");
		keywords.put("FLOAT", "2");
		keywords.put("STRING", "3");
		keywords.put("+", "4");
		keywords.put("-", "4");
		keywords.put("OR", "4");
		keywords.put("*", "5");
		keywords.put("/", "5");
		keywords.put("AND", "5");
		keywords.put("MOD", "5");
		keywords.put("<", "6");
		keywords.put(">", "6");
		keywords.put("=", "6");
		keywords.put(";", "7");
		keywords.put(":", "8");
		keywords.put(":=", "9");
		keywords.put("(", "10");
		keywords.put(")", "11");
		keywords.put("TRUE", "12");
		keywords.put("FALSE", "12");
		keywords.put("PROCEDURE", "13");
		keywords.put("IS", "14");
		keywords.put("DECLARE", "15");
		keywords.put("CONSTANT", "16");
		keywords.put("NOT", "17");
		keywords.put("IF", "18");
		keywords.put("THEN", "19");
		keywords.put("WHILE", "20");
		keywords.put("LOOP", "21");
		keywords.put("BEGIN", "22");
		keywords.put("END", "23");
		keywords.put("PUT", "24");
		keywords.put("PUT_LINE", "24");
		keywords.put("GET", "25");
		keywords.put("NUMERIC", "26");
		keywords.put("$", "-1");

		return keywords;
	}
	  

}
