unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SuddenIncreaseDecrease40.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease40_MeanValues.gnuplotdata' w l
