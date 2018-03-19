(defun list-changing(list) 
	(if (not (null list)) 
		(cons (cons (car list) (last list)) (list-changing (reverse (cdr (reverse (cdr list))))))
		nil
	)
)

(print (list-changing `(a b c d e f g)))
(terpri)