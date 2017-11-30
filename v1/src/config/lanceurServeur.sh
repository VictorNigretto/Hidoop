#!/bin/bash
#############################################
# Le but de ce script est de lancer
# le serveur sur cette même machine
#############################################

portServ=$1
echo $1
cd /home/adussier/projets_2a/syst_conc/hidoop/MapReduce_V0/src
echo "PWD $PWD"
java hdfs.HdfsServer $portServ
