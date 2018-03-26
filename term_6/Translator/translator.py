#!/usr/bin/python3

import sys
import config
from scanner import scan


if len(sys.argv) > 1:
	scan(sys.argv[1])
else:
	scan('true_test1.sig')

print(' line  col   code  value')
print('-----------------------------')
for lexeme in config.lexemes:
	print('   {:<4}{:<6}{:<6}{}'.format(lexeme.line, lexeme.col, lexeme.code, lexeme.value))

for err in config.err_stack:
	print(err)

print('\nConstants: {}'.format(config.consts))
print('Identifires: {}'.format(config.identifires))