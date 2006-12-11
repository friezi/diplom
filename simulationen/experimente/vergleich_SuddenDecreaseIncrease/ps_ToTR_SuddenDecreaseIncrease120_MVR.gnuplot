set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease120.gnuplot'
set output 'ToTR_SuddenDecreaseIncrease120_MVR.ps'
replot queue

