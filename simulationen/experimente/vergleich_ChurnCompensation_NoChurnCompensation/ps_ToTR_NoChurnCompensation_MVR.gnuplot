set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoChurnCompensation_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'ToTR_NoChurnCompensation_MVR.ps'
replot
