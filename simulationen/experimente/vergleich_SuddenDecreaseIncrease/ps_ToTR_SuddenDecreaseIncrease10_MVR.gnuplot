set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease10_MVR.ps'
replot queue

