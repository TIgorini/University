import config
from scanner import Lexeme


lexeme = Lexeme()


class Node:
	def __init__(self, value, code=0, line=0, col=0):
		self.value = value
		self.code = code
		self.line = line
		self.col = col
		self.children = []

	def add_child(self, node):
		self.children.append(node)

	def beautiful_print(self, depth=0):
		if self.code == 0:
			print('{:{fill}>{depth}}{}'.format('', self.value, depth=depth, fill='.'))
		else:
			print('{:{fill}>{depth}}{}  {}'.format('', self.code, self.value, depth=depth, fill='.'))
		for child in self.children:
			child.beautiful_print(depth + 4)


def next_lexem():
	global lexeme
	try:
		lexeme = config.lexemes.pop(0)
	except:
		pass


def program(parent):
	if lexeme.code != 401:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'PROGRAM', lexeme.value))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<procedure-identifier>'); parent.add_child(node)
		next_lexem()
		if proc_identifier(node):
			return True
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
			return True
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			node = Node('<block>'); parent.add_child(node)
			next_lexem()
			if block(node):
				return True
			if lexeme.code != 46:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, '.'))
				return True
			else:
				parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))


def block(parent):
	node = Node('<variable-declaration>'); parent.add_child(node)
	if var_declaration(node):
		return True
	if lexeme.code != 402:
		config.err_stack.append(config.errors['syntax']['syntax_err']\
			.format(lexeme.line, lexeme.col))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<statement-list>'); parent.add_child(node)
		next_lexem()
		if statements_list(node):
			return True
		if lexeme.code != 403:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'END'))
			return True
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()


def var_declaration(parent):
	if lexeme.code == 404:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<declaration-list>'); parent.add_child(node)
		next_lexem()
		if declaration_list(node):
			return True
	else:
		node = Node('<empty>'); parent.add_child(node)


def declaration_list(parent):
	empty = True
	while lexeme.code > 1000:
		empty = True
		node = Node('<declaration>'); parent.add_child(node)
		if declaration(node):
			return True
	if empty:
		node = Node('<empty>'); parent.add_child(node)


def declaration(parent):
	parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
	next_lexem()
	if lexeme.code != 58:
		config.err_stack.append(config.errors['syntax']['delim_expected']\
			.format(lexeme.line, lexeme.col, ':'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<attribute>'); parent.add_child(node)
		next_lexem()
		if attribute(node):
			return True
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
			return True
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()


def attribute(parent):
	if lexeme.code != 405 and lexeme.code != 406:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'type name'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def statements_list(parent):
	empty = True
	while lexeme.code == 407:
		empty = True
		node = Node('<statement>'); parent.add_child(node)
		if statement(node):
			return True
	if empty:
		node = Node('<empty>'); parent.add_child(node)


def statement(parent):
	parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
	node = Node('<condition-expression>'); parent.add_child(node)
	next_lexem()
	if cond_expression(node):
		return True
	if lexeme.code != 408:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'DO'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<statement-list>'); parent.add_child(node)
		next_lexem()
		if statements_list(node):
			return True
		if lexeme.code != 409:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'ENDWHILE'))
			return True
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()
			if lexeme.code != 59:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, ';'))
				return True
			else:
				parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
				next_lexem();


def cond_expression(parent):
	node = Node('<expression>'); parent.add_child(node)
	if expression(node): 
		return True
	node = Node('<comparison-operator>'); parent.add_child(node)
	if comparison_operator(node):
		return True
	node = Node('<expression>'); parent.add_child(node)
	if expression(node):
		return True


def comparison_operator(parent):
	if lexeme.code not in [60, 61, 62, 301, 302, 303]:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'comparison operator'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def expression(parent):
	if lexeme.code > 1000:
		node = Node('<variable-identifier>'); parent.add_child(node)
		if var_identifier(node):
			return True
	else:
		node = Node('<unsigned-integer>'); parent.add_child(node)
		if unsigned_integer(node):
			return True


def var_identifier(parent):
	node = Node('<identifier>'); parent.add_child(node)
	if identifier(node):
		return True


def proc_identifier(parent):
	node = Node('<identifier>'); parent.add_child(node)
	if identifier(node):
		return True


def identifier(parent):
	if lexeme.code < 1001:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'identifier'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def unsigned_integer(parent):
	if lexeme.code < 501 or lexeme.code > 1000:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'expression'))
		return True
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def parse():
	config.parse_tree = Node('<signal-program>')
	node = Node('<program>'); config.parse_tree.add_child(node)
	next_lexem()
	program(node)
