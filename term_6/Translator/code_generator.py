import config


class Buffer:
	def __init__(self):
		self.buf = '' 
	
	def set(self, val):
		self.buf = val

	def get(self):
		return self.buf 


buf = Buffer()		
label_counter = 0
comp_op = {
	60:  'JL',
	61:  'JE',
	62:  'JG',
	301: 'JLE',
	302: 'JGE',
	303: 'JNE',	
}


def cg_func(node):
	rule = node.value
	if rule in [1, 12, 13, 14]:
		cg_func(node.children[0])

	# <program> --> PROGRAM <procedure-identifier>; <block>.
	elif rule == 2:
		cg_func(node.children[1])
		local_buf = buf.get()
		print(local_buf + ' SEGMENT')
		cg_func(node.children[3])
		print(local_buf + ' ENDS')

	# <block> --> <variable-declarations> BEGIN <statements-list> END
	elif rule == 3:
		cg_func(node.children[0])
		cg_func(node.children[2])

	# <variable-declarations> --> VAR <declaration-list> | <empty>
	elif rule == 4:
		if node.children[0].value == 0:
			print('    NOP')
		else:
			cg_func(node.children[1])

	# <declaration-list> --> <declaration> <declaration-list> | <empty>
	# <statements-list> --> <statement> <statements-list> | <empty>
	elif rule == 5 or rule == 8:
		if node.children[0].value == 0:
			print('    NOP')
		else:
			for n in node.children:
				cg_func(n)
		print('')

	# <declaration> --> <variable-identifier>: <attribute>;
	elif rule == 6:
		cg_func(node.children[0])
		local_buf = '    ' + buf.get()
		cg_func(node.children[2])
		local_buf = local_buf + '  ' + buf.get() + ' ?'
		print(local_buf)

	# <attribute> --> INTEGER | FLOAT 
	elif rule == 7:
		if node.children[0].code == 405:
			buf.set('DW')
		else:
			buf.set('DD')

	# <statement> --> WHILE <conditional-expression> DO <statements-list> ENDWHILE;
	elif rule == 9:
		global label_counter
		startl = '?S' + str(label_counter)
		endl = '?E' + str(label_counter)
		label_counter += 1

		buf.set(endl)
		cg_func(node.children[1])
		print(startl + ':')
		cg_func(node.children[3])
		buf.set(startl)
		cg_func(node.children[1])
		print(endl + ':')

	# <conditional-expression> --> <expression> <comprasion-operator> <expression>
	elif rule == 10:
		local_buf = buf.get()
		cg_func(node.children[0])
		print('    MOV\tEAX, ' + buf.get())
		cg_func(node.children[2])
		print('    MOV\tEBX, ' + buf.get())
		print('    CMP\tEAX, EBX')
		buf.set(local_buf)
		cg_func(node.children[1])

	# <comprasion-operator> --> < | > ... <=
	elif rule == 11:
		asm_compare = comp_op[node.children[0].code]
		print('    ' + asm_compare + '\t' + buf.get())

	# <identifier> --> identifier
	# <unsigned-integer> --> constant
	elif rule == 15 or rule == 17:
		buf.set(node.children[0].value)


def cg():
	cg_func(config.parse_tree)	
