import config


buf = ''


def tobuf(val):
	global buf
	buf = val 


def semantic_func(node):
	rule = node.value
	if rule in [1, 12, 13, 14]:
		semantic_func(node.children[0])
	elif rule == 2:
		semantic_func(node.children[1])
		local_buf = buf
		print(local_buf + ' SEGMENT')
		semantic_func(node.children[3])
		print(local_buf + ' ENDS')
	elif rule == 3:
		semantic_func(node.children[0])
		semantic_func(node.children[2])
	elif rule == 4:
		if node.children[0].value == 0:
			print('\n    nop')
		else:
			semantic_func(node.children[1])
	elif rule == 5 or rule == 8:
		if node.children[0].value == 0:
			print('\n    nop')
		else:
			for n in node.children:
				semantic_func(n)
	elif rule == 6:
		str = '    '
		semantic_func(node.children[0])
		str += buf
		semantic_func(node.children[2])
		str = str + '  ' + buf + ' ?'
		print(str)
	elif rule == 7:
		if node.children[0].code == 405:
			tobuf('dw')
		else:
			tobuf('dd')
	elif rule == 9:
		pass
	elif rule == 10:
		pass
	elif rule == 11:
		pass
	elif rule == 15 or rule == 17:
		tobuf(node.children[0].value)


def semantic():
	semantic_func(config.parse_tree)	
