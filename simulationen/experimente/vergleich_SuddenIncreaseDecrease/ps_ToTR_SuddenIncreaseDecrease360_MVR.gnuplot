set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenIncreaseDecrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease360.gnuplot'
set output 'ToTR_SuddenIncreaseDecrease360_MVR.ps'
replot queue

