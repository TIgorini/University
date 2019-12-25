(defun subset(set)
	(cond 
		((NULL set) ())
		((NULL (car set)) (subset (cdr set)))
		((listp (car set)) (cons (car set) (subset (cdr set))))
		(T (subset (cdr set)))
	)
)

(defvar x '(a (a b) c (a (b c)) () s))
(write-line "Original set:")
(write x) (terpri)
(write-line "Subsets in set:")
(write (subset x)) (terpri)
