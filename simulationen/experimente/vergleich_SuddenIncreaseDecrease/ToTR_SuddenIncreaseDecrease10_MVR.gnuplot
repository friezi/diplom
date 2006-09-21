unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenIncreaseDecrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease10.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenIncreaseDecrease10_MeanValues.gnuplotdata' w l
