#!/bin/bash

# Le but de ce script est de lancer :
# 1) l'annuaire
# 2) le name node
# 3) les serveurs
# 4) les daemons
# Et de le faire en répartie !

# /!\ à lancer depuis le dossier parent, à savoir v1


# On lance l'annuaire
java -cp bin/ application/RunAnnuaire &
echo "On lance l'annuaire ..."

# On lance le name node
java -cp bin/ hdfs/NameNodeImpl setUp.txt &
echo "On lance le NameNode ..."

# Pour toutes les machines
cat setUp.txt | while read mot ; do
	port="$(cut -d' ' -f1 <<<$mot)"
	serveur_name="$(cut -d' ' -f2 <<<$mot)"
	daemon_name="$(cut -d' ' -f3 <<<$mot)"

	# On lance le serveur
	commande="java -cp $PWD/bin/ hdfs/HdfsServer $port"
	ssh -f $serveur_name $commande

	# On lance le daemon
	#commande="java -cp $PWD/bin/ ordo/DaemonImpl $daemon_name $port $serveur_name"
	#ssh -f $serveur_name $commande
done
