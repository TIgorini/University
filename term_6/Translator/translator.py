#!/usr/bin/python3

import sys
import config
import re
from scanner import scan
from parser import parse
from code_generator import cg


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


listing = False
if len(sys.argv) < 2:
    fname = 'tests/tt1.sig'
elif len(sys.argv) == 2:
    arg = sys.argv[1]
    if arg == '-l':
        listing = True
        fname = 'tests/tt1.sig'
    else:
        fname = arg
elif len(sys.argv) == 3:
    if sys.argv[1] == '-l' or sys.argv[1] == '--listing':
        fname = sys.argv[2]
        listing = True
    elif sys.argv[2] == '-l' or sys.argv[2] == '--listing':
        fname = sys.argv[1]
        listing = True
    else:
        print(' Invalid arguments')
else:
    print(' Wrong arguments number')
    

scan(fname)
if not parse():
    cg()

#config.parse_tree.beautiful_print()
for err in config.err_stack:
    err_print(fname, err)

if listing:
    print('\nListing saved')
    print('Constants: {}'.format(config.consts))
    print('Identifires: {}'.format(config.identifires))
