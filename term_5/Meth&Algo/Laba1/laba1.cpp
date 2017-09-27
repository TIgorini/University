#include <iostream>
#include <math.h>
#define A 50.1
#define B 95

using namespace std; 

double setx(double x, int &m){

	do {
		m = m + 1;
	} while (!((x < pow(2, m) && x > pow(2, m)*0.5)));
	
	return x/pow(2, m);
}

int found_n(double eps, double z){

	double a = (1-z)/(1+z);
	double lk = a;
	int k = 1; 
	while (lk > 4*eps){
		k += 1;
		lk = pow(a, 2*k-1)/(2*k-1);
	}
	return k;
} 

double sum(int n, double z){

	double a = (1-z)/(1+z);
	double sum = 0;
	for (int k = 1; k <= n; k++)
		sum += pow(a, 2*k-1)/(2*k-1);
	return sum;
}


int main(){

	double x = (A + B)/2;

	printf("\nx = %.2f\n\n", x);
	printf("   eps     n       R        alt-j   \n");
	printf("------------------------------------\n");

	int n;
	double r, lnx;
	int m = 0;
	double z = setx(x, m);
	for (double eps = 1e-2; eps >= 1e-14; eps *= 1e-3){

		n = found_n(eps, z);
		r = pow(1.0/3, 2*n-1)/4*(2*n+1);
		lnx = m*log(2) - 2*sum(n, z) - r;
		printf("  %.0e\t   %d\t%.2e   %.2e\n", eps, n, r, abs(lnx - log(x)));
	}  

	n = 7;
	printf("\nn = %d", n);
	printf("\n   x        R        alt-j   \n");
	printf("-----------------------------\n");

	r = pow(1.0/3, 2*n-1)/4*(2*n+1);
	for (int i = 0; i <= 10; i++){
		m = 0;
		x = A + i*(B - A)/10;
		z = setx(x, m);
		lnx = m*log(2) - 2*sum(n, z) - r;
		printf(" %.2f\t %.2e   %.2e\n", x, r, abs(lnx - log(x)));
	}

	return 0;
}