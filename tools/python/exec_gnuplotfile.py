#! /usr/bin/python

import fileinput
import linescanner
import os, sys
import os.path

gnuplotfiles = 'gnuplotfiles'

if len(sys.argv) != 2:
    print "please give dirname"
    sys.exit()

path = os.path.dirname(sys.argv[1])

olddir = os.getcwd()
os.chdir(path)

input = fileinput.input(gnuplotfiles)

print "executing .gnuplot -files from definitionfile " + path + "/" + gnuplotfiles 

for gnuplotfile in linescanner.linetokens(input):
    os.system('gnuplot ' + gnuplotfile + ' > /dev/null')
    
os.chdir(olddir)