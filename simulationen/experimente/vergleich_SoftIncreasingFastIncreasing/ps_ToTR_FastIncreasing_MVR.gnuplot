set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_FastIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_FastIncreasing.gnuplot'
set output 'ToTR_FastIncreasing_MVR.ps'
replot queue

