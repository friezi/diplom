#! /usr/bin/python

import sys, smtplib, ConfigParser, os, zlib, base64
from optparse import OptionParser
from email.MIMEText import MIMEText

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option('--from',action='store',type='string',dest='frm')
    parser.add_option('--to',action='store',type='string',dest='to')
    parser.add_option('--subject',action='store',type='string',dest='subject')
    parser.add_option('--account',action='store',type='string',dest='account')
    parser.add_option('--textfile',action='store',type='string',dest='textfile')
    
    (options,args) = parser.parse_args()

    if options.frm == None:
        print "Please give from in '--from='" 
        error = True
    if options.to == None:
        print "Please give to in '--to='"
        error = True   
    if options.subject == None:
        print "Please give subject in '--subject='"
        error = True   
    if options.account == None:
        print "Please give account in '--account='"
        error = True   
    if options.textfile == None:
        print "Please give textfile in '--textfile='"
        error = True   
        
    if error == True:
        sys.exit(1)
        
    return (options,args)

""" main """

(options,arg) = parsecmdl()

accountfile = '~/.maccount'
section = options.account

file = open(options.textfile,"rb")
msg = MIMEText(file.read())
msg['Subject'] = options.subject
msg['From'] = options.frm
msg['To'] = options.to

configparser = ConfigParser.ConfigParser()
configparser.read([os.path.expanduser(accountfile)])
host = configparser.get(section,'host')
user = zlib.decompress(base64.b64decode(configparser.get(section,'user')))
password = zlib.decompress(base64.b64decode(configparser.get(section,'password')))

server = smtplib.SMTP(host)
server.starttls()
server.login(user,password)
server.sendmail(options.frm,[options.to], msg.as_string())
server.quit()