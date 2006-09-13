unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_NoChurnCompensation400_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers400_80_900.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoChurnCompensation400_80_900_MeanValues.gnuplotdata' w l
