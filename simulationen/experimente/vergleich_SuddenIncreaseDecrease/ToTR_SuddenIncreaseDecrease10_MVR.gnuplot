unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease10.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValues.gnuplotdata' w l
