set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Exponential_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_Exponential_MVR.ps'
replot 'totalTemporaryRequests_Exponential_MeanValues.gnuplotdata' w l
