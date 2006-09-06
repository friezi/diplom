set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease40_MVR.ps'
replot queue

