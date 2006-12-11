set terminal postscript
unset parametric
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_queue40_MeanValueRanges.gnuplotdata' w l
load 'queue40.gnuplot'
set output 'ToTR_q40_MVR.ps'
replot queue

