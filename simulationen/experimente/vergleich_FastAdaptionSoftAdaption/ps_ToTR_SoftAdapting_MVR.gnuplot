set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SoftAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SoftAdapting_MVR.ps'
replot queue

