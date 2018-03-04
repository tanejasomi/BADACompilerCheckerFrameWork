package org.parserStCodeGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.parserStCodeGenerator.Constant.*;

/*********************************************************************************
 * @author somyataneja, Prog Language: Java 8 , IDE Eclipse, JDK 1.8.
 * Class Parser implements recursive descent parser of BABYADA BNF grammar
 * and generate MIPS instructions corresponding to input program. 
 * All operations are left associative and have usual order of precedence. 
 * Parser error are considered fatal or terminal and all other error 
 * are non fatal and non terminal. Parser adheres to BABYADA language 
 * specifications like: no type coercions. IF and while statement must 
 * be boolean. OR, NOT, AND only operate on Boolean. MOD only operates 
 * on integers.<, >, = work on integers, Booleans, floats.No read on 
 * constants etc and many more. 
 *  @ImportantRoutines:
 *  1) program()
 *     @function: Entry point to program. Return the code status to
 *     compiler class.
 *  2) assignstat:
 *		@function: matches type and location of the id and that of exp
 *		if types compatible then move expRec.loc to id.loc
 *  3) factor(ExpRec expRec)
 *  		@function: for Literals check if type integer,float or boolean. 
 *  		Not operation allowed on booleans.
 *  4) idnonterm()
 *   	@function: Look up IDTOK in the symbol table,  Return a pointer to it.  
 *      Type and location are then available from this pointer.
 *  5) match(String t)
 *  		@function: String matching function to match current token id 
 *  		with expected token id
 ********************************************************************************/
public class Parser {
	/***********************************************************************
	@params
	1) codeOK : Boolean variable used to store status of compilation
	2) currLoc: variable to store top of stack :current location
	3) labelNum: counter used to generate labels for MIPS code	
	4) lineNum: stores line number for error message
	5) token: stores token id from input program
	6) index: used to get next token in link list
	7) tokVal: stores lexeme used to display error msg and symbol table 
	  			routines         
	8) tokenLst: list to store tokenRecords from scanner
	9) strLabelLst: list to store string values for put statement 
			to print at last .data section.
	10) ExpRec: Nested Class contains two elements-type and location
			used for type checking and generating MIPS code
	11) scannerCodeOK: Flag to get status of scanning phase
	 ************************************************************************/
	private static final int NEXT_LOC = -4;// Reserved size of element is 4 bytes
	private static int lineNum; 
	private static int labelNum;
	private int index; 
	private boolean codeOK = true;
	private String token;
	private String tokVal;
	int currLoc = 0;
	
	private SymbolTable st;
	private String inputFile;
	private CodeGeneration cg;
  
	class ExpRec {
		char type;
		int loc;
	};
	List<Token> tokenLst; 
	List<String> strLabelLst; 
	

