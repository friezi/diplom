set terminal postscript
unset parametric
plot 'totalTemporaryRequests_FastAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_FastAdapting_MVR.ps'
replot queue
