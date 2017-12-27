/* Laba3, "РОЗВ’ЯЗАННЯ СЛАР"
 * Tymoshenko Igor, KV-51
 * 19.  	| 15 12 18 14 |			| 209 |
 *		A =	| 18 36 10  7 |     B = | 301 |
 *			|  1  5 10  3 |			|  94 |
 *			| 19  2 13  9 |			| 118 |
 *		
 *		1) виключення Гаусса-Жордана 
 *		2) метод простої iтерацiї
 */

#include <iostream>
#include <math.h>
#include <cstring>

#define N 4

const double a[N][N] = {{15, 12, 18, 14},
						{18, 36, 10,  7},
						{ 1,  5, 10,  3},
						{19,  2, 13,  9}};
const double b[N] = {209, 301, 94, 118};


void print_matrix(const double a[N][N], const double b[N]){
	for (int i = 0; i < N; i++){
		printf("  ");
		for (int j = 0; j < N; j++){
			printf("%.2f\t ", a[i][j]);
		}
		printf("|  %.2f\n", b[i]);
	}
}

void print_res(const double b[N]){
	printf("Vector X:\n");
	for (int i = 0; i < N; i++)
		printf("  %.2f\n", b[i]);
}


int GJ(double a[N][N], double b[N]){
	for (int k = 0; k < N; k++){
		if (a[k][k] == 0)
			return 1;

		double akk = a[k][k];
		for (int i = 0; i < N; i++){
			a[k][i] = a[k][i]/akk;
		}
		b[k] = b[k]/akk;

		for (int i = 0; i < N; i++){
			if (i == k)
				continue;

			double aik = a[i][k];
			for (int j = 0; j < N; j++){
				a[i][j] = a[i][j] - a[k][j]*aik; 
			}
			b[i] = b[i] - b[k]*aik;
		}
	}
	return 0;
}


int direct_iter(double a[N][N], double b[N], double eps){
	double x[N], x_prev[N];
	double sum;
	double norm;
	double q = 0;

	for (int i = 0; i < N; i++){
		sum = 0;
		for (int j = 0; j < N; j++){
			sum += abs(a[i][j]);
		}
		if (sum > q)
			q = sum;
	}

	memcpy(x_prev, b, N * sizeof(double));
	for (int i = 0; i < N; i++){
		sum = 0;
		for (int j = 0; j < N; j++)
			sum += a[i][j] * x_prev[j];
		x_prev[i] = b[i] + sum; 
	}

	int k = 2;
	while (true){
		for (int i = 0; i < N; i++){
			x[i] = b[i]; 
			for (int j = 0; j < N; j++)
				x[i] += a[i][j] * x_prev[j];
		}
		
		norm = 0;
    	for (int i = 0; i < N; i++) {
        	norm += (x[i] - x_prev[i])*(x[i] - x_prev[i]);
    	}
		norm = sqrt(norm);

		if (norm <= eps*(1 - q)/q)
            break;

        k++;
		memcpy(x_prev, x, N * sizeof(double));
	}

	memcpy(b, x, N * sizeof(double));
	return k;
}


int main(){
	double buffA[N][N];
	double buffB[N];

	printf("Matrix A and B:\n");
	print_matrix(a, b); printf("\n");

	//метод исключения Гаусса-Жордана
	memcpy(buffA, a, N * N * sizeof(double));
	memcpy(buffB, b, N * sizeof(double));
	
	printf("\n1) Gauss-Jordan elimination: \n");
	if ( GJ(buffA, buffB) == 1 ){
		printf("Method cant`t solve this system\n\n");
	} else 
		print_matrix(buffA, buffB); printf("\n");
		print_res(buffB);


	//метод простой итерации
	memcpy(buffA, a, N * N * sizeof(double));
	memcpy(buffB, b, N * sizeof(double));

	printf("\n\n2) Direct iteration:\n");

	//сведение матрицы к пригодному для итерации виду 
	for (int j = 0; j < N; j++){
		buffA[0][j] = a[0][j] - 2*a[2][j];
		buffA[3][j] = -4*a[0][j] + a[1][j] + 2*a[2][j] + 2*a[3][j];
	}
	buffB[0] = b[0] - 2*b[2];
	buffB[3] = -4*b[0] + b[1] + 2*b[2] + 2*b[3];
	print_matrix(buffA, buffB); printf("\n");

	//превращение системы
	double aii;
	for (int i = 0; i < N; i++){
		aii = buffA[i][i];
		for (int j = 0; j < N; j++){
			if (i == j)
				buffA[i][i] = 0;
			else 
				buffA[i][j] = -buffA[i][j]/aii;

		}
		buffB[i] = buffB[i]/aii;
	}

	double eps = 10e-3;
	int k;
	k = direct_iter(buffA, buffB, eps);
	print_res(buffB);
	printf("\nFor eps = %.0e:  %d itarations\n\n", eps, k);

	return 0;
}