import config


# Array of ASCII character attributes
attributes = []


class Lexeme:
    def __init__(self, code=0, line=0, col=0, value=''):
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


    def read(self, file):
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


def whitespace(symbol, file):
    while symbol.val and symbol.attr == 0:
        symbol.read(file)
    return {'skip':True}

def ident(symbol, file):
    line = symbol.line
    col = symbol.col
    buf = ''
    while symbol.val and (symbol.attr == 1 or symbol.attr == 2):
        buf += symbol.val
        symbol.read(file)

    if buf in config.keywords:
        code = config.keywords[buf]
    elif buf in config.identifiers:
        code = config.identifiers[buf]
    elif config.identifiers:
        code = max(config.identifiers.values()) + 1
        config.identifiers[buf] = code
    else:
        code = 1001
        config.identifiers[buf] = code

    return {'code':code, 'line':line, 'col':col, 'val':buf, 'skip':False}

def number(symbol, file):
    err_idn = False
    line = symbol.line
    col = symbol.col
    buf = ''
    skiping = False
    while symbol.val and (symbol.attr == 1 or symbol.attr == 2):
        if (symbol.attr != 2):
            err_idn = True 
        buf += symbol.val
        symbol.read(file)
    if err_idn:
        config.err_stack.append(config.errors['lexical']['invalid_ident'].format(line, col, buf))
        return {'skip':True}
    elif buf in config.consts:
        code = config.consts[buf]
    elif config.consts:
        code = max(config.consts.values()) + 1
        config.consts[buf] = code
    else:
        code = 501
        config.consts[buf] = code

    return {'code':code, 'line':line, 'col':col, 'val':buf, 'skip':skiping}

def mult_delim(symbol, file):
    line = symbol.line
    col = symbol.col
    buf = symbol.val
    symbol.read(file)
    buf += symbol.val
    if buf in config.mult_delims:
        code = config.mult_delims[buf]
        symbol.read(file)
    else:
        buf = buf[0]
        code = ord(buf)

    return {'code':code, 'line':line, 'col':col, 'val':buf, 'skip':False}

def delim(symbol, file):
    line = symbol.line
    col = symbol.col
    buf = symbol.val
    code = ord(buf)
    symbol.read(file)
    return {'code':code, 'line':line, 'col':col, 'val':buf, 'skip':False}

def comment(symbol, file):
    line = symbol.line
    col = symbol.col
    symbol.read(file)
    skiping = True
    if symbol.val == '' or symbol.val != '*':
        config.err_stack.append(config.errors['lexical']['invalid_char'].format(line, col, symbol.val))
    else:
        symbol.read(file)
        if symbol.val == '':
            config.err_stack.append(config.errors['lexical']['unclosed_comment'].format(line, col))
        else:
            while True:
                while symbol.val and symbol.val != '*':
                    symbol.read(file)
                if symbol.val == '':
                    config.err_stack.append(config.errors['lexical']['unclosed_comment'].format(line, col))
                    break
                else:
                    symbol.read(file)
                if symbol.val == ')':
                    symbol.read(file)
                    break
    return {'skip':True}

def illegal(symbol, file):
    config.err_stack.append(config.errors['lexical']['invalid_char'].format(symbol.line, symbol.col, symbol.val))   
    symbol.read(file)
    return {'skip':True}

lexeme_type = {
    0: whitespace,
    1: ident,
    2: number,
    3: mult_delim,
    4: delim,
    5: comment,
    6: illegal,
} 


def scan(fname):
    fill_attributes(attributes)

    try:
        f = open(fname, 'r')
    except OSError:
        print("Couldn`t open file '{}'".format(fname))
    else:
        symbol = Symbol()
        symbol.read(f)
        while symbol.val:
            lex = lexeme_type[symbol.attr](symbol, f)
            if not lex['skip']:
                config.lexemes.append(Lexeme(lex['code'], lex['line'], lex['col'], lex['val'])) 
        f.close()
