set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_ChurnCompensation400_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers400_80_900.gnuplot'
replot queue
set output 'ToTR_ChurnCompensation400_80_900_MVR.ps'
replot
