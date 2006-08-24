set terminal postscript
unset parametric
plot 'totalTemporaryRequests_queue40.gnuplotdata_MeanValueRanges' w l
load 'queue40.gnuplot'
set output 'ToTR_q40_MVR.ps'
replot queue

