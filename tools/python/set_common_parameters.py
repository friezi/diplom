#! /usr/bin/python

from propertyreader import PropertyReader, PropertyReaderException
from optparse import OptionParser
import linescanner
import sys
import os
import os.path

testscenarios = 'testscenarios'
tempfile = "t.e.m.p.f.i.l.e"

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
    parser.add_option( '--common_parameters', action='store', type='string', dest='common_parameters' )
 
    ( options, args ) = parser.parse_args()
 
    if options.dir == None:
        print "Please give directory in '--dir='" 
        error = True
  
    if options.common_parameters == None:
        print "Please give common_parameters-file in '--common_parameters='" 
        error = True
        
    if error == True:
        print "usage: " + os.path.basename( sys.argv[0] ) + " --dir=<directory> --common_parameters=<common_parameters>"
        sys.exit( 1 )
        
    return ( options, args )

def escapeForSed( string ):
    
    new = ""
    
    for i in range( len( string ) ):
        if string[i] == '/':
            new = new + '\\'
        new = new + string[i]
    
    return new

""" main """

toplayer = ""
sublayer = ""
sedcommands = ""

( options, args ) = parsecmdl()

""" get options-values """
dir = options.dir
common_parameters = options.common_parameters

""" read properties from common_parameters """
cp_propertyReader = PropertyReader( common_parameters )
toplayer = cp_propertyReader.getProperty( 'Toplayer' )
sublayer = cp_propertyReader.getProperty( 'Sublayer' )

if toplayer != None:
        sedcommands = sedcommands + ' -e \'s/^[ ]*\(BRITEToplayerFile\)[ ]*=\(.*\)$/\\1=' + escapeForSed(toplayer) + '/g\''

if sublayer != None:
        sedcommands = sedcommands + ' -e \'s/^[ ]*\(BRITESublayerFile\)[ ]*=\(.*\)$/\\1=' + escapeForSed(sublayer) + '/g\''
    
""" read testscenarios """
os.chdir( dir )

ts_file = open( testscenarios,'r' )
for ts_line in linescanner.linetokens(ts_file):

    """ read properties from testscenario """
    ts_propertyReader = PropertyReader( ts_line )
    sc_filename = ts_propertyReader.getProperty('scenarioFile')
    original = sc_filename + ".orig"

    """ invoke sed on scenarioFile """
    if sc_filename != 'none':
        if os.path.exists(original) == False:
            os.system("sed " + sedcommands + " " + sc_filename + " > " + tempfile)
            os.rename(sc_filename,original)
            os.rename(tempfile,sc_filename)

ts_file.close()
        
    



