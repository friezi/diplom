0  : startGnuplotRecording
1  : subscribersJoin(500)
1  : setServiceTimeFactor(1)
#100 : startChurn(100,50)
#1000   : setServiceTimeFactor(0.2)
#1000	: subscribersLeave(50)
#2000	: subscribersRejoin
#2000	: setServiceTimeFactor(100)
#3000	: setServiceTimeFactor(50)
#4000	: setServiceTimeFactor(1)
#5000	: setServiceTimeFactor(1.2)
#5000	: stopChurn
6001 : stopGnuplotRecording
6002 : exitSimulation

