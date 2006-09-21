unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SuddenIncreaseDecrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease360.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenIncreaseDecrease360_MeanValues.gnuplotdata' w l