	Parser(String infileName, String outFilePath) {
		inputFile = infileName;
		index = 0; // initialize index
		currLoc = 0;//initialize currLoc
		tokenLst = new ArrayList<Token>(); // make new ArrayList
		strLabelLst = new ArrayList<String>();
		st = new SymbolTable();
		st.createSymbolTable();// Create default Symbol table for global variable
		this.getTokenLst(); // populate list with token values from file.
		this.setToken();
		cg = new CodeGeneration(outFilePath);
		cg.openFile();

	}
	/***********************************************************************
	program : PROCTOK IDTOK ISTOK decls BEGINTOK stats ENDTOK IDTOK ';'
	 ************************************************************************/
	public boolean program() {

		if (token.equals(PROCTOK)) {
			cg.writeProlog();
			match(PROCTOK);
			match(IDTOK);
			match(ISTOK);
			declarations();
			match(BEGINTOK);
			statements();
			match(ENDTOK);
			st.popCurrSymbolTable();
			match(IDTOK);
			match(ESTOK);
			cg.writePostlog(strLabelLst);
			cg.closeFile();
			st.display();
		}
		return codeOK;
	}
	/***********************************************************************
	 statements : statmt statements | <empty>
	 ************************************************************************/
	public void statements() {

		if (token.equals(IDTOK) || token.equals(IFTOK) || token.equals(WRITETOK) 
				|| token.equals(WHILETOK) || token.equals(READTOK) 
				|| token.equals(DECTOK) || token.equals(BEGINTOK)) {

			statmt();
			statements();
		}
	}
	/***********************************************************************
	 declarations : decl declarations | <empty>
	 ************************************************************************/
	public void declarations() {
		if (token.equals(IDTOK)) {
			decl();
			declarations();
		}
	}
	/***********************************************************************
	 decl : IDTOK ':' rest
	 ************************************************************************/
	public void decl() {
		boolean ifDupFlag = false; // duplicate flag to avoid storing duplicate values in ST
		if (token.equals(IDTOK)) {
			STVal sval = new STVal();
			// check if token already declared in current scope
			if (st.isPresentinCurrentScope(tokVal) != null) {
				System.out.println("Error: token " + tokVal + " already exits in current scope");
				codeOK = false;
				ifDupFlag = true; // Set is Duplicate Flag true if value already exist.
			}
			sval.name = tokVal;
			match(IDTOK);
			match(SCTOK);
			rest(sval, ifDupFlag);
		}
	}
	/***********************************************************************
	  rest : BASTYPTOK ';' | CONSTTOK BASTYPTOK ASTOK LITTOK ';'
	 ************************************************************************/
	public void rest(STVal sval, boolean ifDupFlag) {
		if (token.equals(BASTYPTOK)) {
			sval.isConst = false;
			sval.type = tokVal;
			match(BASTYPTOK);
			match(ESTOK);

		} else if (token.equals(CONSTTOK)) {
			sval.isConst = true;
			match(CONSTTOK);
			// sval.type = token;
			match(BASTYPTOK);
			match(ASTOK);
			sval.type = tokVal;
			match(LITTOK);
			match(ESTOK);
		}

		if (!ifDupFlag) { // if no duplicate value then insert in ST
			setLocation(sval);
			st.insertInST(sval.name, sval);

		}
	}
	/***********************************************************************
	 Calculate current location as -4 from last saved variable location
	 ************************************************************************/
	public void setLocation(STVal sval) {

		sval.location = currLoc;
		currLoc = currLoc + NEXT_LOC;
	}
	/***********************************************************************
	statmt : assignstat | ifstat | readstat | writestat |blockst | loopst
	 ************************************************************************/
	public void statmt() {

		if (token.equals(IDTOK)) {
			assignstat();

		} else if (token.equals(IFTOK)) {
			ifstat();

		} else if (token.equals(READTOK)) {
			readstat();

		} else if (token.equals(WRITETOK)) {
			writestat();

		} else if (token.equals(DECTOK) || token.equals(BEGINTOK)) {
			blockst();

		} else if (token.equals(WHILETOK)) {
			loopst();

		} else {
			System.out.println("Error in statmt token " + token);
			codeOK = false;
			System.exit(0);
		}
	}
	/***********************************************************************
	 assignstat : idnonterm ASTOK express ';'
	 ************************************************************************/
	public void assignstat() {
		ExpRec expRec = new ExpRec();
		expRec = idnonterm();

		match(ASTOK);
		ExpRec expRhs = new ExpRec();
		expRhs = express();
		matchTypes(expRec.type,expRhs.type);
		cg.codeGen(" #code generation in assignment ");
		cg.codeGen("lw $t0 " + expRhs.loc + "($fp)");
		cg.codeGen("sw $t0 " + expRec.loc + "($fp)");
		match(ESTOK);
	}
	/***********************************************************************
	 ifstat : IFTOK express THENTOK stats ENDTOK IFTOK ';'
	 ************************************************************************/
	public void ifstat() {
		match(IFTOK);
		ExpRec exp = new ExpRec();
		exp = express();
		//condition can only be boolean
		matchTypes(exp.type, 'b');
		
		String endTrue = genLabel("EndTrue");
		cg.codeGen("# branch on if false --if block");
		cg.codeGen("lw $t0 " + exp.loc + "($fp)");
		cg.codeGen("beqz $t0 " + endTrue);
		
		match(THENTOK);
		statements();
		cg.codeGen(endTrue + ":");
		match(ENDTOK);
		st.popCurrSymbolTable();
		match(IFTOK);
		match(ESTOK);

	}
	/***********************************************************************
	  readstat : READTOK '(' idnonterm ')' ';'
	 ************************************************************************/
	public void readstat() {

		match(READTOK);
		match(OBTOK);
		ExpRec expRec = new ExpRec();
		expRec = idnonterm();

		if (expRec.type == 'c') {
			System.out.println("Error: cant not read value into constant");
			codeOK = false;
		} else if (expRec.type == 'b') {
			System.out.println("Error: variable is type boolean");
			codeOK = false;
		} else if (expRec.type == 'f') {
	
			cg.codeGen("li $v0, 6  #read float value"); 
			cg.codeGen("syscall");
			cg.codeGen("sw $v0 " + expRec.loc + "($fp)");
		} else { // expRec.type is integer
			cg.codeGen("li $v0, 5  #read integer value"); 
			cg.codeGen("syscall");
			cg.codeGen("sw $v0 " + expRec.loc + "($fp)");
		}
		match(CBTOK);
		match(ESTOK);
	}
	/***********************************************************************
	 writestat : WRITETOK '(' writeexp ')' ';'
	 ************************************************************************/
	public void writestat() {
		boolean doesCR = false;
		if (tokVal.equals("put_line"))
			doesCR = true;
		match(WRITETOK);
		match(OBTOK);
		writeexp(doesCR);
		match(CBTOK);
		match(ESTOK);
	}
	/***********************************************************************
	 loopst : WHILETOK express LOOPTOK statements ENDTOK LOOPTOK ';'
	 ************************************************************************/
	public void loopst() {

		match(WHILETOK);
		String toplabel = genLabel("TopWhile");
		cg.codeGen(toplabel + ":");
		ExpRec exp = new ExpRec();
		exp = express();
		//condition can only be boolean"
		matchTypes(exp.type,'b');
		
		cg.codeGen("lw $t0 " + exp.loc + "($fp)" + "  # while loop");
		String endWhile = genLabel("EndWhile");
		cg.codeGen("beqz $t0 " + endWhile);

		match(LOOPTOK);
		statements();
		cg.codeGen("j " + toplabel);
		cg.codeGen(endWhile + ":");
		match(ENDTOK);
		st.popCurrSymbolTable();
		match(LOOPTOK);
		match(ESTOK);
	}
	/***********************************************************************
	 blockst : declpart BEGINTOK stats ENDTOK ';'
	 ************************************************************************/
	public void blockst() {
		if (token.equals(DECTOK) || token.equals(BEGINTOK)) {

			st.createSymbolTable();
			declpart();
			match(BEGINTOK);
			statements();
			match(ENDTOK);
			match(ESTOK);
			st.popCurrSymbolTable();
		}
	}
	/***********************************************************************
	  declpart : DECTOK decl declarations | <empty>
	 ************************************************************************/
	public void declpart() {

		if (token.equals(DECTOK)) {
			match(DECTOK);
			decl();
			declarations();
		}
	}
	/***********************************************************************
	  writeexp : STRLITTOK | express
	 ************************************************************************/
	public void writeexp(boolean doesCR) {
		if (token.equals(STRLITTOK)) {
			String label = genLabel("strLabel");
			strLabelLst.add(label + ": .asciiz " + "\"" + tokVal + "\"");
			cg.codeGen("la $a0 " + label);
			cg.codeGen("li $v0  4 #Code for printing string"); 
			cg.codeGen("syscall");
			match(STRLITTOK);

		} else if (token.equals(LITTOK) || token.equals(IDTOK) 
				|| token.equals(NOTTOK) || token.equals(OBTOK)) {
			ExpRec exp = new ExpRec();
			exp = express();
			if (exp.type == 'i') {
				cg.codeGen("li $v0  1 #Code for printing integer"); 
				cg.codeGen("lw $t2 " + exp.loc + "($fp)");
				cg.codeGen("move $a0, $t2");
				cg.codeGen("syscall");

			} else if (exp.type == 'f') {
				cg.codeGen("la $a1 " + exp.loc + "($fp)");
				cg.codeGen("li $v0 2 #Code for printing float"); 
				cg.codeGen("l.s $f12 ($a1)");
				cg.codeGen("syscall");

			} else if (exp.type == 'b') {
				System.out.println("Error: trying to print boolean value ");
				codeOK = false;
			}

		}
		if (doesCR) {
			cg.codeGen("la $a0 NewLine");
			cg.codeGen("li $v0 4");
			cg.codeGen("syscall");
		}
	}
	/***********************************************************************
	 express : term expprime
	 ************************************************************************/
	public ExpRec express() {
		ExpRec expRec = new ExpRec();
		expRec = term();
		expprime(expRec);

		return expRec;
	}
	/***********************************************************************
	 expprime : ADDOPTOK term expprime | <empty>
	 ************************************************************************/
	public void expprime(ExpRec expRec) {
		if (token.equals(ADDOPTOK)) {
			String op = "";
			if (expRec.type == 'f') {
				op = getFltOpString(tokVal.charAt(0));
				match(ADDOPTOK);
				ExpRec expRhs = new ExpRec();
				expRhs = term();
				// check type coercion
				matchTypes(expRec.type,expRhs.type);
				cg.codeGen(" # Code for " + op + " operation ");
				cg.codeGen("l.s $f1 " + expRhs.loc + "($fp)");
				cg.codeGen("l.s $f2 " + expRec.loc + "($fp)");
				cg.codeGen(op + " $f3 $f2 $f1");
				cg.codeGen("s.s $f3 " + currLoc + "($fp)");
			} else {
				op = getOpString(tokVal.charAt(0));
				match(ADDOPTOK);
				ExpRec expRhs = new ExpRec();
				expRhs = term();
				// check type coercion
				matchTypes(expRec.type,expRhs.type);
				cg.codeGen(" # Code for " + op + " operation ");
				cg.codeGen("lw $t0 " + expRhs.loc + "($fp)");
				cg.codeGen("lw $t1 " + expRec.loc + "($fp)");
				cg.codeGen(op + " $t2 $t1 $t0");
				cg.codeGen("sw $t2 " + currLoc + "($fp)");
			}
			
			// set er type to correct type if neccessary and er.loc to current it by 4
			expRec.loc = currLoc;
			currLoc = currLoc + NEXT_LOC;
			expprime(expRec);
		}
	}
	/***********************************************************************
	  term : relfactor termprime
	 ************************************************************************/
	public ExpRec term() {
		ExpRec expRec = new ExpRec();
		expRec = relfactor(expRec);
		expRec = termprime(expRec);

		return expRec;
	}
	/***********************************************************************
	  termprime : MULOPTOK relfactor termprime | <empty>
	 ************************************************************************/
	public ExpRec termprime(ExpRec expRec) {
		
		if (token.equals(MULOPTOK)) {
			String op = "";
			if (expRec.type == 'f') {
				op = getFltOpString(tokVal.charAt(0));
			} else {
				op = getOpString(tokVal.charAt(0));
			}
			match(MULOPTOK);
			ExpRec expRhs = new ExpRec();
			expRhs = relfactor(expRhs);
			// check type coercion
			matchTypes(expRec.type,expRhs.type);
			cg.codeGen("# code for " + op + " operation");
		
			if (op.equals("mul") || op.equals("div")) {//integer operation
				cg.codeGen("lw $t0 " + expRhs.loc + "($fp) ");
				cg.codeGen("lw $t1 " + expRec.loc + "($fp)");
				cg.codeGen(op + "$t2 $t1 $t0"); 
				cg.codeGen("sw $t2 " + currLoc + "($fp)");
			} 
			else if (op.equals("mul.s") || op.equals("div.s")) {//float operation
				cg.codeGen("l.s $f1 " + expRhs.loc + "($fp) ");
				cg.codeGen("l.s $f2 " + expRec.loc + "($fp)");
				cg.codeGen(op + " $f3 $f2 $f1"); // incase of mult: yields 64 bits number
				cg.codeGen("s.s $f3 " + currLoc + "($fp)");
				//cg.codeGen("mflo $t2");
			} 
			else if (op.equals("rem")) {
				// mod operation can only be done on integer
				matchTypes(expRec.type,'i');
				cg.codeGen(op + " $t2 $t1 $t0"); // MOD
				cg.codeGen("sw $t2 " + currLoc + "($fp)");
			} else {
				cg.codeGen(op + " $t2 $t1 $t0"); // OR,AND
				cg.codeGen("sw $t2 " + currLoc + "($fp)");
			}
			
			// er.loc to current it by 4
			expRec.loc = currLoc;
			currLoc = currLoc + NEXT_LOC;

			expRec = termprime(expRec);
		}
		return expRec;
	}
	/***********************************************************************
	  Type check. Operation can only be performed on simillar types
	 ************************************************************************/
	public void matchTypes(char l, char r) {
		if (l != r) {
			System.out.println(" Error: Type mismatch. "+ l+" doesnot match " + r +
					" Operation can only be performed for simillar data type.");
			codeOK = false;
		}
		
	}
	/***********************************************************************
	  relfactor : factor factorprime
	 ************************************************************************/
	public ExpRec relfactor(ExpRec expRec) {
		expRec = factor(expRec);
		expRec = factorprime(expRec);

		return expRec;
	}
	/***********************************************************************
	 factorprime : RELOPTOK factor | <empty>
	 ************************************************************************/
	public ExpRec factorprime(ExpRec expRec) {
		
		if (token.equals(RELTOK)) {
			char ch = tokVal.charAt(0);			
			// get value of first operand
			match(RELTOK);
			// get value of second operand
			ExpRec expRhs = new ExpRec();
			expRhs = factor(expRhs);
			
			String label = genLabel("relOpLabel");
			cg.codeGen("# code for relational operation");
			cg.codeGen("lw $t0 " + expRhs.loc + "($fp)");
			cg.codeGen("lw $t1 " + expRec.loc + "($fp)");
			cg.codeGen("li $t3 0");
			cg.codeGen("sw $t3 " + currLoc + "($fp)");
		
			switch (ch) {

			case '<':
				cg.codeGen("#condition check for less then");
				cg.codeGen("slt $t2 $t1 $t0");
				cg.codeGen("bnez $t2 " + label);
				break;
			case '=':
				cg.codeGen("#condition check for equal");
				cg.codeGen("beq $t1 $t0 " + label);
				break;
			case '>':
				cg.codeGen("#condition check for greater then");
				cg.codeGen("slt $t2 $t0 $t1 #check if less then");
				cg.codeGen("bnez $t2 " + label);
			}
			/*********************************************/
			String endRop = genLabel("endRop");
			cg.codeGen("j " + endRop);
			cg.codeGen(label + ":");
			cg.codeGen("li $t3 1");
			cg.codeGen("sw $t3 " + currLoc + "($fp)");
			cg.codeGen(endRop + ":");
			// set er type to correct type if neccessary and er.loc to current it by 4
			expRec.type = 'b';
			expRec.loc = currLoc;
			currLoc = currLoc + NEXT_LOC;
		}
		return expRec;
	}
	/***********************************************************************
	factor : NOTTOK factor | idnonterm | LITTOK | '(' express ')'
	 ************************************************************************/
	public ExpRec factor(ExpRec expRec) {
		if (token.equals(NOTTOK)) {
			match(NOTTOK);
			expRec = factor(expRec);
			cg.codeGen("lw $t0 " + expRec.loc + "($fp) # code for NOT ");
			cg.codeGen("not $t1 $t0");
			cg.codeGen("sw $t1 " + expRec.loc + "($fp)");

		} else if (token.equals(IDTOK)) {

			expRec = idnonterm();

		} else if (token.equals(LITTOK)) {
			expRec.loc = currLoc;

			// Only three valid type of literals: integer, float, boolean
			if (getType(tokVal) == 'i') {
				expRec.type = 'i';
				cg.codeGen("li $t0 " + tokVal + " #store integer value");
				cg.codeGen("sw $t0 " + expRec.loc + "($fp)");
			} else if (getType(tokVal) == 'f') {
				expRec.type = 'f';
				String fval = genLabel("fval");
				strLabelLst.add(fval + ": .float " + tokVal);
				cg.codeGen("l.s $f2 " + fval + " # store float value");
				cg.codeGen("s.s $f2 " + expRec.loc + "($fp)");
			} else {
				expRec.type = 'b';
				cg.codeGen("li $t0 " + tokVal + " #store binary value");
				cg.codeGen("sw $t0 " + expRec.loc + "($fp)");
			}
			currLoc = currLoc + NEXT_LOC;
			match(LITTOK);

		} else {

			match(OBTOK);
			expRec = express();
			match(CBTOK);
		}
		return expRec;
	}
	/***********************************************************************
	idnonterm : IDTOK
	check for token in current scope in Symbol table. If not present find 
	in all scopes to get value attributes of token. 
	*************************************************************************/
	public ExpRec idnonterm() {
		ExpRec expRec = new ExpRec();
		if (token.equals(IDTOK)) {

			STVal stval = st.isPresentinCurrentScope(tokVal);

			if (stval == null) {
				stval = st.findInAll(tokVal);
			}
			if (stval == null) {
				System.out.println("Error: undefined variable: " 
						+ tokVal + " in line no " + lineNum);
				codeOK = false;
			} else {
				if (stval.isConst) // if is_constant is true set type = c
					expRec.type = 'c';
				else {
					expRec.type = stval.type.charAt(0);
				}

				expRec.loc = stval.location;
				match(IDTOK);
			}
		}
		return expRec;
	}
	/***********************************************************************
	 String matching function to match current token id with expected token 
	 ************************************************************************/
	public void match(String t) {
		if (token.equals(t)) {
			token = getNextToken(); // read next token
			if (token.equals(ESTOK))
				System.out.println(" "); //newline for better display of output
		} else {
			System.out.println("Fatal error: Unexpected token: " + tokVal
					+ " token id: " + token + " required id " + t);
			codeOK = false;
			System.exit(0);
		}
	}
	/***********************************************************************
	  Method to generate label used in MIPS code
	 ************************************************************************/
	public String genLabel(String inputStr) {
		labelNum++;
		return (inputStr + labelNum);
	}
	/***********************************************************************
	 Getter method to return lexeme from tokenlst
	 ************************************************************************/
	public String getLexeme() {
		Token tok = tokenLst.get(index);
		String tokLexeme = tok.getTokLexeme();
		return tokLexeme;
	}
	/***********************************************************************
	 Getter method to return token from token list
	 ************************************************************************/
	public String getNextToken() {
		Token tok = tokenLst.get(index);
		String tokId = tok.getTokenId();
		tokVal = tok.getTokLexeme();
		index++;
		return tokId;
	}
	/***********************************************************************
	 Getter method to return String to specify operation to be done
	 ************************************************************************/
	public String getOpString(char ch) {
		String st = "";
		switch (ch) {
		case '>':
			st = "bgt";
			break;
		case '<':
			st = "blt";
			break;
		case '=':
			st = "beq";
			break;
		case '*':
			st = "mul";
			break;
		case '/':
			st = "div";
			break;
		case 'm':
		case 'M':
			st = "rem";
			break;
		case 'a':
		case 'A':
			st = "and";
			break;
		case '+':
			st = "add";
			break;
		case '-':
			st = "sub";
			break;
		case 'o':
		case 'O':
			st = "or";
		}

		return st;
	}
	
