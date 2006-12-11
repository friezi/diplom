unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_FastDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_FastDegrading.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastDegrading_MeanValues.gnuplotdata' w l
