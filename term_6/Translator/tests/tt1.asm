TT1 SEGMENT
    V1  Dw ?, ?
    V2  DW ?

?loop0:
    MOV	AX, V1
    MOV	BX, 42
    CMP	AX, BX
    JNE	?loop0_end
    NOP
    JMP	?loop0
?loop0_end:
TT1 ENDS
