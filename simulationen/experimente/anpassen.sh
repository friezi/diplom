
i=1
subs=2

while (( $i<=50 ))
do
	cd hauptexperiment_${i}
	echo betrete verzeichnis hauptexperiment_${i}
        for file in mac_parameters*
	do
		echo bearbeite ${file}
		sed --in-place=.bak -e 's/subscribers=\(.*\)/subscribers='${subs}'/g' ${file}
	done
	cd ..
	
	i=$(($i+1))
	subs=$(($subs+2))
done



