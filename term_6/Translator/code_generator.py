import config
import os


class Buffer:
	def __init__(self):
		self.buf = '' 
	
	def set(self, val):
		self.buf = val

	def get(self):
		return self.buf 


buf = Buffer()
errors_occurred = False
label_counter = 0
program_name_code = 0
init_identifiers = []
reverse_comp_op = {
	60:  'JGE',
	61:  'JNE',
	62:  'JLE',
	301: 'JG',
	302: 'JL',
	303: 'JE',  
}


def cg_func(file, node):
	global errors_occurred
	rule = node.value
	if rule in [1, 12, 13, 14]:
		cg_func(file, node.children[0])

	# <program> --> PROGRAM <procedure-identifier>; <block>.
	elif rule == 2:
		cg_func(file, node.children[1])
		ident = buf.get()
		global program_name_code
		program_name_code = ident.code
		file.write(ident.value + ' SEGMENT\n')
		cg_func(file, node.children[3])
		file.write(ident.value + ' ENDS\n')

	# <block> --> <variable-declarations> BEGIN <statements-list> END
	elif rule == 3:
		cg_func(file, node.children[0])
		cg_func(file, node.children[2])

	# <variable-declarations> --> VAR <declaration-list> | <empty>
	elif rule == 4:
		if node.children[0].value == 0:
			file.write('    NOP\n')
		else:
			cg_func(file, node.children[1])

	# <declaration-list> --> <declaration> <declaration-list> | <empty>
	# <statements-list> --> <statement> <statements-list> | <empty>
	elif rule == 5 or rule == 8:
		if node.children[0].value == 0:
			file.write('    NOP\n')
		else:
			for n in node.children:
				cg_func(file, n)

	# <declaration> --> <variable-identifier>: <attribute>;
	elif rule == 6:
		cg_func(file, node.children[0])
		ident = buf.get()
		if ident.code in init_identifiers or ident.code == program_name_code:
			config.err_stack.append(config.errors['cg']['ident_repeated']\
				.format(ident.line, ident.col, ident.value))
			errors_occurred = True
			return
		init_identifiers.append(ident.code)
		local_buf = '    ' + ident.value
		cg_func(file, node.children[2])
		local_buf = local_buf + '  ' + buf.get()
		file.write(local_buf)

	# <attribute> --> INTEGER | FLOAT 
	elif rule == 7:
		if node.children[0].code == 405:
			buf.set('DW ?\n')
		else:
			buf.set('Dw ?, ?\n')

	# <statement> --> WHILE <conditional-expression> DO <statements-list> ENDWHILE;
	elif rule == 9:
		global label_counter
		startl = '?loop' + str(label_counter)
		endl = '?loop' + str(label_counter) + '_end'
		label_counter += 1

		file.write('\n' + startl + ':\n')
		buf.set(endl)
		cg_func(file, node.children[1])
		cg_func(file, node.children[3])
		file.write('    JMP\t' + startl + '\n')
		file.write(endl + ':\n')

	# <conditional-expression> --> <expression> <comprasion-operator> <expression>
	elif rule == 10:
		label = buf.get()
		cg_func(file, node.children[0])
		local_buf = buf.get()
		if local_buf.code not in init_identifiers:
			if local_buf.value not in config.consts:
				config.err_stack.append(config.errors['cg']['not_defined']\
					.format(local_buf.line, local_buf.col, local_buf.value))
				errors_occurred = True
		file.write('    MOV\tAX, ' + local_buf.value + '\n')

		cg_func(file, node.children[2])
		local_buf = buf.get()
		if local_buf.code not in init_identifiers:
			if local_buf.value not in config.consts:
				config.err_stack.append(config.errors['cg']['not_defined']\
					.format(local_buf.line, local_buf.col, local_buf.value))
				errors_occurred = True
		file.write('    MOV\tBX, ' + local_buf.value + '\n')
		file.write('    CMP\tAX, BX\n')

		buf.set(label)
		cg_func(file, node.children[1])

	# <comprasion-operator> --> < | > ... <=
	elif rule == 11:
		asm_compare = reverse_comp_op[node.children[0].code]
		file.write('    ' + asm_compare + '\t' + buf.get() + '\n')

	# <identifier> --> identifier
	# <unsigned-integer> --> constant
	elif rule == 15 or rule == 17:
		buf.set(node.children[0])


def cg(out_fname):
	with open(out_fname, 'w') as file:
		cg_func(file, config.parse_tree)
	if errors_occurred:
		os.remove(out_fname)
		return False
	return True

