i=20

while (( $i<=80 ))
do

  mkdir subscribersLeave${i}
  cp subscribersLeave50/* subscribersLeave${i}/
  cvs add subscribersLeave${i}
  cd subscribersLeave${i}
  cvs add *
  cd ..
  i=$(($i+20))

done
