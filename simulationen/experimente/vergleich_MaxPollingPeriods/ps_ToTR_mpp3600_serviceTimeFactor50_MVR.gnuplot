set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_mpp3600_serviceTimeFactor50_MVR.ps'
replot queue
