unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease360.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease360_MeanValues.gnuplotdata' w l
