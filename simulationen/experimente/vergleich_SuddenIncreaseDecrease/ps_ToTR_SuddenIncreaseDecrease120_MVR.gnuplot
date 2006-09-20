set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenIncreaseDecrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease120.gnuplot'
set output 'ToTR_SuddenIncreaseDecrease120_MVR.ps'
replot queue

