
i=10

while (( $i<=90 ))
do

  if (( $i == 50 ))
  then
      i=$(($i+20))
      continue
  fi

	cd subscribersLeave${i}
	echo betrete verzeichnis subscribersLeave${i}
        for file in actions*
	do
		echo bearbeite ${file}
		sed --in-place=.bak -e 's/subscribersLeave(50)/subscribersLeave('${i}')/g' ${file}
	done
	cd ..
	
	i=$(($i+20))
done



