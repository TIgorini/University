TT2 SEGMENT
    V1  Dw ?, ?
    V2  DW ?

?loop0:
    MOV	AX, V1
    MOV	BX, 42
    CMP	AX, BX
    JNE	?loop0_end

?loop1:
    MOV	AX, V2
    MOV	BX, V1
    CMP	AX, BX
    JL	?loop1_end
    NOP
    JMP	?loop1
?loop1_end:
    JMP	?loop0
?loop0_end:

?loop2:
    MOV	AX, 12
    MOV	BX, 34
    CMP	AX, BX
    JGE	?loop2_end
    NOP
    JMP	?loop2
?loop2_end:
TT2 ENDS
