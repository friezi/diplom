unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease120.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValues.gnuplotdata' w l
