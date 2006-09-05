unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Polynomial_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_Polynomial_MeanValues.gnuplotdata' w l
