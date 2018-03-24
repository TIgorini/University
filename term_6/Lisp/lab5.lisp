(defun f8 (lst n)
	(if (eql n 0) 
		lst
		(f8 (cons (car (reverse lst)) (reverse (cdr (reverse lst))) ) (- n 1))
	) 
)

(print (f8 `(a b c d e f g h) 2))
(terpri)