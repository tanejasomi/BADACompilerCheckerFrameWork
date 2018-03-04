package org.parserStCodeGenerator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

/**************************************************************************
 * @author somyataneja, Prog Lang: Java 8 , IDE Eclipse, JDK 1.8
 * @Description: Class Symbol Table contains symbol table related function.
 * Symbol Table is implemented using hash table. Separate ST(hash table) 
 * is created for new block/scope. List of active STs is maintained in stack 
 * (activeStack). Token is object of class STVal. For every token it 
 * checks in current Symbol Table if not present it checks immediate above 
 * open scope and so on. When scope ends ST is popped from active stack and 
 * is pushed to inactive List for display purpose.
 *         
 * @ImportantRoutines:
 * 1) findInST(String token, Hashtable<String, STVal> st)
 *    @function: Find token in current block 
 * 2) findInAll(String token)
 * 	  @function: find token in all active block
 * 3) insertInST(String token, STVal sval)
 *    @function: Insert token in symbol table
 * 4) popCurrSymbolTable()
 *    @funciton: Remove ST from active stack when scope ends and 
 *    push it to inactive stack.
 * 5) createSymbolTable()
 *    @funciton: create new symbol table when new scope/block starts
 * 5) printsymboltable(Hashtable<String, STVal> st)
 *    @function: Print inactive stacks to console for user 
 * 
 ***************************************************************************/

public class SymbolTable {
	/*********************************************************************** 
	  @params: 
	   1) activeStack: Stack to maintain active Symbol Tables
	   2) inactiveSt: Maintain order of inactive ST
	   3) currSt: ST to store current scope
	   4) scopeNo: Variable to store scope numbers for each ST
	************************************************************************/
	private Stack<Hashtable<String, STVal>> activeStack; 
	private LinkedList<Hashtable<String, STVal>> inactiveSt; 
	private Hashtable<String, STVal> currSt;
	private static int scopeNo;

	public SymbolTable() {
		activeStack = new Stack<Hashtable<String, STVal>>();
		inactiveSt = new LinkedList<Hashtable<String, STVal>>();
		scopeNo = 0;
	}

	/*********************************************************************** 
	  Method to create new symbol table for new scope.
	************************************************************************/
	private Hashtable<String, STVal> createNewST() {
		Hashtable<String, STVal> st = new Hashtable<>();
		return st;
	}

	/************************************************************************
	  Method to create symbol table: If not ST for global scope and current   
	  ST is empty i.e no variable declared in last scope than use same empty
	  ST else create new ST and push to activeStack.
	*************************************************************************/
	public void createSymbolTable() {

		if (scopeNo != 0 && currSt.isEmpty()){
		} else {
			currSt = createNewST();
			activeStack.push(currSt);
			scopeNo++; // Increment scope no for new opened scope
		}
	}
	
	/*********************************************************************** 
	  Display log of all symbol tables created in program.
	************************************************************************/
	public void display() {
		System.out.println("Values in Inactive Stack: ");
		System.out.println("scopeNo...name...type...location...Is Constant:");
		for (Hashtable<String, STVal> st : inactiveSt) {
			System.out.print("Scope " + " Data -> ");
			printsymboltable(st);
		}
	}

	/*********************************************************************** 
	 Find token in all active blocks ST
	************************************************************************/
	public STVal findInAll(String token) {
		STVal val = new STVal();
		for (Hashtable<String, STVal> ht : activeStack) {
			val = findInST(token, ht);
			if (val != null)
				break;
		}
		return val;
	}
	
	/*********************************************************************** 
	 Return value if token is present in input scope (hashtable)
	************************************************************************/
	public STVal findInST(String token, Hashtable<String, STVal> st) {
		if (st.isEmpty()) {
			return null;
		}
		return st.get(token);

	}
	/*********************************************************************** 
	 Insert value in current symbol table
	************************************************************************/
	public void insertInST( String token, STVal sval) {
		sval.scopeNo=scopeNo;
		if (currSt != null) { // null check to avoid dereference.of.nullable error
            currSt.put(token, sval);
        }
	}
	
	/*********************************************************************** 
	Check if token already present in Current scope.
	************************************************************************/
	public STVal isPresentinCurrentScope(String token) {
		if(currSt != null) //null check to ensure non null currSt
	        return (findInST(token, currSt));
		else
		    return null;
	}
	
	/*********************************************************************** 
	pop ST from active stack and push to InactiveSt list and make last
	open scope current
	************************************************************************/
	public void popCurrSymbolTable() {
		if (!activeStack.isEmpty()) {
			activeStack.peek();
			inactiveSt.add(activeStack.pop());
			if (!activeStack.isEmpty()) {
				currSt = activeStack.peek();
			}
			scopeNo--; // set scope no to previous scope
		}
	}
	/*********************************************************************** 
	 Print values from individual symbol table
	************************************************************************/
	private void printsymboltable(Hashtable<String, STVal> st) {
		Enumeration<String> keys = st.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			STVal s = st.get(key);
			System.out.print("(" + s.scopeNo + " , " + s.name + ", " + s.type + 
				", " + s.location + ", Is Constant: "+ s.isConst + " ) ");
		}
		System.out.println(); // new line for next scope for better display
	}
}