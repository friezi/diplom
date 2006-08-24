
class OutOfBoundsError(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

def linetokens(fileinput):
    
    for line in fileinput:
        
        token = line
 
        """ delete trailing returns """
        for i in range(2):
            if len(token) > 0:
                if token[len(token)-1] == '\n':
                    token = token[0:len(token)-1]
                elif token[len(token)-1] == '\r':
                    token = token[0:len(token)-1]
                    
        """ cut of leading spaces """
        for i in range(len(token)):
            if token[0] == ' ' or token[0] == "\t":
                token = token[1:len(token)]
            else:
                break

        """ skip empty lines and comments """
        if len(token) == 0 or token[0] == '#':
            continue
        else:
            yield token
    
def token(number,fileinput):
    
    i = 0
    
    if number < i:
        raise OutOfBoundsError,"number out of bounds"
    
    for token in linetokens(fileinput):
        if i == number:
            return token
        else:
            i+=1

    raise OutOfBoundsError,"number out of bounds"
            