import config
from scanner import Lexeme


lexeme = Lexeme()


def next_lexem():
	global lexeme 
	lexeme = config.lexemes.pop(0) 


def program():
	if lexeme.code != 401:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'PROGRAM', lexeme.value))
	else:
		next_lexem()
		identifier()
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
		else:
			next_lexem()
			block()
			if lexeme.code != 46:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, '.'))


def block():
	var_declaration()
	if lexeme.code != 402:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'BEGIN', lexeme.value))
	else:
		next_lexem()
		statements_list()
		if lexeme.code != 403:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'END', lexeme.value))
		else:
			next_lexem()


def var_declaration():
	if lexeme.code == 404:
		next_lexem()
		declaration_list()


def declaration_list():
	while lexeme.code > 1000:
		declaration()


def declaration():
	identifier()
	if lexeme.code != 58:
		config.err_stack.append(config.errors['syntax']['delim_expected']\
			.format(lexeme.line, lexeme.col, ':'))
	else:
		next_lexem()
		attribute()
		if lexeme.code != 59:
			config.err_stack.append(config.errors['syntax']['delim_expected']\
				.format(lexeme.line, lexeme.col, ';'))
		else:
			next_lexem()


def attribute():
	if lexeme.code != 405 and lexeme.code != 406:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'type'))
	else:
		next_lexem()


def statements_list():
	while lexeme.code == 407:
		statement()


def statement():
	#WHILE to tree
	next_lexem()
	cond_expression()
	if lexeme.code != 408:
		config.err_stack.append(config.errors['syntax']['keyword_expected']\
			.format(lexeme.line, lexeme.col, 'DO', lexeme.value))
	else:
		next_lexem()
		statements_list()
		if lexeme.code != 409:
			config.err_stack.append(config.errors['syntax']['keyword_expected']\
				.format(lexeme.line, lexeme.col, 'ENDWHILE', lexeme.value))
		else:
			next_lexem()
			if lexeme.code != 59:
				config.err_stack.append(config.errors['syntax']['delim_expected']\
					.format(lexeme.line, lexeme.col, ';'))
			else:
				next_lexem();


def cond_expression():
	expression()
	comparison_operator()
	expression()


def comparison_operator():
	if lexeme.code not in [60, 61, 62, 301, 302, 303]:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'comparison operator'))
	else:
		next_lexem()


def expression():
	if lexeme.code > 1000:
		identifier()
	else:
		unsigned_integer()


def identifier():
	if lexeme.code < 1001:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'identifier'))
	else:
		next_lexem()


def unsigned_integer():
	if lexeme.code < 501 or lexeme.code > 1000:
		config.err_stack.append(config.errors['syntax']['something_expected']\
			.format(lexeme.line, lexeme.col, 'expression'))
	else:
		next_lexem()


def parse():
	next_lexem()
	program()
