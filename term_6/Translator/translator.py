#!/usr/bin/python3

import sys
import config
import re
from scanner import scan
from parser import parse


def lexemes_print(lexemes):
    print(' line  col   code  value')
    print('-----------------------------')
    for lexeme in config.lexemes:
        print('   {:<4}{:<6}{:<6}{}'\
            .format(lexeme.line, lexeme.col, lexeme.code, lexeme.value))


def err_print(fname, err):
    try:
        f = open(fname, 'r')
    except OSError:
        print("Couldn`t open file '{}'".format(fname))
    else:
        pattern = re.compile('[0-9]+')
        numbers = pattern.findall(err) 
        line = int(numbers[0])
        col = int(numbers[1])
        for i in range(line):
            line = f.readline()
        f.close()
        print('\n   ' + line, end='')
        print('   {:>{col}}'.format('^', col=col))
        print(err)


if len(sys.argv) > 1:
    fname = sys.argv[1]
else:
    fname = 'tests/tt1.sig'

scan(fname)
parse()
config.parse_tree.beautiful_print()
for err in config.err_stack:
    err_print(fname, err)

print('\nConstants: {}'.format(config.consts))
print('Identifires: {}'.format(config.identifires))
