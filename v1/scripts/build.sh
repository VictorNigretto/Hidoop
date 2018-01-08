#!/bin/bash

# Le but de ce script est de compiler les sources du code Java
# et de les mettres dans un même répertoire ./bin

# /!\ à lancer depuis le dossier parent, à savoir v1

javac -d bin -cp src/*/*.java
echo "Tout est compilé ! <3"
