#!/usr/bin/python3

import sys
import config
from scanner import scan


if len(sys.argv) > 1:
	scan(sys.argv[1])
else:
	scan('true_test1.sig')


for lexeme in config.lexemes:
	print('  {:<4}{:<6}{:<6}{}'.format(lexeme.line, lexeme.col, lexeme.code, lexeme.value))

print('\nDelimiters: {}'.format(config.delims))
print('\nMult-delimiters: {}'.format(config.mult_delims))
print('\nKeywords: {}'.format(config.keywords))
print('\nConstants: {}'.format(config.consts))
print('\nIdentifires: {}'.format(config.identifires))