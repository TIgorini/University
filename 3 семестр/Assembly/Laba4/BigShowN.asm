.686 
.model flat,C 

public BigShowN 

.const 

NewLine	db	 10,13,0 
Space	db	 32,0 
Symbol	db	 '%c',0 

.code 

printf PROTO arg1:Ptr Byte, printlist: VARARG 
	
;*****************************************
; п/п виведення на екран в hex-форматі
; даних із регістра esi:
; якщо di=28, то виводяться всі 4 байти
; якщо di=20, то виводяться 3 молодші байти
; якщо di=12, то виводяться 2 молодші байти
; якщо di=4, то виводиться один молодший байт

show_bt proc
	
	pushad
	mov		bx,di
bt0:
	mov		edx,esi
	mov		cl,bl
	shr		edx,cl
	and		dl,00001111b
	cmp		dl,10
	jl		bt1
	add		dl,7

bt1:
	add dl,30h
	invoke printf, offset Symbol, dl ; написати цифру у 16-ковому форматі
	sub bl,4
	jnc bt0
	invoke printf, offset Space ; записати один пробіл
	
	popad
	ret

show_bt endp

; void BigShowN(byte* p1, int p2)
BigShowN proc
	
	@mas	equ		[ebp+8]		; місцезнаходження адреси масиву
	@len	equ		[ebp+12]	; місцезнаходження кількості

	push ebp
	mov ebp,esp 

	; перехід на новий рядок без збереження ecx
	invoke	printf, offset NewLine	; перехід на новий рядок

;-----------------------------------------------
; обчислення кількості пробілів у першому рядку
;-----------------------------------------------
	
	mov		ax, @len
	test	ax, 00000011b
	pushf
	shr		ax,2
	popf
	jz		@1
	inc		ax
@1:
	xor		bx,bx
	mov		di,28
	and		ax,00000111b
	jz		@2

; формування пробілів по відсутніх подвійних словах
	mov		ah,8
	sub		ah,al
	mov		al,ah
	xor		ah,ah
	imul	ax,9	;8+1
	mov		bx,ax
@2:
	mov dx,@len
	and dx,00000011b
	jz l000
	
; формування початкового значення кількості зсувів
	mov		di,dx	;di - 1 2 3
	dec		di		;di - 0 1 2
	shl		di,3	;di - 0 8 16
	add		di,4	;di - 4 12 20
	
; формування пробілів по відсутніх байтах у подвійному слові
	mov		dh,4
	xchg	dh,dl	;dh - 1 2 3
	sub		dl,dh	;dl - 3 2 1
	shl		dl,1	;dl - 6 4 2
	xor		dh,dh	;dx - 6 4 2
	add		bx,dx
l000:
	jcxz	l002

; виведення початковіх пробілів у першому рядку (ecx не зберігається)
l001:
	invoke	printf, offset Space ; вивести один пробіл
	dec		bx
	cmp		bx,0
	jne		l001
l002:
	xor		ecx,ecx
	mov		cx,@len
	shr		cx,2
	cmp		di,28
	jz		@3
	inc		cx
@3:
	mov		ebx,@mas			; записуємо в ebx адресу масиву
	lea		ebx,[ebx+ecx*4]-4	; записуємо в ebx адресу останнього (найстаршого) елемента масиву

; виведення масиву (з найстаршого елементу до наймолодшого)
l004:
	mov		esi, dword ptr [ebx]	; зберігаємо 4 байти масиву для виведення
	sub		ebx,4					; переміщуємо ebx на молодші 4 байти
	call	show_bt					; виклик функції виведення
	mov		di,28
	dec		cx
	test	cx,7	; 7 = 0111b
	jne l005

; перехід на новий рядок зі збереженням ecx
	push	ecx						; printf змінює есх, тому треба його зберегти
	invoke	printf, offset NewLine	; перехід на новий рядок
	pop ecx
l005:
	jcxz l006
	jmp l004
l006:
	invoke	printf, offset NewLine	; перехід на новий рядок
	
	pop ebp
	ret
BigShowN endp
END