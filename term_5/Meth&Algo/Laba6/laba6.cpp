/* Laba6, "Наближення функцiй за допомогою iнтерполяцiйних сплайнiв"
 * Tymoshenko Igor, KV-51
 * 19. XYZ = 001
 * 	   Спосiб розв`язання системи: cхема єдиного подiлу
 *	   Функцiя: lg(x)ln(10x)sin(2.5x);   [2; 10]
 */

#include <iostream>
#include <math.h>
#include <fstream>
#include <cstring>

#define N 100
#define A 2
#define B 10
#define f(x) log10(x)*log(10*x)*sin(2.5*x)

typedef struct {
	double a, b, c, d, x;
} Coefficients;

using namespace std; 


void make_matrix(double a[N][N], double b[N],const double h){
	memset(a, 0, N*N*sizeof(double));
	memset(b, 0, N*sizeof(double));

	double x = A; 

	a[0][0] = 1; 
	a[N-1][N-1] = 1;
	for (int i = 1; i < N - 1; i++){
		a[i][i-1] = h;
		a[i][i] = 4*h;
		a[i][i+1] = h;
		b[i] = 6*(f(x+h) - 2*f(x) + f(x-h))/h;
	}
}


void gaussian_elem(double a[N][N], double b[N]){
	double x[N];

	for (int k = 0; k < N; k++){
		if (a[k][k] == 0)
			return;

		double akk = a[k][k];
		for (int i = 0; i < N; i++){
			a[k][i] = a[k][i]/akk;
		}
		b[k] = b[k]/akk;
		
		for (int i = k + 1; i < N; i++){
			
			double aik = a[i][k];
			for (int j = 0; j < N; j++)
				a[i][j] = a[i][j] - a[k][j]*aik;

			b[i] = b[i] - b[k]*aik;
		}	
	}

	for (int k = N - 1; k >= 0; k--){
		double sum = 0;
		for (int j = k + 1; j < N; j++){
			sum += a[k][j] * x[j];
		}
		x[k] = b[k] - sum;
	}
	memcpy(b, x, N*sizeof(double));
}


void get_coefficients(Coefficients coeffs[N], double c[N], const double h){
	double x = A;

	coeffs[0].a = f(x);
	coeffs[0].x = x;
	for (int i = 1; i < N; i++){
		x += h;
		coeffs[i].x = x; 
		coeffs[i].a = f(x);
		coeffs[i].c = c[i];
		coeffs[i].d = (c[i] - c[i-1])/h;
		coeffs[i].b = h*c[i]/2 - h*h*coeffs[i].d/6 + (coeffs[i].a - coeffs[i-1].a)/h;
	} 
} 

double spline(Coefficients coeff, double x){
	return coeff.a + coeff.b*(x - coeff.x) + coeff.c/2 * (x-coeff.x)*(x-coeff.x) + coeff.d/6 * (x-coeff.x)*(x-coeff.x)*(x-coeff.x);  
}


int main(){

	Coefficients coeffs[N];
	double a[N][N];
	double b[N];

	const double h = (double)(B - A)/N;
	make_matrix(a, b, h);
	get_coefficients(coeffs, b, h);


	ofstream file("points.csv", ofstream::out);
    double y;
    double x = A + h/2;
    printf("points.csv:\n");
    for (int i = 0; i < N; i++) {
        y = spline(coeffs[i], x);
        file << x << ";" << y << endl;
        printf("%.3f; %.3f\n", x, y);
        x += h;
    }
    file.close();
    printf("File saved successfully\n");

	return 0;
}