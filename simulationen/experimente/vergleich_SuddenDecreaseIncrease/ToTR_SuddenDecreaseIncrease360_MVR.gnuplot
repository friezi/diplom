unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease360.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValues.gnuplotdata' w l
