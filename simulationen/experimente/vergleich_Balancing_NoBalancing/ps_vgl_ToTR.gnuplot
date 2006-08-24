set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Balancing_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'vgl_ToTR.ps'
replot 'totalTemporaryRequests_NoBalancing_MeanValues.gnuplotdata' w l
