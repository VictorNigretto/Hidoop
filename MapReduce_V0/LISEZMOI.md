Ce répertoire correspond à l'arborescence de fichiers que doivent respecter vos rendus
- le répertoire config contient les fichiers d'initialisation pouvant être utiles lors du lancement de la plateforme
- le répertoire data accueille les fichiers de données de l'application
- le répertoire doc accueille les rapports attendus
- le répertoire src contient les codes sources. Ce répertoire contient lui-même les sous-répertoires suivants
  - application, pour le code des applications
  - config, pour les utilitaires de configuration
  - formats, pour la spécification et la réalisation des formats
  - hdfs, pour la mise en œuvre de hdfs
  - ordo pour l'ordonnancement et le contrôle des tâches Map/Reduce
  
En outre,
- le répertoire data contient un fichier d’entrée pour l’application de comptage de mots, ainsi que le fichier résultat correspondant.
*Votre implémentation devra permettre d’exécuter cette application, qui devra obligatoirement donner exactement le même résultat à partir de ce fichier d’entrée.*
- le répertoire src contient des interfaces et des propositions d'ébauche pour certaines classes de la plateforme hidoop :
  - src/application contient les versions itératives et map-reduce du comptage de mots. *La version map-reduce doit pouvoir tourner sur votre plateforme sans avoir à y apporter aucune modification *
  - src/formats contient les interfaces pour la gestion des formats, ainsi que l'implémentation de la classe KV. Ces interfaces doivent être respectées sans aucune modification. Il est toutefois possible d'étendre le type énuméré Format.Type, pour prendre en compte de nouveaux formats.
  - src/hdfs contient une proposition  (non obligatoire) de squelette pour HdfsClient
  - src/map contient les interfaces pour les tâches Map-Reduce. Ces interfaces doivent être respectées sans aucune modification.
  - src/ordo contient l'interface Daemon, qui doit être respecté sans aucune modification, ainsi que des propositions pour les interfaces JobInterface et SortComparator. Seules les méthodes startJob(), setInputFormat(Format.Type ft) et setInputFname(String fname) de l'interface JobInterface doivent être respectées sans aucune modification.