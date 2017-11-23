#!/bin/bash
#############################################
# Le but de ce script est de lancer
# les différents serveurs sur des machines
# prédéfinies
#############################################

# Le nombre de serveurs à déployer
nbServeurs=3

# La liste des noms des machines
nomServ1="daurade"
nomServ2="truite"
nomserv3="omble"

# La liste des ports pour ces machines
portServ1=4444
portServ2=5555
portServ3=6666

# La liste des commandes à exécuter
#cmdServ1="java $PWD/hdfs/HdfsServer $portServ1"
#cmdServ2="java $PWD/hdfs/HdfsServer $portServ2"
#cmdServ3="java $PWD/hdfs/HdfsServer $portServ3"
cmdServ1="bash -l $PWD/config/lanceurServeur.sh $portServ1"
cmdServ2="bash -l $PWD/config/lanceurServeur.sh $portServ2"
cmdServ3="bash -l $PWD/config/lanceurServeur.sh $portServ3"

cmdCd="$PWD/hdfs/"

# La boucle d'éxécution
echo "On lance le Serveur $nomServ1 ..."
ssh -f $nomServ1 $cmdServ1
echo "On lance le Serveur $nomServ2 ..."
ssh -f $nomServ2 $cmdServ2
echo "On lance le Serveur $nomServ3 ..."
ssh -f $nomServ3 $cmdServ3
