set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SoftIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftIncreasing.gnuplot'
set output 'ToTR_SoftIncreasing_MVR.ps'
replot queue

