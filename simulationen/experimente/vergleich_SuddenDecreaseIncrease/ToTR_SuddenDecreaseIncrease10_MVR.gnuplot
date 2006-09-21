unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease10.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValues.gnuplotdata' w l
