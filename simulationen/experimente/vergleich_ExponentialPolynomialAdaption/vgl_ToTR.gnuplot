set terminal aqua
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Exponential_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_Polynomial_MeanValues.gnuplotdata' w l
