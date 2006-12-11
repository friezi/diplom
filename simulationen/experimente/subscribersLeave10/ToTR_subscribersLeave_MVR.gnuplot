unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_subscribersLeave10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_subscribersLeave10_MeanValues.gnuplotdata' w l
