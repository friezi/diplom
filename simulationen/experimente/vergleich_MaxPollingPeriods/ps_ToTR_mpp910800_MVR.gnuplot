set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_mpp910800_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_mpp910800_MVR.ps'
replot queue

