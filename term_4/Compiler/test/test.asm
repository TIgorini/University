M2 MACRO SEG
  	OR WORD PTR SEG:[BX+DI+15], 17
ENDM

M1 MACRO
    ROL AX, 1
    ROL BL, 1
ENDM

DAT SEGMENT
    V1  DB  10011B
    S1  DB  "My text example"
    V2  DW  4567
    V3  DD  1598456
DAT ENDS


COD SEGMENT

LB1:
    MOVSW
    MOV BX, AX
    JNA LB3
    MOV AL, CL    
    M2  DS

LB3:
    CMP AX, ES:[BX+DI+9]
    CMP CX, [BP+SI+7]
    CMP BL, DS:[BX+DI]
    CMP DS:[BP+SI+7], CL
    CMP ES:[BX+DI+8], AX
    JNA LB2

LB2:
    M1  
    OR  WORD PTR [BX+SI], "aa" 
    OR  WORD PTR ES:[BP+DI+4556], 20
    OR  WORD PTR [BP+DI+4], 2456
    M2  CS
    OR  BYTE PTR DS:[BP+SI], 011B
    OR  BYTE PTR [BP+DI], 20
    JNA LB1    
    POP [BX+DI+0]
COD ENDS 

END