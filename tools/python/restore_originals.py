#! /usr/bin/python

from optparse import OptionParser
from re import compile, match
from dircache import listdir
import os
import sys

pattern = compile("^(.*)\.orig")

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
 
    ( options, args ) = parser.parse_args()
 
    if options.dir == None:
        print "Please give directory in '--dir='" 
        error = True
       
    if error == True:
        print "usage: " + os.path.basename( sys.argv[0] ) + " --dir=<directory>"
        sys.exit( 1 )
        
    return ( options, args )

""" main """

(options,args) = parsecmdl()
dir = options.dir

os.chdir(dir)

files = listdir(os.getcwd())

for file in files:
    match = pattern.match(file)
    if match != None:
        os.rename(file,match.group(1))
