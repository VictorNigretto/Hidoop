#!/bin/bash
#############################################
# Le but de ce script est de lancer
# le fichier Map Reduce
#############################################

fileMr=$1
file=$2

emplacementMr="application.$fileMr"

cd src
javac */*.java
pwd >> stdout
java $emplacementMr $file >> stdout 2>> stderr
rm */*.class
