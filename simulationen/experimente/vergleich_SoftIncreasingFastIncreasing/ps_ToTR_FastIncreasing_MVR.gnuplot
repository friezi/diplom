set terminal postscript
unset parametric
plot 'totalTemporaryRequests_FastIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_FastIncreasing_MVR.ps'
replot queue

