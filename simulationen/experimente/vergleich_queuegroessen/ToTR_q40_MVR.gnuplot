unset parametric
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_queue40_MeanValueRanges.gnuplotdata' w l
load 'queue40.gnuplot'
replot queue
replot 'totalTemporaryRequests_queue40_MeanValues.gnuplotdata' w l
