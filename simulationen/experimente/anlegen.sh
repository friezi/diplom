i=20

DIR=churnCompensation

while (( $i<=90 ))
do

  mkdir ${DIR}${i}
  cp ${DIR}10/* ${DIR}${i}/
  cvs add ${DIR}${i}
  cd ${DIR}${i}
  cvs add *
  cd ..
  i=$(($i+10))

done
