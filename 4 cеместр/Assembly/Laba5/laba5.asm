.386
data segment use16		
	value	db	160 dup (03h,00001100b)
	count 	dw	80
data ends

code segment use16
	Assume 	ds:data, cs:code

click proc far
	push   	ds
	push   	es
	pusha  
	push   	0b800h
	pop		es

	cmp		bx, 1		;button clicked: right or left
	je		left
	mov 	bx, 0
	jmp		row_loop 
left:
	mov 	bx, 1
row_loop:	
	mov 	ax, bx
	imul	ax, 80*2
	mov		bp, ax
	mov 	di, 1
	col_loop:
		mov 	al, byte ptr es:[bp+di]
		xor 	al, 00001100b
		mov 	es:[bp+di], al
		add		di, 2
		cmp		di, 80*2
		jb 		col_loop
	add 	bx, 2
	cmp		bx, 24
	jb		row_loop

	popa
	pop 	es
	pop 	ds
	ret
click endp

start:
	mov 	ax, data
	mov		ds, ax
		 
	mov	    ax, 3		;start text mode
	int 	10h

	push 	es
	push 	0b800h		;monitor filling
	pop 	es
	mov 	bx, 0
write:	
	mov		si, offset value
	mov 	ax, bx
	imul	ax, 80*2
	mov		di, ax
	mov 	cx, count
	rep 	movsw
	inc 	bx
	cmp		bx, 24
	jb		write
	pop 	es
	
	xor 	ax, ax		;start mouse handle
	int 	33h
	mov		ax, 0ch		
	mov		cx, 1010b
	push 	es
	push 	cs
	pop 	es
	lea 	dx, click
	int		33h
	pop 	es

	mov 	ah, 01h		;waiting for key press	
	int		21h		 

	xor 	cx,cx		;stop mouse handle
	mov		ax,0ch		
	int		33h

	mov		ax, 4c00h
	int		21h

code ends
end	start	