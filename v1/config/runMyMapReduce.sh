#!/bin/bash

if [ $# != 2 ]
then
	echo "Mauvais nombre d'arguments, exemple :"
	echo "./runMyMapReduce.sh mapReduce file"
else
	# Lancer l'annuaire
	java Annuaire

	# Lancer les démons sur des machines différentes
	java DaemonImpl succube
	java DaemonImpl lucifer
	java DaemonImpl cthun

	# Lancer le Map/Reduce
	java $1 $2

fi
