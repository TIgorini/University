data SEGMENT BYTE 
	val		db 00100110b	; 76543210	->  54165111	
data ENDS

code SEGMENT
	ASSUME cs:code, ds:data

begin:	 
	mov		ax, data
	mov 	ds, ax

	mov 	dl, val
	xor		dh, dh			

	mov 	bl, dl 
	and		bl, 00000010b
	shr		bl, 1
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00000010b
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00000010b
	shl		bl, 1
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00100000b
	mov 	cl, 2
	shr		bl, cl
	or		dh, bl

	mov 	bl, dl 
	and		bl, 01000000b
	mov 	cl, 2
	shr		bl, cl
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00000010b
	mov		cl, 4
	shl		bl, cl
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00010000b
	mov		cl, 2
	shl		bl, cl
	or		dh, bl

	mov 	bl, dl 
	and		bl, 00100000b
	mov		cl, 2
	shl		bl, cl
	or		dh, bl
	nop

	mov 	ax, 4c00h
	int 	21h

code ENDS 
	END 	begin 
