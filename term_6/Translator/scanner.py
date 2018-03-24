import config


# Array of ASCII character attributes
attributes = []


class Lexeme:
	def __init__(self, code, line, col, value):
		self.value = value
		self.code = code
		self.line = line
		self.col = col


class Symbol:
	def __init__(self, val='', attr=0):
		self.val = val;
		self.attr = attr;
		self.line = 1
		self.col = 0

	def get(self, file):
		self.val = file.read(1).upper()
		if self.val:
			self.attr = attributes[ord(self.val)]
			self.col += 1
			if self.val == '\n':
				self.col = 0
				self.line += 1
		return self.val	


def fill_attributes(attr):
	# 6 - illegals characters
	attr += [6 for x in range(128)]
	# 0 - whitespace
	for i in range(9, 14):
		attr[i] = 0
	attr[32] = 0
	# 1 - letters
	for i in range(65, 91):
		attr[i] = 1
	# 2 - numbers
	for i in range(48, 58):
		attr[i] = 2
	# 3 - multi-character delimiters
	attr[60] = 3
	attr[62] = 3
	# 4 - delimiters
	attr[46] = 4
	attr[58] = 4
	attr[59] = 4
	attr[61] = 4
	# 5 - character '('
	attr[40] = 5

def witespace(symbol):
	

def scan(fname):
	fill_attributes(attributes)
	
	try:
		f = open(fname, 'r')
	except OSError:
		print('Couldn`t open this file')
	else:
		symbol = Symbol()
		symbol.get(f)
		while symbol.val:
			lex_code = 0
			lex_line = symbol.line
			lex_col = symbol.col
			buf = ''
			skiping = False
			
			# whitespace
			if symbol.attr == 0:
				while symbol.val and symbol.attr == 0:
					symbol.get(f)
				skiping = True
			# identifiers
			elif symbol.attr == 1:
				while symbol.val and (symbol.attr == 1 or symbol.attr == 2):
					buf += symbol.val
					symbol.get(f)

				if buf in config.keywords:
					lex_code = config.keywords[buf]
				elif buf in config.identifires:
					lex_code = config.identifires[buf]
				elif config.identifires:
					lex_code = max(config.identifires.values()) + 1
					config.identifires[buf] = lex_code
				else:
					lex_code = 1001
					config.identifires[buf] = lex_code
			# numbers
			elif symbol.attr == 2:
				err_idn = False
				while symbol.val and symbol.attr != 0:
					if (symbol.attr != 2):
						err_idn = True 
					buf += symbol.val
					symbol.get(f)

				if err_idn:
					skiping = True
					print("Lexer: Error (line: {}, column: {}): invalid identifier '{}'".format(lex_line, lex_col, buf))
				elif buf in config.consts:
					lex_code = config.consts[buf]
				elif config.consts:
					lex_code = max(config.consts.values()) + 1
					config.consts[buf] = lex_code
				else:
					lex_code = 501
					config.consts[buf] = lex_code
			# multi-character delimiters
			elif symbol.attr == 3:
				buf += symbol.val
				symbol.get(f)
				buf += symbol.val
				if buf in config.mult_delims:
					lex_code = config.mult_delims[buf]
					symbol.get(f)
				else:
					buf = buf[0]
					lex_code = ord(buf)
			# delimiters
			elif symbol.attr == 4:
				buf = symbol.val
				lex_code = ord(buf)
				symbol.get(f)
			# comment
			elif symbol.attr == 5:
				symbol.get(f)
				skiping = True
				if symbol.val == '' or symbol.val != '*':
					print("Lexer: Error (line: {}, column: {}): invalid character '{}'".format(lex_line, lex_col, symbol.val))
				else:
					symbol.get(f)
					if symbol.val == '':
						print("Lexer: Error (line: {}, column: {}): *) expected, but end of file found".format(lex_line, lex_col))
					else:
						symbol.get(f)
						while True:
							while symbol.val and symbol.val != '*':
								symbol.get(f)
							if symbol.val == '':
								print("Lexer: Error (line: {}, column: {}): *) expected, but end of file found".format(lex_line, lex_col))
								break
							else:
								symbol.get(f)
							if symbol.val == ')':
								symbol.get(f)
								break
			# illegal character
			else:
				print("Lexer: Error (line: {}, column: {}): invalid character '{}'".format(lex_line, lex_col, symbol.val))	
				symbol.get(f)
				skiping = True

			if not skiping:
				config.lexemes.append(Lexeme(lex_code, lex_line, lex_col, buf))	
		f.close()
