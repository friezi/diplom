get_local(){

	i=${FROM}	

	cd ${MAIN_DIR}

	while (( ${i} <= ${TO} ))
	do
		mkdir ${HAUPTEXPERIMENT}_${i}
		cd ${HAUPTEXPERIMENT}_${i}
		cp ../../${HAUPTEXPERIMENT}_${i}/${RESULTS} .
		tar xzf ${RESULTS}
		cd ..

		i=$(($i+1))

	done

	cd ..

}

get_remote(){

	i=${FROM}

	cd ${MAIN_DIR}

	while (( ${i} <= ${TO} ))
	do
		mkdir ${HAUPTEXPERIMENT}_${i}
		cd ${HAUPTEXPERIMENT}_${i}
		scp ${REMOTE_DIR}/${HAUPTEXPERIMENT}_${i}/${RESULTS} .
		tar xzf ${RESULTS}
		cd ..

		i=$(($i+1))

	done

	cd ..

}


RESULTS=results.tar.gz
HAUPTEXPERIMENT=hauptexperiment
MAIN_DIR=results_${HAUPTEXPERIMENT}
REMOTE_DIR=bruja.cs.tu-berlin.de:/home/f/friezi/studium/diplomstuff/diplom/simulationen/experimente

FROM=1
TO=7

get_local

FROM=45
TO=50

get_local

FROM=8
TO=44

get_remote


