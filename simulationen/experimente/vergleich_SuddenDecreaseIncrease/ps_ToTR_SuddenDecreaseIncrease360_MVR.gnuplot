set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease360.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease360_MVR.ps'
replot queue

