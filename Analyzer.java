import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class Analyzer{

	private final String lamSymbol;
	private String variable;
	private List<String> req;

	// Parser constructor; Initialize data members;
	public Analyzer() {
		variable  = "[a-z. ]";
		lamSymbol = "[L]";
	}

	// Identify the length of the parentheses's contents.
	public int lengthParenthesis(String expr) {
		int openPar = 0, closePar = 0, length = 0;
		String lex = null;
		for(int index = 0; index < expr.length(); index++) {
			lex = Character.toString(expr.charAt(index));
			if(lex.matches("\\(") == true) {
				openPar++;
			} else if(lex.matches("\\)") == true) {
				closePar++;
				if(openPar == closePar)
					return length;
			}
			length++;
		}
		return 0;
	}

	// Identify the length of the parentheses's contents.
	public int lengthParenthesis(String expr, int startAt) {
		int openPar = 0, closePar = 0, length = startAt;
		String lex = null;
		for(int index = startAt; index < expr.length(); index++) {
			lex = Character.toString(expr.charAt(index));
			if(lex.matches("\\(") == true) {
				openPar++;
			} else if(lex.matches("\\)") == true) {
				closePar++;
				if(openPar == closePar)
					return length;
			}
			length++;
		}
		return 0;
	}

	// Identify parameters.
	public List<String> getParameters(String expr, boolean state) {
		req = new ArrayList<String>();
		int initial = 0;
		if(state == true)
			initial = lengthParenthesis(expr);

		String lex  = null;
		for(int index = initial; index < expr.length(); index++) {
			lex = Character.toString(expr.charAt(index));
			if(lex.matches("[a-z]") == true) {
				req.add(lex);
			} else if(lex.matches("\\(") == true) {
				req.add(expr.substring(index, lengthParenthesis(expr, index) + 1));
				index = lengthParenthesis(expr, index);
			}
		}
		return req;
	}

	// Function will divide the given lambda expression.
	public List<String> splitExpr(String expr) {
		req = new ArrayList<String>();
		int openPar = 0, closePar = 0, start = 0, end = 0, pos = -1;
		boolean openFlag = false, status = false, getLetters = true;
		for(int index = 0; index < expr.length() && status == false; index++) {
			if(expr.charAt(index) == 'L' && openFlag == false) {
				if(pos != -1)                   // Updated: 9/30/17 <- from 0 to -1
					req.add(expr.substring(pos));
				else
					req.add(expr.substring(index));
				status = true;
			} else if(expr.charAt(index) != '(' && expr.charAt(index) != ')') {
				if(openFlag == false) {
					if(getLetters == true) {
						pos = index;
						getLetters = false;
					} if(expr.substring(pos).indexOf('(') == -1) {
						req.add(expr.substring(pos, expr.length()));
						status = true;
					}
				} else if(openFlag == true && getLetters == false) {
					req.add(expr.substring(pos, index - 1));
					getLetters = true;
				}
			} else if(expr.charAt(index) == '(') {
				openPar++;
				if(openFlag == false) {
					start = index;
					openFlag = true;
				}
			} else if(expr.charAt(index) == ')' && openFlag == true) {
				closePar++;
				if(openPar == closePar) {
					end = index;
					openFlag   = false;
					req.add(expr.substring(start, end + 1));
					pos = -1; // reset
				}
			} else if(expr.charAt(index) == ')' && openFlag != true) {
				status = true;
			}
		}
		return req;
	}

	// Function will analyze a given expression whether it is valid or not.
	public boolean parseExpression(String expr) {
		req = new ArrayList<String>();
		String  lex  = null;
		int openPar = 0, closePar = 0;
		boolean lookBehind = false, activeLSymbol = false, openFlag = false;
		for(int index = 0; index < expr.length(); index++) {
			lex = Character.toString(expr.charAt(index));
			if(lex.matches("[a-z ]") == true) {                 // If character is a variable from a-z (including whitespace).
				if(lookBehind == false) {
					req.add(lex);
					lookBehind = true;
				} else if(lookBehind == true) {
					if(req.get(index - 1).matches("[a-z ]") == true) {
						req.add(lex);
					} else if(req.get(index - 1).matches("L") == true && activeLSymbol == true) {
						req.add(lex);
					} else if(req.get(index -1).matches(".") == true) {
						req.add(lex);
					} else if(req.get(index - 1).matches("\\(") == true) {
						req.add(lex);
					} else if(req.get(index - 1).matches("\\)") == true) {
						req.add(lex);
					} else
						return false;
				}
			} else if(lex.matches("L") == true) {                 // If character is a Lambda(L) symbol.
				if(lookBehind == false && activeLSymbol == false) {
					req.add(lex);
					activeLSymbol = true;
					lookBehind    = true;
				} else if(lookBehind == true && activeLSymbol == false) {
					if(req.get(index - 1).matches("[a-z ]") == true) {
						activeLSymbol = true;
						req.add(lex);
					} else if(req.get(index - 1).matches(".") == true) {
						activeLSymbol = true;
						req.add(lex);
					} else if(req.get(index - 1).matches("\\(") == true) {
						activeLSymbol = true;
						req.add(lex);
					} else if(req.get(index - 1).matches("\\)") == true) {
						activeLSymbol = true;
						req.add(lex);
					}
				} else
					return false;
			} else if(lex.matches("\\.") == true) {                 // If character is a dot(.) symbol.
				if(lookBehind == false)
					return false;
				else if(lookBehind == true) {
					if(req.get(index - 1).matches("[a-z ]") == true && activeLSymbol == true && index != expr.length() - 1) {
						req.add(lex);
						activeLSymbol = false;
					} else
						return false;
				}
			} else if(lex.matches("\\(") == true) {       // If character is a Open parenthesis.
				if(lookBehind == false) {
					openPar++;
					req.add(lex);
					if(openFlag == false)
						openFlag = true;
					lookBehind = true;
				} else if(lookBehind == true) {
					if(req.get(index - 1).matches("[a-z ]") == true) {
						if(openFlag == false)
							openFlag = true;
						openPar++;
						req.add(lex);
					} else if(req.get(index - 1).matches(".") == true) {
						if(openFlag == false)
							openFlag = true;
						openPar++;
						req.add(lex);
					} else if(req.get(index - 1).matches("\\(") == true) {
						openPar++;
						req.add(lex);
					} else
						return false;
				}
			} else if(lex.matches("\\)") == true) {         // If character is a Close parenthesis.
				if(lookBehind == false) {
					return false;
				} else if(lookBehind == true && openFlag == true) {
					if(req.get(index - 1).matches("[a-z ]") == true) {
						closePar++;
						req.add(lex);
					} else if(req.get(index - 1).matches("\\)") == true) {
						closePar++;
						req.add(lex);
					} else
						return false;
				}
			} else if(lex.equals(" ")){
				req.add(lex);
			} else
				return false;
		}
		if(String.join("", req).equals(expr) && openPar == closePar && activeLSymbol == false)
			return true;
		else
			return false;
	}

	/* Functions below serves as a guide to create/generate a valid expression. */

	public boolean varRule(String prevChar) { // If expression only has 1 character
		if(Pattern.matches(variable, prevChar) == true)
			return true;
		return false;
	}
	public boolean varRule(String prevChar, String newChar) {
		if(Pattern.matches(variable, prevChar) == true && Pattern.matches(variable, newChar) == true)
			return true;
		return false;
	}
	public boolean absRule(String prevChar, String newChar) { // Function override, exclusively used when checking for a complete given lambda expression.
		if(Pattern.matches(variable, prevChar) == true && Pattern.matches(lamSymbol, newChar) == true)
			return true;
		return false;
	}
	public boolean absRule(String prevChar, String newChar, int remLength) {
		if(Pattern.matches(variable, prevChar) == true && (Pattern.matches(lamSymbol, newChar) == true && remLength >= 4))
			return true;
		return false;
	}
	public boolean dummyRule(String prevChar, String newChar) {
		if(Pattern.matches(lamSymbol, prevChar) == true && Pattern.matches(variable, newChar) == true)
			return true;
		return false;
	}
}
