import linescanner
from re import compile, match

class PropertyReaderException:
    def __init__( self, msg ):
        self.msg = msg
        
    def __str__( self ):
        return repr( self.msg )

class PropertyReader:
    """For reading key-value-pairs. No sections are supported and necessary."""
    
    def __init__( self , filename="" ):
        self.filename = ""
        self.properties = {}
        self.pattern = compile( "^(\w+)\s*(=|:)\s*(.*)$" )
        if filename != "":
            self.load( filename )
       
    def load( self, filename ):
        "Loads the definitions from a file"
        
        self.filename = filename
        file = open( filename, 'r' )
        
        for line in linescanner.linetokens( file ):
            
            match = self.pattern.match( line )
            
            if match == None:
                file.close()
                raise PropertyReaderException( "invalid line in file " + self.filename + ": " + line )
            
            self.properties[match.group( 1 )] = match.group( 3 )
            
        file.close()
            
    def getProperty( self, key ):
        try:
            return self.properties[key]
        except KeyError:
            return None
        