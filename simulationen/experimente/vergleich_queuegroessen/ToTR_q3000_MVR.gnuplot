unset parametric
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_queue3000_MeanValueRanges.gnuplotdata' w l
load 'queue3000.gnuplot'
replot queue
replot 'totalTemporaryRequests_queue3000_MeanValues.gnuplotdata' w l
