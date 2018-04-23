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
	lexeme = config.lexemes.pop(0)


def program(parent):
	if lexeme.code != 401:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'PROGRAM', lexeme.value))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<procedure-identifier>'); parent.add_child(node)
		next_lexem()
		proc_identifier(node)
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			node = Node('<block>'); parent.add_child(node)
			next_lexem()
			block(node)
			if lexeme.code != 46:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, '.'))
			else:
				parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))


def block(parent):
	node = Node('<variable-declaration>'); parent.add_child(node)
	var_declaration(node)
	if lexeme.code != 402:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'BEGIN', lexeme.value))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<statement-list>'); parent.add_child(node)
		next_lexem()
		statements_list(node)
		if lexeme.code != 403:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'END', lexeme.value))
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()


def var_declaration(parent):
	if lexeme.code == 404:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<declaration-list>'); parent.add_child(node)
		next_lexem()
		declaration_list(node)
	else:
		node = Node('<empty>'); parent.add_child(node)


def declaration_list(parent):
	empty = True
	while lexeme.code > 1000:
		empty = False
		node = Node('<declaration>'); parent.add_child(node)
		declaration(node)
	if empty:
		node = Node('<empty>'); parent.add_child(node)


def declaration(parent):
	parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
	next_lexem()
	if lexeme.code != 58:
		config.err_stack.append(config.errors['syntax']['delim_expected']\
			.format(lexeme.line, lexeme.col, ':'))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<attribute>'); parent.add_child(node)
		next_lexem()
		attribute(node)
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()


def attribute(parent):
	if lexeme.code != 405 and lexeme.code != 406:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'type'))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def statements_list(parent):
	empty = True
	while lexeme.code == 407:
		empty = False
		node = Node('<statement>'); parent.add_child(node)
		statement(node)
	if empty:
		node = Node('<empty>'); parent.add_child(node)


def statement(parent):
	parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
	node = Node('<condition-expression>'); parent.add_child(node)
	next_lexem()
	cond_expression(node)
	if lexeme.code != 408:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'DO', lexeme.value))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		node = Node('<statement-list>'); parent.add_child(node)
		next_lexem()
		statements_list(node)
		if lexeme.code != 409:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'ENDWHILE', lexeme.value))
		else:
			parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
			next_lexem()
			if lexeme.code != 59:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, ';'))
			else:
				parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
				next_lexem();


def cond_expression(parent):
	node = Node('<expression>'); parent.add_child(node)
	expression(node)
	node = Node('<comparison-operator>'); parent.add_child(node)
	comparison_operator(node)
	node = Node('<expression>'); parent.add_child(node)
	expression(node)


def comparison_operator(parent):
	if lexeme.code not in [60, 61, 62, 301, 302, 303]:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'comparison operator'))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def expression(parent):
	if lexeme.code > 1000:
		node = Node('<variable-identifier>'); parent.add_child(node)
		var_identifier(node)
	else:
		node = Node('<unsigned-integer>'); parent.add_child(node)
		unsigned_integer(node)


def var_identifier(parent):
	node = Node('<identifier>'); parent.add_child(node)
	identifier(node)


def proc_identifier(parent):
	node = Node('<identifier>'); parent.add_child(node)
	identifier(node)


def identifier(parent):
	if lexeme.code < 1001:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'identifier'))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def unsigned_integer(parent):
	if lexeme.code < 501 or lexeme.code > 1000:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'expression'))
	else:
		parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
		next_lexem()


def parse():
	config.parse_tree = Node('<signal-program>')
	node = Node('<program>'); config.parse_tree.add_child(node)
	next_lexem()
	program(node)
