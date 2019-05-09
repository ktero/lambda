import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		while(choice != 5) {
			System.out.println("\n1. Generate expression\n2. Alpha conversion\n3. Beta reduction\n4. Verify expression\n5. Close program");
			System.out.print("Choice: ");
			choice = sc.nextInt();
			switch(choice) {
				case 1: generateUI(); break;
				case 2: alphaUI(sc); break;
				case 3: betaUI(sc); break;
				case 4: verificationUI(sc); break;
				case 5: System.out.println("\nGoodbye"); break;
				default: break;
			}
		}
		sc.close();
	}

	// Generate lambda expression
	static void generateUI() {
		Lambda gn = new Lambda();
		System.out.println("Program generated: " + gn.generateExpression());
	}

	// Perform alpha conversion
	static void alphaUI(Scanner sc) {
		System.out.print("\nEnter a lambda expression: ");
		String expr = sc.next();
		System.out.print("Convert: ");
		char converte = sc.next().charAt(0);
		System.out.print("Into:    ");
		char convert  = sc.next().charAt(0);
		Lambda cv = new Lambda(expr, Character.toString(converte),Character.toString(convert));
		System.out.println("\nInput:  " + expr + "\nResult: " + cv.getConversion());
	}

	// Perform beta reduction
	static void betaUI(Scanner sc) {
		System.out.print("\nEnter a lambda expression: ");            // Main expression should be enclosed with Parenthesis.
		String expr = sc.next();
		System.out.print("Enter parameter: ");
		String parameters = sc.next();
		Lambda rd = new Lambda(expr, new Analyzer().getParameters(parameters, false));
		System.out.println("\nInput:  " + expr + parameters + "\nResult: " + rd.getReduction());
	}

	// Verify a given lambda expression
	static void verificationUI(Scanner sc) {
		Analyzer a = new Analyzer();
		System.out.print("\nEnter a lambda expression: ");
		String expr = sc.next();
		System.out.println("The expression is: " + a.parseExpression(expr));
	}
}
