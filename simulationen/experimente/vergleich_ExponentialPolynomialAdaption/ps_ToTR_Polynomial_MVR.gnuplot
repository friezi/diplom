set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Polynomial_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'ToTR_Polynomial_MVR.ps'
replot 'totalTemporaryRequests_Polynomial_MeanValues.gnuplotdata' w l
