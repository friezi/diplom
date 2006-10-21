# Author: Friedemann Zintel

from os import *
from os.path import *
from stat import *
from re import *
from optparse import OptionParser
import sys

tempfilename = 't.e.m.p.f.i.l.e'

def parseOptions():
	
	parser = OptionParser( usage="usage: %prog --dir=<directory> --oldprefix=<oldprefix> --newprefix=<newprefix>" )
	
	parser.add_option( "--path", action="store", type="string", dest="path", help="the directory/file to work in/with" )
	parser.add_option( "--oldprefix", action="store", type="string", dest="oldprefix", help="the prefix to be changed" )
	parser.add_option( "--newprefix", action="store", type="string", dest="newprefix", help="the substitute-prefix" )
	parser.set_description( 'This script will change prefixes in all files in the given directory and '
							+ ' (if necessary) will also rename each prefixed-file. If the given directory matches the '
							+ 'prefix it will be renamed as well.' )
	
	( options, args ) = parser.parse_args()
	
	if options.path == None or options.oldprefix == None or options.newprefix == None:
		print "invalid calling-syntax!"
		parser.print_help()
		sys.exit( 1 )
		
	return ( options, args )
	
def print_level( entry, level ):

	""" nur fuer Ausgabe """		
	s = "|"
	for i in range( level ):
		s = s + '-'

	print s + entry

def modifyFilename( oldfilename, oldprefix, newprefix ):
	""" falls Filename prefix enthaelt: umbenennen """			

	matchobject = pattern.match( oldfilename )
	if  matchobject != None:
		postfix = matchobject.group( 1 )
		newname = newprefix + postfix
		try:
			rename( oldfilename, newname )
			print 'renamed ' + oldfilename + ' to ' + newname
		except OSError:
			print 'could not rename file ' + oldfilename + ': either it doesn\'t exist or ' \
				+ newname + ' is a directory!'

def modifyFilecontent( entry, oldprefix, newprefix ):
	""" Inhalt auf Muster untersuchen und aendern """

	system( "sed -e 's/" + oldprefix + "/" + newprefix + "/g' " + entry + " > " + tempfile )
	system( "attrib -R " + entry )
	system( "del " + entry )
	rename( tempfile, entry )

def diveAndModify( dir, oldprefix, newprefix, level ):
	""" Verzeichnis traversieren, Dateiinhalt aendern und evtl. umbenennen """
		
	chdir( dir )
	
	""" aktuelles Verzeichnis durchforsten	"""
	for entry in listdir( getcwd() ):
		modifyPath( entry, oldprefix, newprefix, level )

	chdir( '..' )
						
def modifyPath( entry, oldprefix, newprefix, level ):
	""" evtl. Verzeichnis traversieren, Dateiinhalt aendern und evtl. umbenennen """
	
	print_level( entry, level )
		
	""" auf Datei oder Verzeichnis ueberpruefen """
	if isdir( entry ):
		chmod( entry, S_IWRITE )
		diveAndModify( entry, oldprefix, newprefix, level+1 )    # rekursiv
		
	elif isfile( entry ):
		modifyFilecontent( entry, oldprefix, newprefix )
			
	modifyFilename( entry, oldprefix, newprefix )

 
""" main: """

oldprefix = ''
newprefix = ''
tempfile = tempfilename

( options, args ) = parseOptions()

basepath = options.path
oldprefix = options.oldprefix
newprefix = options.newprefix

pattern = compile( oldprefix + '(.*)' )

modifyPath( basepath, oldprefix, newprefix, 1 )