#! /usr/bin/python
import os
import sys
import linescanner

tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"
testscenarios = "testscenarios"

if len(sys.argv) != 2:
    print "usage: " + os.path.basename( sys.argv[0] ) + ' <directory>'
    sys.exit()
    
os.chdir(sys.argv[1])


file = open(testscenarios,'r')
for simulation in linescanner.linetokens( file ):

    """ potentiellen infix entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
                + " -e 's/^[ ]*\(gnuplotFileAvgMsgDelayRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
                + " -e 's/^[ ]*\(gnuplotFileRelReOmRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
                + " -e 's/^[ ]*\(gnuplotFileAvgUptodateRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
                + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
    
file.close()
