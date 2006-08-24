set terminal postscript
unset parametric
plot 'totalTemporaryRequests_queue3000_MeanValueRanges.gnuplotdata' w l
load 'queue3000.gnuplot'
set output 'ToTR_q3000_MVR.ps'
replot queue

