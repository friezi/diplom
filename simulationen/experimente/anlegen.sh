i=50

while (( $i<=90 ))
do

  if (( $i == 50 ))
  then 
      i=$(($i+20))
      continue
  fi

  mkdir subscribersLeave${i}
  cp subscribersLeave50/* subscribersLeave${i}/
  cvs add subscribersLeave${i}
  cd subscribersLeave${i}
  cvs add *
  cd ..
  i=$(($i+20))
done
