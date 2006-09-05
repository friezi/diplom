#! /usr/bin/python

import linescanner
import os, sys
import os.path
from optparse import OptionParser

gnuplotfiles = 'gnuplotfiles'

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option('--dir',action='store',type='string',dest='dir')
    (options,args) = parser.parse_args()

    if options.dir == None:
        print "Please give direcory in '--dir='" 
        error = True
        
    if error == True:
        sys.exit(1)
        
    return (options,args)

""" main """

(options,args) = parsecmdl()

path = options.dir

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