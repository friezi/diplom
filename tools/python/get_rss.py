#! /usr/bin/python

from os import remove, system
from time import sleep, localtime, time
from string import maketrans, translate
from filecmp import cmp
from optparse import OptionParser
from ConfigParser import ConfigParser
import sys

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--url', action='store', type='string', dest='url' )
    parser.add_option( '--prefix', action='store', type='string', dest='prefix' )
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
    parser.add_option( '--interval', action='store', type='int', dest='interval' )
    parser.add_option( '--minutes', action='store', type='int', dest='minutes' )
    ( options, args ) = parser.parse_args()
    if options.dir == None:
        print "Please give direcory in '--dir='" 
        error = True
    if options.interval == None:
        print "Please give interval in '--interval='"
        error = True   
        
    if error == True:
        sys.exit( 1 )
        
    return ( options, args )

def download_rss( url, filename, logfile, dir ):
    """ downloads the url via wget to localfile 
    
    nothing more
    """

    command = 'wget --output-document=' + dir + '/' + filename + ' ' + "'" + url + "'" + ' 2>>' + dir + '/' + logfile
    system( command )



""" main """

url = "http://rss.sourceforge.net/sourceforge/export/rss2_sfnews.php"
fileprefix = "rss_sourceforge"
logfile = "wget.log"
dir = "rss"
interval = 5

( options, args ) = parsecmdl()

dir = options.dir
interval = options.interval
minutes = 0

if options.url == None:
    urlparser = ConfigParser()
    urlparser.read( [dir + "/URL"] )
    options.url = urlparser.get( 'DEFAULT', 'url' )

if options.prefix == None:
    options.prefix = options.dir + "_rss"
    
if options.minutes != None:
    minutes = options.minutes
    if minutes <= 0:
        print 'only positiv values for option --minutes allowed'
        print 'to run an infinit loop omit --minutes'
        sys.exit()

url = options.url
fileprefix = options.prefix
 
count = 1
newfile = ""
oldfile = ""
map = maketrans( ' :()', '_-se' )

starttime = time()

""" close stderr """

while True:

    """ exit on reached timelimit """    
    if minutes > 0:
        if ( time()-starttime )*60 >= minutes:
            break 

    """ generate filename """
    date = localtime()
    filename = fileprefix + '+' + str( count ) + '+' + translate( str( date ), map ) + ".xml"

    """ download file """
    download_rss( url, filename, logfile, dir )
   
    newfile = dir + '/' + filename
   
    """ compare newfile with oldfile and delete newfile if no difference """
    if oldfile != "":
        if cmp( oldfile, newfile ) == True:
            remove( newfile )
        else:
            oldfile = newfile
            count+=1
    else:
        oldfile = newfile
        count+=1
    
    sleep( interval )

    
