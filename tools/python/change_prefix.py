from os import *
from os.path import *
from stat import *
from re import *
from optparse import OptionParser
import sys

tempfilename = 't.e.m.p.f.i.l.e'

def parseOptions():
	
	parser = OptionParser(usage="usage: %prog --dir=<directory> --oldprefix=<oldprefix> --newprefix=<newprefix>")
	
	parser.add_option("--dir",action="store",type="string",dest="dir",help="the directory to work in")
	parser.add_option("--oldprefix",action="store",type="string",dest="oldprefix",help="the prefix to be changed")
	parser.add_option("--newprefix",action="store",type="string",dest="newprefix",help="the substitute-prefix")
	parser.set_description('This script will change prefixes in all files in the given directory and '
							+ ' (if necessary) will also rename each prefixed-file')
	
	(options,args) = parser.parse_args()
	
	if options.dir == None or options.oldprefix == None or options.newprefix == None:
		print "invalid calling-syntax!"
		parser.print_help()
		sys.exit(1)
		
	return (options,args)

def diveAndModify(dir,level):
	""" Verzeichnis traversieren, Dateiinhalt aendern und evtl. umbenennen """

	# nur fuer Ausgabe		
	s = "|"
	for i in range(level):
		s = s + '-'
		
	chdir(dir)

	chmod(getcwd(),S_IWRITE)
	
	# aktuelles Verzeichnis durchforsten	
	for entry in listdir(getcwd()):
	
		print s + entry
		
		if isdir(entry):
			diveAndModify(entry,level+1)    # rekursiv
		elif isfile(entry):
			
			# Inhalt auf Muster untersuchen und aendern
			system("sed -e 's/" + oldprefix + "/" + newprefix + "/g' " + entry + " > " + tempfile)
			system("attrib -R " + entry)
			system("del " + entry)
			rename(tempfile,entry)

		# falls Filename prefix enthaelt: umbenennen			
		matchobject = pattern.match(entry)
		if  matchobject != None:
			postfix = matchobject.group(1)
			newname = newprefix + postfix
			rename(entry,newname)
			print 'renamed ' + entry + ' to ' + newname

	chdir('..')
  
""" main: """

oldprefix = ''
newprefix = ''
tempfile = tempfilename

(options,args) = parseOptions()

basedirectory = options.dir
oldprefix = options.oldprefix
newprefix = options.newprefix

pattern = compile(oldprefix + '(.*)')
  
diveAndModify(basedirectory,1)