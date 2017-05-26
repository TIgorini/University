.386

Data1 segment use16
	I1	db	?
	I2	db	?
	I3	db	?
	A1	dw	4 dup (5 dup (6 dup (0)))
Data1 ends

Code1 segment use16 
	ASSUME CS:Code1, DS:Data1

start:
	mov		ax, Data1
	mov 	ds, ax
	mov		i1, 0
	mov		i2, 0
	mov		i3, 0

	_loop:
		xor		ax, ax
		mov		al, i1	
		imul 	bx, al, 5*6*2
		xor		ax, ax
		mov		al, i2
		imul	bp, al, 6*2
		xor		si, si
		xor		ax, ax
		mov		al, i3
		mov		si, al
		shl		si, 1
		lea		di, [bp + si]	
		
		xor		ax, ax
		mov		al, i1
		add		al, i2
		add		al, i3
 
		test	al, 1
		jnz		short odd
		
		test 	i1, 1
		jnz		short odd

		test	i2, 1
		jnz		short odd
		mov		A1[bx + di], ds
		
		jmp		short end_if	 

		odd:
			lea 	si, A1[bx + di]
			mov		A1[bx + di], si

		end_if:
			inc 	i3
			cmp		i3, 6
			jb		_loop 

			mov		i3, 0
			inc		i2
			cmp		i2, 5
			jb		_loop

			mov		i2, 0
			inc		i1
			cmp		i1, 4
			jb		_loop
 	
 	jmp far ptr _label
Code1 ends


Data2 segment use16	
	A2	dw	4 dup ( 5 dup (6 dup (0)))
Data2 ends


Code2 segment use16
	_label label far	
	Assume cs:Code2, ds:Data1, es:Data2

start2:
	mov 	ax, Data2
	mov 	es, ax

	lea  	di, A2
	lea 	si, A1
	mov		cx, 4*5*6
	rep		movsw

	lea 	di, A2
	mov 	cx, 4*5*6
	mov 	ax, ds
	mov 	bx, -1

	cld

	_loop2:
		repne	scasw
		inc		bx
		cmp 	cx, 0
		jne 	_loop2	

	mov ax, 4C00h
  	int 21h
Code2 ends	

end start