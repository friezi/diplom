unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Referenzverlauf_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_Referenzverlauf_MeanValues.gnuplotdata' w l
