.686
.model flat,C

public Biggr

.code
Biggr proc

	push	ebp
	mov		ebp,esp

	mov		esi, [ebp + 8]
	mov		edi, [ebp + 12]
	mov		ecx, [ebp + 16]
	test	ecx, 00000011b
	jz		dw_loop

b_loop:
	dec		ecx
	mov		al, byte ptr [esi + ecx]
	mov		bl, byte ptr [edi + ecx]
	test	ecx, 00000011b
	jz		dw_loop
	cmp		al, bl
	je		b_loop
	jmp		loop_end

dw_loop:
	sub		ecx, 4
	mov		eax, dword ptr [esi + ecx]
	mov		ebx, dword ptr [edi + ecx]
	cmp		ecx, 0
	jl		false
	cmp		eax, ebx
	je		dw_loop	
	 	
loop_end:
	jbe		false
	mov		al, 1
	jmp		if_end	
false:
	mov		ax, 0			
if_end:	

	pop		ebp
	ret
Biggr endp
End
	