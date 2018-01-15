Pour lancer l'application, suivre les étapes suivantes :

Tous les scripts se trouvent dans le dossier v1/scripts.

- lancer le script build.sh depuis le dossier v1
Il compile les classes nécessaires à l'exécution d'Hidoop

- lancer le script run_lanceur.sh depuis le dossier v1
Il met en route l'annuaire, le NameNode, les serveurs et les démons

- Enfin, lancer le script run_terminal.sh depuis le dossier v1
Vous pouvez entrer les commandes pour utiliser l'application

Pour modifier les machines sur lesquelles les services sont déployées, vous
pouvez modifier le fichier setUp.txt

Pour chaque ligne (une par machine), il faut indiquer
<port> <nom_machine> <nom_deamon>

- A la fin de l'utilisation de l'application, ou en cas de modification des
  classes, il faut redéployer les serveurs. Avant cela, lancer le script
  reset.sh depuis le dossier v1. Ajouter l'option  bin permet de supprimer les
  classes compilées. Il faudra relancer un build.sh dans ce cas d'utilisation.
