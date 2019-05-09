import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lambda extends Analyzer {
	
	private List<String> listOfExpr, parameters, finalResult;
	private String  expr, changeChar, changeTo;
	private boolean status;
	
	// Lambda Constructor; Initialize data members by default.
	public Lambda() {
		status = false;
		expr   = null;
	}
	
	// Alpha Conversion Constructor.
	public Lambda(String expr, String changeChar, String changeTo) {
		if(parseExpression(expr)) {
			setExpression(expr);
			setListOfExpr(expr);
		} else
			this.expr = null;
		setAction(changeChar, changeTo);
	}
	
	// Beta Reduction Constructor.
	public Lambda(String expr, List<String> parameters) {
		if(parseExpression(expr)) {
			setExpression(expr);
			setListOfExpr(expr);
		} else
			this.expr = null;
		setParameters(parameters);
	}
	
	// Set Conversion/Reduction.
	public void setAction(String changeChar, String changeTo) {
		this.changeChar = changeChar;
		this.changeTo = changeTo;
	}
	
	// Set list of Beta parameters.
	public void setParameters(List<String> parameters) {
		this.parameters = new ArrayList<String>(parameters);
	}
	
	// Return the list of parameters for Beta Reduction.
	public List<String> getArguements() {
		return parameters;
	}
	
	// Set expression.
	public void setExpression(String expr) { 
			this.expr = expr; 
	}
	
	// Returns the given lambda expression.
	public String getExpression() { 
		return expr; 
	}
	
	// Set list containing sub expressions in a expression.
	public void setListOfExpr(String expr) {
		this.listOfExpr = new ArrayList<String>(splitExpr(expr));
	}
	
	// Returns the list that has the contents needed to perform Conversion/Reduction.
	public List<String> getList() { 
		return listOfExpr; 
	} 
	
	// Generate the first character for a lambda expression.
	private String getFirstChar(Character[] variables, Random rand, int numOfChar) {
		while(!status) {
			expr = variables[rand.nextInt(variables.length)].toString();
			if(!expr.equals(" "))  // Restricts the generator to not generate a whitespace as the first character.
				if(expr.equals("L") && numOfChar >= 4)
					status = true;
				else if(!expr.equals("L"))
					status = true;
		} return expr;
	}
	
	// Generate the number of dummy characters. ISSUE: Using a boolean as the flag for the loop will cause the function to generate 1 all the time. Idk why.
	private int getDummyLimit(Random rand, int remChar, int maximum) {
		int limit = 0, flag = 0;
		while(flag == 0) { // Generate the number of variables/characters between the Lambda(L) character and the dot(.) character.
			limit = rand.nextInt((maximum + 1) - 20);
			if(limit == 0) { limit += 1; }
			if(limit < remChar) { flag = 1; } // loop breaks.
		} return limit;
	}
	
	// Generates a valid lambda expression. 
	public String generateExpression() {
		Random rand = new Random();
		Character[] variables =  {'a', 'b', 'c', 'd', 'L'}; // Generate characters from a-d to increase chance of a abstract expression.
		final int maximum = 30; 
		String newChar = null, genDum = null, prevChar = null;
		int numOfChar  = rand.nextInt(maximum + 1), charNum = 0, remChar = 0, putDot = 0, untilDot = 0;
		if(numOfChar == 0)  
			numOfChar = 1;
		remChar = numOfChar - 1;
		expr = getFirstChar(variables, rand, numOfChar); charNum = 1;// First character generated ; Increment # of char.
		if(expr.equals("L"))
			putDot = getDummyLimit(rand, remChar, maximum); 
		while(charNum < numOfChar) {
			prevChar = Character.toString(expr.charAt(expr.length() - 1));
			newChar   = variables[rand.nextInt(variables.length)].toString();
			if(varRule(prevChar, newChar) == true) {   // Add variables/character, including whitespace
				expr += newChar;
				if(newChar.equals(" ") == false) { 
					charNum++; remChar--; } 
			} else if(absRule(prevChar, newChar, remChar) == true) {   // Add the lambda symbol
				expr += newChar;
				charNum++; remChar--;
				putDot = getDummyLimit(rand, remChar, maximum); 
			} else if(dummyRule(prevChar, newChar) == true) {    // Add a character after a lambda symbol
				genDum = newChar;
				if(newChar.equals(" ") == false) 
				{ ++charNum; remChar--; untilDot = 1; } 
				else
					untilDot = 0;
				while(untilDot < putDot - 1) {   // Add the remaining dummy variable and then explicitly place a dot.
					prevChar = Character.toString(genDum.charAt(genDum.length() - 1));
					newChar = variables[rand.nextInt(variables.length)].toString();
					if(varRule(prevChar, newChar) == true) {
						genDum += newChar;
						if(newChar.equals(" ") == false) {
							charNum++; untilDot++; remChar--;
						}
					}
				}
				genDum += "."; --remChar; charNum++;  expr += genDum;
			}
		}
		return expr;
	}
	
	// Perform Alpha Conversion.
	public String getConversion() {
		String lex  = null;
		finalResult = new ArrayList<String>();
		if(listOfExpr == null)	// If the list is empty, it is assumed that the expression was invalid.
			return "Invalid Expression";
		for(int counter = 0; counter < listOfExpr.size(); counter++) {
			boolean activeLSymbol = false, dotFound = false, dummyBound = false, boundInPar = false, openFlag = false, inPar = false, noLSymbol = true;         
			int openPar = 0, closePar = 0;
			expr = listOfExpr.get(counter);
			for(int index = 0; index < expr.length(); index++) {
				lex = Character.toString(expr.charAt(index));
				if(lex.matches("L") == true && activeLSymbol == false) {          
					if(noLSymbol == true)
						noLSymbol = false;
					else if(openFlag == true)
						inPar = true;
					activeLSymbol = true;
					dotFound      = false;
					finalResult.add(lex);
				} else if(lex.matches("[a-z]") == true) {
					if(dotFound == false) {
						if(lex.matches(changeChar) == true && activeLSymbol == true) {
							if(dummyBound == false) {
								if(inPar == true)       		// Bound variable is part of a expression, enclosed within a pair of parenthesis.
									boundInPar = true;
								dummyBound = true;
								finalResult.add(changeTo);
							} else {
								return "No conversion.";
							}
						} else if(lex.matches(changeTo) == true && activeLSymbol == true) 
							return "No conversion.";
						else
							finalResult.add(lex);
					} else if(dotFound == true) {
						if(lex.matches(changeChar) == true && noLSymbol == false && dummyBound == true) {
							finalResult.add(changeTo);
						} else if(lex.matches(changeChar) == true && noLSymbol == true) {
							finalResult.add(lex);
						} else if(lex.matches(changeTo) == true) {
							return "No conversion.";
						} else 
							finalResult.add(lex);
					}
				} else if(lex.matches("\\.") == true) {
					activeLSymbol = false;
					dotFound      = true;
					finalResult.add(lex);
				} else if(lex.matches("\\(") == true) {
					if(openFlag == false) 
						openFlag = true;
					openPar++;
					finalResult.add(lex);
				} else if(lex.matches("\\)") == true) {
					closePar++;
					if(openPar == closePar) {
						openFlag = false;
						if(inPar == true) {                     // Abstraction is in a parenthesis.
							activeLSymbol = false;             
							if(boundInPar == true) {            // If the dummy bound variable is part inside a parenthesis.
								dummyBound = false;				// Another Abstraction expression can hold a dummy bound.
								noLSymbol  = true;				// Free variables
								boundInPar = false;
							} 
							inPar = false;
						} else {
							dummyBound = false;
							noLSymbol  = true;
						}
					}
					finalResult.add(lex);
				} else if(lex.equals(" "))
					finalResult.add(lex);
			}
		}
		if(!String.join("", finalResult).equals(String.join("", listOfExpr)))
			return String.join("", finalResult);
		else
			return "No conversion.";
	}
	
	// Perform Beta Reduction. 
	public String getReduction() {
		String lex  = null, arguement = null;
		boolean dontReduce = false, noLeftOver = false;
		int    indexOfL = -1, boundIndex = -1;
		StringBuilder exprSb;
		if(listOfExpr == null) 
			return "Invalid Expression.";
		else if(listOfExpr.size() != 1)
			return "No reduction.";
		else if(doReduction(listOfExpr.get(0)) == false)
			return "No reduction.";
		exprSb = new StringBuilder(listOfExpr.remove(0));
		for(int counter = 0; counter < parameters.size(); counter++) {    
			boolean activeLSymbol = false, inBodyL = false, dotFound = false, dummyBound = false;
			arguement = parameters.get(counter);  
			for(int index = 0; index < exprSb.length() && dontReduce == false; index++) {
				lex = Character.toString(exprSb.charAt(index));
				if(lex.matches("L") == true) {
					if(activeLSymbol == false) {
						indexOfL      = index;
						activeLSymbol = true;
						dotFound      = false;
					} else if(dotFound == true)
						inBodyL = true;
					else
						return "No reduction.";
				} else if(lex.matches("[a-z]") == true) {
					if(dotFound == false) {
						if(dummyBound == false) { 
							setAction(lex, arguement);      // Lock dummy variable as a possible bound variable.
							boundIndex = index;
							dummyBound = true;
						} else if(lex.matches(changeTo) == true) {
							return "No reduction.";
						}
					} else if(dotFound == true) {
						if(lex.matches(changeChar) == true && dummyBound == true) {  
							if(inBodyL == true && lex.matches(changeChar) == true)
								return "No reduction.";
							if(changeTo.length() > 1) {
								exprSb.delete(index, index + 1);
								exprSb.insert(index, changeTo);                 // Note:   Once inserted, the loop will consider the characters of the inserted string.
								index += changeTo.length() - 1;                 // Action: Point index after the last character of the changeTo String.
							} else {
								exprSb.setCharAt(index, changeTo.charAt(0));
							}
						} else if(lex.matches(changeTo) && dummyBound == true) {
							return "No reduction.";
						} 	
					} 
				} else if(lex.matches("\\.") == true) {
					dotFound = true;
					if(inBodyL == true)
						inBodyL = false;
				} 
				if(index == exprSb.length() - 1 && activeLSymbol == true) {
					parameters.remove(counter);
					counter -= 1;
					exprSb.deleteCharAt(boundIndex);                                  // The dummy variable will be removed.
					if(exprSb.length() > 1 && exprSb.charAt(indexOfL + 1) == '.') {   // Remove the Lambda symbol together with its dot.
						exprSb.delete(indexOfL, indexOfL + 2);
						activeLSymbol = false;
						dontReduce    = true;
						noLeftOver    = true;
					}
				}
			}
		}
		if(doReduction(exprSb.toString()) == true && noLeftOver == true) {     // Beta Reduction continues..
			exprSb.deleteCharAt(exprSb.indexOf("("));                          // Remove original parenthesis.
			exprSb.deleteCharAt(exprSb.lastIndexOf(")"));
			parameters.addAll(0, getParameters(exprSb.toString(), true));      // Prepare necessary requirements.
			setListOfExpr(exprSb.substring(0, lengthParenthesis(exprSb.toString()) + 1));
			return getReduction();                                             // Recursive call
		} 
		if(!exprSb.toString().equals(expr))
			return exprSb.toString() + " " + String.join("", parameters);
		else
			return "No reduction.";
	} 
	
	// Check if it can perform Beta Reduction.
	private boolean doReduction(String expr) {
		String lex = null;
		boolean getIndexOfL = true, getIndexOfVar = true;
		int     indexOfL = expr.length() + 1, indexOfVar = expr.length() + 1;
		for(int index = 0; index < expr.length(); index++) {
			lex = Character.toString(expr.charAt(index));
			if(lex.matches("L") == true && getIndexOfL == true) {
				indexOfL = index;
				getIndexOfL = false;
			} else if(lex.matches("[a-z]") == true && getIndexOfVar == true) {
				indexOfVar = index;
				getIndexOfVar = false;
			}
		}
		if(indexOfL < indexOfVar)  
			return true;
		return false;
	}
}
