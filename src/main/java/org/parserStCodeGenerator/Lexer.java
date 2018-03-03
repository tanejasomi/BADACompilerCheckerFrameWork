
package org.parserStCodeGenerator;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************
 * @author somyataneja Prog Lang: Java 8 , IDE Eclipse, JDK 1.8
 * Lexer class creates token list from input program based on DFA. 
 * Token is object of class Token which stores lexeme,id,type and 
 * line number values. Input is read one character at time and 
 * follows peek ahead implementation to identify token type. Keywords
 * are mapped using class Constant that has predefined token id and 
 * lexeme mapper. It also performs validations to check BABYADA 
 * language constraints.Scanner errors are non-fatal error.
 * 
 *  @ImportantRoutines:
 *  1) findTokens()
 *    @funciton : main defines which reads input character and use
 *     switch case of token identification, performs validation and 
 *     insert token in tokenRecs list.
 *  2) Get Routines
 *   @function: get respective values from input buffer. 
 *  	  1. getIdentifierVal(char[] input, int i)
 *    2. getLineNum()
 *    3. getNumericVal(char[] input, int i)
 *    4. getStringVal(char[] input, int i)
 *  3) Insert Routines
 *  @function:
 *    1. insertLitToken(String key)
 *    2. insertNumericToken(String key)
 *    3. insertStrToken(String key)
 *    4.	 insertTokenRecord(String word)
 *  4) Validation Routines: 
 *   @function: Perform Validation for specific token type.
 *    1. isAssignmentOp(char[] input, int i)
 *    2. isComment(char[] input, int i)
 *    3. isKeyword(String key)
 *    4. isValidIdentifier(String num)
 *    5. isValidNumericVal(String num)
 *    6. sValidStr(String str)
 ***********************************************************************/

public class Lexer {
	/*******************************************************************
	 * @params: 
	 * 1) lineNum: Store line number of code, used for error display 
	 * 2) buffer: Store input string in char array format 
	 * 3) TokenRecs: List of token records generated from input
	 *******************************************************************/

	private int lineNum;
	private char[] buffer;
	List<Token> tokenRecs;
	private boolean codeOK = true;

	// constructor call to set defaults
	Lexer(String input, int num) {
		this.setLineNum(num);
		this.setInput(input);
		tokenRecs = new ArrayList<Token>();
	}

	/***********************************************************************
	 * read from buffer & generate valid token with get and validation function
	 ************************************************************************/
	public List<Token> findTokens() {
		int i = 0;

		while (i < buffer.length) { // till at end of input line

			switch (buffer[i]) {
			case '(': // left parenthesis
			case ')': // right parenthesis
			case '+': // addition
			case '*': // multiplication
			case '/': // division
			case ';': // end of statement
			case '>': // greater then operator
			case '<': // less then operator
			case '=': // equal operator
			case '$': // end of line
				insertTokenRecord(Character.toString(buffer[i]));
				i++;
				break;

			case ':':
				// peek ahead to check if its assignment operator
				if (isAssignmentOp(buffer, i)) {
					insertTokenRecord(":=");
					i++;
				} else // if not assignment op it is colon
					insertTokenRecord(":");
				i++;
				break;

			case '-':
				// peek ahead to check if it comment
				if (isComment(buffer, i)) {
					while (i < buffer.length) // if comment eat entire line
						i++;
				} else
					insertTokenRecord("-"); // else store as Sub sign
				i++;
				break;

			case '"':
				i++;
				// get value of string to till end '""
				String str = getStringVal(buffer, i);
				// check valid string constraints before inserting token
				if (isValidStr(str)) {
					insertStrToken(str);
				}
				i = i + str.length() + 1;
				break;

			default:
				// Eat all white spaces
				if (Character.isWhitespace(buffer[i])) {
					i++;
				}
				// if input value is numeric
				else if (Character.isDigit(buffer[i])) {
					String num = getNumericVal(buffer, i);
					// if float value check for float constraints
					if (isValidNumericVal(num)) {
						insertNumericToken(num);
					}
					i += num.length();
				} else if (buffer[i] == '@' || buffer[i] == '~' || 
						buffer[i] == '!' || buffer[i] == '&') {
					
					System.out.println("Scanner Error occurred in the " + 
						"program due to non supporting " + buffer[i]);
					codeOK = false;
					i++;
					break;
				} else {
					String word = getIdentifierVal(buffer, i);
					// check if input token is keyword or identifier
					if (isKeyword(word))
						insertTokenRecord(word);
					else {
						// if identifier check for identifier constraints
						if (isValidIdentifier(word))
							insertLitToken(word);
					}
					i += word.length();
				}

			}

		}
		return tokenRecs;
	}

