#! /usr/bin/python

import sys
import fileinput
import os

testscenarios = "testscenarios"
tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"

def cutNewline( line ):
    retstring = line
    if line[len( line )-1] == '\n':
        retstring = line[0:len( line )-1]
    elif line[len( line )-1] == '\r':
        retstring = line[0:len( line )-1]
    else:
        return line
 
    if retstring[len( retstring )-1] == '\n':
        retstring = retstring[0:len( retstring )-1]
    elif retstring[len( retstring )-1] == '\r':
        retstring = retstring[0:len( retstring )-1]
        
    return retstring


""" main """

if len( sys.argv ) < 2 & len( sys.argv ) > 3:
    print "invalid syntax!"
    print "usage: " + os.path.basename( sys.argv[0] ) + ' <directory> [<pass>]'
    sys.exit( 1 )
    
seedvalue = ""
passvalue = ""

try:
    seedvalue = cutNewline( fileinput.input( "seed" )[0] )
    fileinput.close()
except IOError:
    print 'file "seed" not found'

if len( sys.argv ) == 3:
    passvalue=sys.argv[2]

dir = sys.argv[1]
os.chdir( dir )

print "entering directory " + dir

for line in fileinput.input( testscenarios ):
    simulation = cutNewline( line )
    if seedvalue != "":
        os.system( "sed -e 's/^[ ]*\(seedValue\)[ ]*=.*$/\\1=" + seedvalue +"/g' " + simulation + " > " + tempfile )
        os.rename( tempfile, simulation )
 
    """ potentiellen infix entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
        
    if passvalue != "":
        
        """ infix _pass<value> eintragen """
        os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + simulation + " > " + tempfile )
        os.rename( tempfile, simulation )
    
    print "starting simulation " + simulation    
    os.system( "java -cp ../../DiscreteAndRealtimeSimulation/ Simulation " + simulation )

    """ infix wieder entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
    
    
