set terminal postscript
unset parametric
plot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease360_MVR.ps'
replot queue

