program tt2;

var v1: float;
    v2: integer;

begin
    while v1 = 42 do
        while v2 >= v1 do
        	(*loop1*)
        endwhile;
    endwhile;

    while 12 < 34 do 
    	(*loop2*)
    endwhile;
end.
