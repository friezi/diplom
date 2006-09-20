set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenIncreaseDecrease40_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease40.gnuplot'
set output 'ToTR_SuddenIncreaseDecrease40_MVR.ps'
replot queue

