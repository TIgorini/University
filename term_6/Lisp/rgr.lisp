(defun f1(i)
	(if (eql i 1) 1
		(- i (sin (+ (f1 (- i 1)) i)))
	)
)

(defun f2(i)
	(if (eql i 10) 1
		(* 4 (cos (* (f2 (- i 1)) (log i))))
	)
)

(defun f(i)
	(cond
		((not (numberp i)) nil)
		((/= (mod i 1) 0) nil)
		((< i 1) nil)
		((< i 10) (f1 i))
		((< i 21) (f2 i))
		(T nil)
	)
)

;tests
(print (f `(a b c 1)))
(print (f 5.5))
(print (f 1))
(print (f 3))
;(print (- 3 (sin (+ (- 2 (sin (+ 1 2))) 3))))	
(print (f 10))
(print (f 12))
;(print (* 4 (cos (* 4 (cos (log 11)) (log 12)))))
(print (f 21))
(terpri)