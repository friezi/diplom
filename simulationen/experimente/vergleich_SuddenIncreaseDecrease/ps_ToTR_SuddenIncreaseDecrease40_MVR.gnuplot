set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease40.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease40_MVR.ps'
replot queue

