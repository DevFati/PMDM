package ejercicios;

import java.util.Scanner;

public class Ejercicio1 {

	public static void main(String[] args) {
		Scanner teclado=new Scanner(System.in);
		
		System.out.println("Introduce los siguientes datos: ");
		System.out.print("Introduce el dato a: ");
		int a=teclado.nextInt();
		System.out.print("Introduce el dato b: ");
		int b=teclado.nextInt();
		System.out.print("Introduce el dato c: ");
		int c=teclado.nextInt();
		
		double resultado1=((double)(-b+Math.sqrt(Math.pow(b, 2)-4*a*c))/(2*a));
		double resultado2=((double)(-b-Math.sqrt(Math.pow(b, 2)-4*a*c))/(2*a));
		
		System.out.println("Resultado 1: "+resultado1);
		System.out.println("Resultado 2: "+resultado2);
		
		
	}
}
