/* Laba2, "РОЗВ’ЯЗАННЯ РІВНЯНЬ З ОДНИМ НЕВІДОМИМ"
 * Tymoshenko Igor, KV-51
 * 19. x - 5*sin(x - PI/4) = 0, (I,Д)
 */
#define _USE_MATH_DEFINES

#include <iostream>
#include <cmath>

using namespace std;

double f(double x){
	return x - 5*sin(x - M_PI_4);
}

double df(double x){
	return 1 - 5*cos(x - M_PI_4);
}

double d2f(double x){
	return 5*sin(x - M_PI_4);
}

double phi(double x, double lambda){
	return (df(x) > 0) ? (x - lambda*f(x)) : (x + lambda*f(x));
}

double increment(double a, double b, double eps, double &delta, int &iters){
	double m1 = abs(df(a)), M1 = abs(df(b));
	if (m1 > M1){
		swap(m1, M1);
	} 
	double x0, xk = (a+b)/2;
	double lambda = 1.0/M1;
	double q = 1 - m1/M1;
	iters = 0;
	do {
		x0 = xk;
		xk = phi(x0, lambda);
		iters++;
	} while (abs(x0-xk) > (1-q)/q*eps);

	delta = abs(x0-xk)*q/(1-q); 
	return xk; 
}

double tangents(double a, double b, double eps, double &delta, int &iters){
	double m1 = min(abs(df(a)), abs(df(b)));
	double xk = (f(a)*d2f(a) > 0) ? a : b;
	iters = 0;

	while (abs(f(xk))/m1 > eps){
		xk = xk - f(xk)/df(xk);
		iters++;
	}

	delta = abs(f(xk))/m1;
	return xk; 
}


void printTables(double a, double b){
	int iters = 0;
	double delta = 0; 
	double x = 0;
	printf("\t\t\t[%.1f;%.1f]\n", a, b);
	printf("    Increment method\t\t    Tangents method\n\n");
	printf("   eps     x\t delta\t\t   eps    x\tdelta\n");
	printf("-------------------------\t-------------------------\n");
	for (double eps = 1e-2; eps >= 1e-14; eps *= 1e-3){
		x = increment(a, b, eps, delta, iters);
		printf("  %.0e\t %.2f\t%.2e\t", eps, x, delta);
		x = tangents(a, b, eps, delta, iters);
		printf("  %.0e\t %.2f\t%.2e\n", eps, x, delta);
	}
	printf("\n");
}

void compareTable(double a, double b){
	int incIters = 0;
	int tanIters = 0;
	double delta = 0; 

	printf("\n\tCompare table\n\n   eps  increment   tangents\n");
	printf("-----------------------------\n");
	for (double eps = 1e-2; eps >= 1e-14; eps *= 1e-3){
		increment(a, b, eps, delta, incIters);
		tangents(a, b, eps, delta, tanIters);
		printf("  %.0e\t    %d\t\t%d\n", eps, incIters, tanIters);
	}
	printf("\n");
}


int main(){

	printTables(-2.2, -1.8);
	printTables(0.8, 1.2);
	printTables(3, 3.5);
	compareTable(-2.2, -1.8);
	return 0;
}

