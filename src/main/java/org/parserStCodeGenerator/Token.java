package org.parserStCodeGenerator;

/**************************************************************************
 * @author somyataneja Prog Lang: Java 8 , IDE Eclipse, JDK 1.8 
 * Token class define token structure and implements generic. Token stores
 *  four values : lexeme, lexType,token id, and line number. It uses 
 *  class Constant.java mapper functionality to get token id.
 * @ImportantRoutines:
 * 1) setTokenId()
 *    @function: calls constant.mapper to set Id for current token
 * 
 **************************************************************************/
public class Token {
	/*********************************************************************** 
	  @params: 
	  1) lexeme: store actual value of token
	  2) lexType: stores if identifier,string, numeric or keyword value.
	   	 This is required to get id from Constant.mapper.  
	  3) tokenId: to store corresponding value of token id
	  4) line: stores line number for display in case of error.
	 ***********************************************************************/
	private String lexeme;
	private String lexType;
	private String tokenId;
	private String line;
	
	public Token(String type, String val, int line) {

		this.line = Integer.toString(line);
		this.lexType = type;
		this.lexeme = val;
		//this.setTokenId();
		this.tokenId = Constant.mapper.get(lexType.toUpperCase());
	}

	/***********************************************************************
	  Set tokenId to predefined tokenId for given value of token stored in
	  constant.mapper.
	 ************************************************************************/
	private void setTokenId() {
		this.tokenId = Constant.mapper.get(lexType.toUpperCase());
	}
	
	/***********************************************************************
	  Getter method to return token id used by Lexer class
	 ************************************************************************/
	 public String getTokenId() {
		return this.tokenId;

	}
	
	/***********************************************************************
	  Getter method to return lexeme used by Parser class
	 ************************************************************************/
	public String getTokLexeme() {
		return this.lexeme;
	}
	
	/***********************************************************************
	 Generic method to format token record for better output readability
	 ************************************************************************/
	public String getToken() {
		StringBuilder sb = new StringBuilder();
		sb.append(lexeme);
		sb.append(" ,T.id: ");
		sb.append(tokenId);
		sb.append(" , Lnum: ");
		sb.append(line);

		return sb.toString();
	}

}
