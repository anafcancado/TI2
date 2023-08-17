package soma;

import java.util.*;

public class Soma {
	
	public static Scanner scanner = new Scanner (System.in);
	
	public static void main(String args[]) {
		int num1, num2, soma;
		
		System.out.println("Digite o primeiro numero");
		num1 = scanner.nextInt();
		
		System.out.println("Digite o segundo numero");
		num2 = scanner.nextInt();
		
		soma=num1+num2;
		
		System.out.println("o resultado da soma é: "+soma);
		
	}

}
