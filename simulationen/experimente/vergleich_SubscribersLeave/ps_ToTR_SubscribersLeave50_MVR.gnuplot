set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SubscribersLeave50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'ToTR_SubscribersLeave50_MVR.ps'
replot
