#!/usr/bin/python3

import sys
import config
from scanner import scan
from parser import parse


if len(sys.argv) > 1:
	scan(sys.argv[1])
else:
	scan('tests/true_test1.sig')

# print(' line  col   code  value')
# print('-----------------------------')
# for lexeme in config.lexemes:
# 	print('   {:<4}{:<6}{:<6}{}'\
#		.format(lexeme.line, lexeme.col, lexeme.code, lexeme.value))

parse()

config.parse_tree.beautiful_print()
for err in config.err_stack:
	print(err)

print('\nConstants: {}'.format(config.consts))
print('Identifires: {}'.format(config.identifires))
