set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease10.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease10_MVR.ps'
replot queue

