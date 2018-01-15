#!/bin/bash

# Le but de ce script est de supprimer toutes les modifications qui auraient pû
# être engendrés par les autres scripts

# Supprimer les processus locaux
killall java
killall rmiregistry

# Supprimer les processus distans
cat setUp.txt | while read mot ; do
	serveur_name="$(cut -d' ' -f2 <<<$mot)"
	commande="killall java"
	ssh -f $serveur_name $commande
done

# Supprimer tous les .class
if [ "$1" = "bin" ]; then
	rm -rf bin/*
	echo "On a supprimé les bin"
fi
