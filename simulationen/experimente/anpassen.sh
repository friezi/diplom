
i=20

DIR=churnCompensation

while (( $i<=90 ))
do
	cd ${DIR}${i}
	echo betrete verzeichnis ${DIR}${i}
        for file in actions.al
	do
		echo bearbeite ${file}
		sed --in-place=.bak -e 's/startChurn(10,900)/startChurn('${i}',900)/g' ${file}
	done
	cd ..
	
	i=$(($i+10))
done



