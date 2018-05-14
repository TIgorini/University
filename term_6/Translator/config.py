# Table of lexemes
lexemes = []

# Parse tree
parse_tree = None

# Table of delimiters
delims = {
    '.': 46, 
    ':': 58, 
    ';': 59, 
    '<': 60, 
    '=': 61, 
    '>': 62,
}

# Table of multi-character delimiters
mult_delims = {
    '<=': 301, 
    '>=': 302, 
    '<>': 303,
}

# Table of keywords
keywords = {
    'PROGRAM': 401,
    'BEGIN': 402,
    'END': 403,
    'VAR': 404,
    'INTEGER': 405,
    'FLOAT': 406,
    'WHILE': 407,
    'DO': 408,
    'ENDWHILE': 409,
}

# Table of constants
consts = {}

# Table of identifires
identifiers = {}

errors = {
    'lexical': {
        'invalid_ident': "Lexer: Error (line: {}, column: {}): invalid identifier '{}'",
        'invalid_char': "Lexer: Error (line: {}, column: {}): invalid character '{}'",
        'unclosed_comment': "Lexer: Error (line: {}, column: {}): *) expected, but end of file found",
    },
    'syntax': {
        'keyword_expected': "Parser: Error (line: {}, column: {}): '{}' expected",
        'delim_expected': "Parser: Error (line: {}, column: {}): '{}' expected",
        'something_expected': "Parser: Error (line: {}, column: {}): {} expected",
        'syntax_err': "Parser: Error (line: {}, column: {}): invalid syntax",
    },
    'cg': {
        'ident_repeated': "Code Generator: Error (line: {}, column: {}): '{}' is already defined in this scope",
        'not_defined': "Code Generator: Error (line: {}, column: {}): variable '{}' is not defined",
    },
}

err_stack = []

grammar = {
    1:  '<signal-program>',
    2:  '<program>',
    3:  '<block>',
    4:  '<variable-declarations>',
    5:  '<declaration-list>',
    6:  '<declaration>',
    7:  '<attribute>',
    8:  '<statements-list>',
    9:  '<statement>',
    10: '<conditional-expression>',
    11: '<compresion-operator>',
    12: '<expression>',
    13: '<variable-identifier>',
    14: '<procedure-identifier>',
    15: '<identifier>',
    17: '<unsigned-integer>',
    0:  '<empty>'
}
