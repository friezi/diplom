unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenDecreaseIncrease40.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValues.gnuplotdata' w l