	/***********************************************************************
	 Getter method for Float operations to return String to specify operation
	 ************************************************************************/
	public String getFltOpString(char ch) {
		String st = "";
		switch (ch) {
		case '*':
			st = "mul.s";
			break;
		case '/':
			st = "div.s";
			break;
		case 'm':
		case 'M':
			st = "rem";
			break;
		case '+':
			st = "add.s";
			break;
		case '-':
			st = "sub.s";
			
		}
		return st;
		}
	/***********************************************************************
	 Getter method read file and populate token list.
	 ************************************************************************/
	public void getTokenLst() {
		BufferedReader reader = null;
		try {
			File file = new File(inputFile);

			try {
				FileReader fileReader = new FileReader(file);
				reader = new BufferedReader(fileReader);
				String line = null;

				while ((line = reader.readLine()) != null) {
					lineNum++;
					scanInputLine(line, lineNum);
				}
				// send end of file character to scanner
				scanInputLine("$", lineNum);
			} catch (Exception e) {
				System.err.println("Error occurred in the program due to below error.");
				e.printStackTrace();
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/***********************************************************************
	 Getter method to get token type-integer, float or binary (true,false)
	 ************************************************************************/
	// 
	public char getType(String lit) {
		char ch;
		if (lit.charAt(0) == 't' || lit.charAt(0) == 'f')
			ch = 'b';
		else if (lit.contains("."))
			ch = 'f';
		else
			ch = 'i';

		return ch;
	}
	/***********************************************************************
	 Method to populate token list from input file
	 ************************************************************************/
	public void populateTokenLst(List<Token> tokenRec) {

		for (Token token : tokenRec) {
			tokenLst.add(token);
		}
	}
	/***********************************************************************
	 scan input line to list of get token and fetch corresponding records.
	 ************************************************************************/
	public void scanInputLine(String inputLine, int lineNumber) {
		List<Token> tokenRecs = new ArrayList<Token>();
		Lexer l = new Lexer(inputLine, lineNumber);
		try {
			tokenRecs = l.findTokens();
			populateTokenLst(tokenRecs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***********************************************************************
	 method to set token value
	 ************************************************************************/ 
	public void setToken() {
		this.token = getNextToken();
	}
}
