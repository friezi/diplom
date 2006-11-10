i=20

while (( $i<=50 ))
do
  mkdir hauptexperiment_${i}
  cp hauptexperiment_1/* hauptexperiment_${i}/
  cvs add hauptexperiment_${i}
  cd hauptexperiment_${i}
  cvs add *
  cd ..
  i=$(($i+1))
done