	/***********************************************************************
	 * identifier/keyword :are sequences of letters, digits, and underscores.
	 ************************************************************************/
	public String getIdentifierVal(char[] input, int i) {
		int j = i;

		for (; j < input.length;) {
			// valid input character is letter, number of underscore
			if (Character.isLetter(input[j]) || 
					(input[j] == '_') || Character.isDigit(input[j])) {
				j++;
			} else
				break;
		}

		String str = String.valueOf(input);
		return (str.substring(i, j));
	}

	/***********************************************************************
	 * Two numeric literals - float or integer
	 ************************************************************************/
	public String getNumericVal(char[] input, int i) {
		int j = i;
		char ch;
		while (j < input.length) {
			ch = input[j];
			if (Character.isDigit(ch) || ch == '.') {
				j++;
			} else
				break;
		}
		String str = String.valueOf(input);
		return (str.substring(i, j));

	}

	/***********************************************************************
	 * String values: any value in double quotes "...."
	 ***********************************************************************/
	public String getStringVal(char[] input, int i) {
		StringBuilder str = new StringBuilder();
		try {
			while (input[i] != '"') {
				str.append(input[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println("Error occured in program due to following err");
			e.printStackTrace();
		}
		return str.toString();
	}

	/***********************************************************************
	 * peek ahead to check if assignment operator ':=' 
	 ***********************************************************************/
	public boolean isAssignmentOp(char[] input, int i) {
		if (input.length > i + 1 && input[i + 1] == '=')
			return true;

		else
			return false;

	}

	/***********************************************************************
	 * peek ahead to check if comment. '--'is comment & '-' is substraction
	 ***********************************************************************/
	public boolean isComment(char[] input, int i) {
		if (input.length > i + 1 && input[i + 1] == '-')
			return true;
		else
			return false;

	}

	/***********************************************************************
	 * check from constant mapper if value is keyword or identifier. 
	 * BABYADA is not case sensitive send UpperCase key to mapper.
	 ***********************************************************************/
	public boolean isKeyword(String key) {
		return Constant.mapper.containsKey(key.toUpperCase());
	}

	/***********************************************************************
	 * create and store identifier token with type IDENTIFIER
	 ***********************************************************************/
	public void insertLitToken(String key) {
		Token t = new Token("IDENTIFIER", key, lineNum);
		tokenRecs.add(t);
	}

	/***********************************************************************
	 * create and store numeric token with type NUMERIC
	 ***********************************************************************/
	public void insertNumericToken(String key) {
		Token t = new Token("NUMERIC", key, lineNum);
		tokenRecs.add(t);
	}

	/***********************************************************************
	 * create and store string token with type STRING
	 ***********************************************************************/
	public void insertStrToken(String key) {
		Token t = new Token("STRING", key, lineNum);
		tokenRecs.add(t);
	}

	/***********************************************************************
	 * create and insert generic token
	 **********************************************************************/
	public void insertTokenRecord(String key) {
		Token t = new Token(key, key, lineNum);
		tokenRecs.add(t);
	}

	/***********************************************************************
	 * identifier check first character is letter ,can not start with '_' 
	 * or numeric value or end with '_' and max length of identifier is 225
	 **********************************************************************/
	public boolean isValidIdentifier(String word) {
		int len = word.length();

		if ((!word.isEmpty()) && ((word.charAt(0) == '_') || 
							(word.charAt(len - 1) == '_'))) {
			System.out.println("Scanner Error in string" + word + 
					" at line number "+ lineNum);
			codeOK = false;
			return false;
			
		} else if (word.length() > 255) {
			System.out.println("Scanner Error: maximum length of a "
					+ "element is 255 characters." + "line number " + lineNum);
			codeOK = false;
			return false;
		}
		return true;
	}

	/***********************************************************************
	 * Only valid numbers unsigned integers and float. Float must have digit
	 * before and after the decimal point
	 ************************************************************************/
	public boolean isValidNumericVal(String num) {
		int len = num.length();
		// if float value:check number can not start or end with '.'
		if (len != 0 && num.contains(".") && 
				((num.charAt(0) == '.') || (num.charAt(len - 1) == '.'))) {
			System.out.println("Scanner Error: invalid float value " + num + 
					" at line no " + lineNum);
			 codeOK = false;
			return false;
		} else

			return true;
	}

	/***********************************************************************
	 * String can not contain end of line character
	 ************************************************************************/
	public boolean isValidStr(String str) {
		if (str.contains(";")) {
			System.out.println("Scanner Error: invalid character in input string " 
		      + str + " line no " + lineNum);
			codeOK = false;
			return false;
		}

		return true;
	}
	/***********************************************************************
	 * Initialize line number
	 ************************************************************************/
	public void setLineNum(@UnderInitialization Lexer this, int lineNum) {
		this.lineNum = lineNum;
	}
	
	/***********************************************************************
	 * Convert string to character array and initialize buffer
	 ************************************************************************/
	@EnsuresNonNull({"buffer"})
	public void setInput(@UnderInitialization Lexer this, String input) {
		this.buffer = input.toCharArray();
	}

}
