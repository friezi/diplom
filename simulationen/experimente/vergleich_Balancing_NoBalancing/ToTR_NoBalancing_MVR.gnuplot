unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoBalancing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoBalancing_MeanValues.gnuplotdata' w l
