data SEGMENT BYTE 
	val		db 00100110b	; 76543210	
	res		db 00000000b	; 54165111	
data ENDS

code SEGMENT
	ASSUME cs:code, ds:data

begin:	 
	mov		ax, data
	mov 	ds, ax

	mov 	dl, val			

	mov 	bl, dl 
	and		bl, 00000010b
	shr		bl, 1
	or		res, bl

	mov 	bl, dl 
	and		bl, 00000010b
	or		res, bl

	mov 	bl, dl 
	and		bl, 00000010b
	shl		bl, 1
	or		res, bl

	mov 	bl, dl 
	and		bl, 00100000b
	mov 	cl, 2
	shr		bl, cl
	or		res, bl

	mov 	bl, dl 
	and		bl, 01000000b
	mov 	cl, 2
	shr		bl, cl
	or		res, bl

	mov 	bl, dl 
	and		bl, 00000010b
	mov		cl, 4
	shl		bl, cl
	or		res, bl

	mov 	bl, dl 
	and		bl, 00010000b
	mov		cl, 2
	shl		bl, cl
	or		res, bl

	mov 	bl, dl 
	and		bl, 00100000b
	mov		cl, 2
	shl		bl, cl
	or		res, bl
	nop

	mov 	ax, 4c00h
	int 	21h

code ENDS 
	END 	begin 
