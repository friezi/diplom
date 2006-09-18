unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease120.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease120_MeanValues.gnuplotdata' w l
