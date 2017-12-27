/* Laba4, "ЧИСЕЛЬНЕ IНТЕГРУВАННЯ"
 * Tymoshenko Igor, KV-51
 * 19. Функцiя iнтегрування: sin(x)*cos(x)
 *     Межi iнтегрування: [0, 83]
 *     Первiсна: (sin(x))^2 / 2
 *     Метод iнтегруваня: узагальнена формула Сiмпсона
 */

#include <iostream>
#include <math.h>
#include <vector>

double f(double x){
	return sin(x)*cos(x);
}

double d4f(double x){
	return 16*sin(x)*cos(x);
}

double F(double x){
	return sin(x)*sin(x)/2;
} 


double composite_Simpson(double a, double b, double h_par){
	int n = (b-a)/h_par;
	double h = (b-a)/n;
	double res = f(a) + f(b);
	double sigma = 0;
	for (int i = 1; i <= n-1; i += 2){
		sigma += f(a + i*h);
	}
	res += 4*sigma;
	sigma = 0;
	for (int i = 2; i <= n-2; i += 2){
		sigma += f(a + i*h);
	}
	res = h*(res + 2*sigma)/3;
	return res;
}


int main(){
	
	double a = 0;
	double b = 83;
	double h, I;
	std::vector<double> deltas;
	double integralNL = F(b) - F(a);
	double m4 = 8; //max(d4f(x))

	//Таблица 1
	printf("\nNewton-Leibniz integral:  %.2f\n", integralNL);
	printf("\n   eps\t    h\t     I(x)\t delta\n");
	printf("------------------------------------------\n");
	for (double eps = 1e-02; eps >= 1e-8; eps *= 1e-03){
		h = 0.001 * sqrt(sqrt(180*eps/(b - a)/m4));
		I = composite_Simpson(a, b, h);
		deltas.push_back(abs(I - integralNL));

		printf("  %.0e\t %.2e   %.5f\t%.2e\n", eps, h, I, deltas.back());
	}
	printf("\n");

	//Таблица 2
	double In, I2n, eps;
	int n;
	printf("\n    eps\t\th\t delta\n");
	printf("----------------------------------\n");
	for (int i = 0; i < deltas.size(); i++){
		eps = deltas[i];
		n = 1/sqrt(sqrt(eps));

		In = composite_Simpson(a, b, (b - a)/n);
		n = n*2;
		I2n = composite_Simpson(a, b, (b - a)/n);
		while (abs(In - I2n) > 15*eps){
			In = I2n;
			n = n*2;
			I2n = composite_Simpson(a, b, (b - a)/n);
		}

		In = I2n;
		printf("  %.2e   %.2e   %.2e\n", eps, (b - a)/n, abs(In - integralNL));
	}

	return 0;
}