set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_ChurnCompensation50_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers50_80_900.gnuplot'
replot queue
set output 'ToTR_ChurnCompensation50_80_900_MVR.ps'
replot
