#!/bin/bash

# Le but de ce script est de récupérer les commandes de l'utilisateur en boucle
# exemple : write toto.txt line
# exemple : read toto.txt res.txt
# exemple : delete toto.txt

# /!\ à lancer depuis le dossier parent, à savoir v1

while true
do
	echo "Entrez votre commande : "
	read cmd

	action="$(cut -d' ' -f1 <<<$cmd)"
	file="$(cut -d' ' -f2 <<<$cmd)"
	
	if [ "$action" = "write" ]; then
		format="$(cut -d' ' -f3 <<<$cmd)"
		java -cp bin/ hdfs/HdfsClient $action $file $format &
	elif [ "$action" = "read" ]; then
		fileout="$(cut -d' ' -f3 <<<$cmd)"
		java -cp bin/ hdfs/HdfsClient $action $file $fileout &
	elif [ "$action" = "delete" ]; then
		java -cp bin/ hdfs/HdfsClient $action $file &
	else
		echo "Usage : write <file> <line|kv>"
		echo "Usage : read <filein> <fileout>"
		echo "Usage : delete <file>"
	fi
		 

done
