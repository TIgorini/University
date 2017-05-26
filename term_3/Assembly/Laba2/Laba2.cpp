#include <stdio.h>
int i, m;

void cppCode() {

	int A[9];
	m = 0;
	i = 8;
	do {
		m = 8 * i;
		switch (i) {
		case 2: m += 4; break;
		case 0: m = 17; break;
		case 7: m -= 4; break;
		case 1: m = 4; break;
		default: m++;
		}
		A[i] = m;
		i--;
	} while (i>-1);
	for (i = 0;i<9;i++)
		printf("%d ", A[i]);
	printf("\n");
}

void asmCode() {

	int A[9];
	m = 0;
	i = 8;
	_asm {
		// 7    : 	do {
		// 8    : 		m = 8 * i;
		repeat:
			mov	 eax, i
			shl	 eax, 3
			mov	 m, eax
		// 9    :	`	switch (i) {
			mov	 ecx, i
			cmp	 ecx, 7
			je	 SHORT case3
			cmp	 ecx, 2
			je	 SHORT case1
			cmp	 ecx, 1
			je	 SHORT case4
			cmp	 ecx, 0
			je	 SHORT case2
		// 14   : 		default: m++;
			inc  m
			jmp	 SHORT break
		// 10   : 		case 2: m += 4; break;
		case1:
			add	 eax, 4
			mov	 m, eax
			jmp	 SHORT break
		// 11   : 		case 0: m = 17; break;
		case2 :
			mov	 m, 17
			jmp	 SHORT break
		// 12   : 		case 7: m -= 4; break;
		case3 :
			sub	 eax, 4
			mov	 m, eax
			jmp	 SHORT break
		// 13   : 		case 1: m = 4; break;
		case4 :
			mov	 m, 4
		// 15   :		}
		// 16   : 		A[i] = m;
		break:
			mov	 eax, m
			mov	 A[ecx * 4], eax
		// 17   : 		i--;
			dec  i
		// 18   :		} while (i>-1);
			cmp	 i, -1
			jg	 repeat
		}
		for (i = 0;i<9;i++)
			printf("%d ", A[i]);
		printf("\n");
}

int main() {

	cppCode();
	asmCode();

	return 0;
}