#! /usr/bin/python

import linescanner
import os, sys
import os.path

gnuplotfiles = 'gnuplotfiles'

if len( sys.argv ) != 2:
    print "please give dirname"
    sys.exit()

path = sys.argv[1]

olddir = os.getcwd()
os.chdir( path )

try:
    
    file = open( gnuplotfiles,'r' )

    print "executing .gnuplot -files from definitionfile " + path + "/" + gnuplotfiles 

    for gnuplotfile in linescanner.linetokens( file ):
        os.system( 'gnuplot ' + gnuplotfile + ' > /dev/null' )
        
    file.close()
    
    os.chdir( olddir )
    
except IOError:
    print 'no file "' + gnuplotfile + '" found'