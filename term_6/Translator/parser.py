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
        node.parent = self

    def beautiful_print(self, depth=0):
        if self.code == 0:
            print('{:{fill}>{depth}}{}'.format('', config.grammar[self.value], depth=depth, fill='.'))
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
        return True
    else:
        parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
        node = Node(14); parent.add_child(node)
        next_lexem()
        if proc_identifier(node):
            return True
        if lexeme.code != 59:
            config.err_stack.append(config.errors['syntax']['delim_expected']\
                .format(lexeme.line, lexeme.col, ';'))
            return True
        else:
            parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
            node = Node(3); parent.add_child(node)
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
    node = Node(4); parent.add_child(node)
    if var_declaration(node):
        return True
    if lexeme.code != 402:
        config.err_stack.append(config.errors['syntax']['syntax_err']\
            .format(lexeme.line, lexeme.col))
        return True
    else:
        parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
        node = Node(8); parent.add_child(node)
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
        node = Node(5); parent.add_child(node)
        next_lexem()
        if declaration_list(node):
            return True
    else:
        node = Node(0); parent.add_child(node)


def declaration_list(parent):
    empty = True
    while lexeme.code > 1000:
        empty = False
        node = Node(6); parent.add_child(node)
        if declaration(node):
            return True
    if empty:
        node = Node(0); parent.add_child(node)


def declaration(parent):
    node = Node(13); parent.add_child(node)
    if var_identifier(node):
        return True
    if lexeme.code != 58:
        config.err_stack.append(config.errors['syntax']['delim_expected']\
            .format(lexeme.line, lexeme.col, ':'))
        return True
    else:
        parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
        node = Node(7); parent.add_child(node)
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
            .format(lexeme.line, lexeme.col, 'name of type'))
        return True
    else:
        parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
        next_lexem()


def statements_list(parent):
    empty = True
    while lexeme.code == 407:
        empty = True
        node = Node(9); parent.add_child(node)
        if statement(node):
            return True
    if empty:
        node = Node(0); parent.add_child(node)


def statement(parent):
    parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
    node = Node(10); parent.add_child(node)
    next_lexem()
    if cond_expression(node):
        return True
    if lexeme.code != 408:
        config.err_stack.append(config.errors['syntax']['keyword_expected']\
            .format(lexeme.line, lexeme.col, 'DO'))
        return True
    else:
        parent.add_child(Node(lexeme.value, lexeme.code, lexeme.line, lexeme.col))
        node = Node(8); parent.add_child(node)
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
    node = Node(12); parent.add_child(node)
    if expression(node): 
        return True
    node = Node(11); parent.add_child(node)
    if comparison_operator(node):
        return True
    node = Node(12); parent.add_child(node)
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
        node = Node(13); parent.add_child(node)
        if var_identifier(node):
            return True
    else:
        node = Node(17); parent.add_child(node)
        if unsigned_integer(node):
            return True


def var_identifier(parent):
    node = Node(15); parent.add_child(node)
    if identifier(node):
        return True


def proc_identifier(parent):
    node = Node(15); parent.add_child(node)
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


# returning True if syntax error found
def parse():
    config.parse_tree = Node(1)
    node = Node(2); config.parse_tree.add_child(node)
    next_lexem()
    if program(node):
        return True 
