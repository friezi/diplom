unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_ChurnCompensation50_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_ChurnCompensation50_80_900_MeanValues.gnuplotdata' w l