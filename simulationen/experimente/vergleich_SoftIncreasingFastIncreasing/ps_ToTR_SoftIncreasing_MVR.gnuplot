set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SoftIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftIncreasing.gnuplot'
set output 'ToTR_SoftIncreasing_MVR.ps'
replot queue

