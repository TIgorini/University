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
identifires = {}

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
}

err_stack = []
