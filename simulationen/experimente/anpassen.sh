
i=10

while (( $i<=90 ))
do
	cd subscribersLeave${i}
	echo betrete verzeichnis subscribersLeave${i}
        for file in *
	do
		echo bearbeite ${file}
		sed --in-place=.bak -e 's/SubscribersLeave/subscribersLeave/g' ${file}
	done
	cd ..
	
	i=$(($i+10))
done



