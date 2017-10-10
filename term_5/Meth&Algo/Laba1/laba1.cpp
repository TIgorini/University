#include <iostream>
#include <math.h>
#define A 50.1
#define B 95

using namespace std; 

double setx(double x, int &m){

	do {
		m = m + 1;
	} while (!((x < pow(2, m) && x >= pow(2, m)*0.5)));
	
	return x/pow(2, m);
}

double lk(double a, int k){

	return pow(a, 2*k-1)/(2*k-1);
}

int found_n(double eps, double z){

	double a = (1-z)/(1+z);
	int k = 1; 
	while (lk(a, k) > 4*eps){
		k += 1;
	}
	return k;
} 

double sum(int n, double z){

	double a = (1-z)/(1+z);
	double sum = 0;
	for (int k = 1; k <= n; k++)
		sum += lk(a, k);
	return sum;
}


int main(){

	double x = (A + B)/2;

	printf("\nx = %.2f\n\n", x);
	printf("   eps     n       R        delta   \n");
	printf("------------------------------------\n");

	int n;
	double r, lnx;
	int m = 0;
	double z = setx(x, m);
	for (double eps = 1e-2; eps >= 1e-14; eps *= 1e-3){

		n = found_n(eps, z);
		r = lk((1-z)/(1+z), n + 1);
		//r = pow(1.0/3, 2*n-1)/4*(2*n+1);
		lnx = m*log(2) - 2*sum(n, z) - r;
		printf("  %.0e\t   %d\t%.2e   %.2e\n", eps, n, r, abs(lnx - log(x)));
	}  

	n = found_n(1e-8, z);
	printf("\nn = %d", n);
	printf("\n   x        R        delta   \n");
	printf("-----------------------------\n");

	//r = pow(1.0/3, 2*n-1)/4*(2*n+1);
	for (int i = 0; i <= 10; i++){
		m = 0;
		x = A + i*(B - A)/10;
		z = setx(x, m);
		r = lk((1-z)/(1+z), n + 1);
		lnx = m*log(2) - 2*sum(n, z) - r;
		printf(" %.2f\t %.2e   %.2e\n", x, r, abs(lnx - log(x)));
	}

	return 0;
}