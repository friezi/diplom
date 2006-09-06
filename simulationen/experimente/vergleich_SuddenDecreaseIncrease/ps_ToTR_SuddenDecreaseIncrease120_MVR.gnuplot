set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease120_MVR.ps'
replot queue

