sh application.LanceurHDFS.sh

start=$SECONDS
java hdfs.HdfsClient write line "../PetitTexte.txt"
duration=$((SECONDS - start ))
