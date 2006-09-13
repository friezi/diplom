set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease120.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease120_MVR.ps'
replot queue

