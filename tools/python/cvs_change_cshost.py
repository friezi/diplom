#! /usr/bin/python

from os.path import isdir,exists
import os
from optparse import OptionParser

CVSDIR = 'CVS'
CVSROOT = 'Root'
PREFIX = 'friezi\@'
SUFFIX = '.cs.tu-berlin.de'
tempfile = 't.e.m.p.f.i.l.e'

def escapeDotsForSed(string):
    
    new = ""
    
    for i in range(len(string)):
        if string[i] == '.':
            new = new + '\\'
        new = new + string[i]
    
    return new

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option('--host',action='store',type='string',dest='host')
    parser.add_option('--dir',action='store',type='string',dest='dir')
    (options,args) = parser.parse_args()

    if options.dir == None:
        print "Please give hostname in '--host='" 
        error = True

    if options.dir == None:
        print "Please give direcory in '--dir='" 
        error = True
        
    if error == True:
        sys.exit(1)
        
    return (options,args)

def traverse(dir,host,is_cvs_dir):

    os.chdir(dir)
    
    if is_cvs_dir == 'true':
        
        print 'changing ' + os.getcwd() + '/' + CVSROOT
        
        os.system("sed '-e s/^[ ]*\(" + PREFIX + "\)\(.*\)\(" + escapeDotsForSed(SUFFIX) +"\)\(.*\)$/\\1" + host + "\\3\\4/g' " + CVSROOT + " > "
                  + tempfile)
        os.rename(tempfile, CVSROOT)
        
    else:
    
        dirs = os.listdir('./')
        
        for entry in dirs:
    
            if isdir(entry):
                if entry == CVSDIR:
                    traverse(entry,host,'true')
                else:
                    traverse(entry,host,'false')
        
    os.chdir('..')
        

""" main """

(options,args) = parsecmdl()

dir = options.dir
host = options.host

traverse(dir,host,'false')