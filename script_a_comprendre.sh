#!/bin/bash
############################################
#                                          #
#  Deployment script for Hidoop and HiDFS  #
#                                          #
############################################

# Source variables
source config/config.sh

############################################
############################################

# Build logs directory and empty it
if [ ! -d $log_dir ]; then
	mkdir $log_dir
else
	rm $log_dir/*
fi

# Launch our RMI registry implementation locally
java -cp $bin_dir distantRmi/DaemonsRegistryImpl &
echo "Daemons RMI registry launched locally!"

# Foreach computer in list.deploy
cat $list | while read computer_address; do
	# Build command line, and redirecting IO
	command_line="bash -l $PWD/config/deploy_action.sh $PWD $bin_dir $working_dir $HOSTNAME"
	stdout="$log_dir/$computer_address.log"
	stderr="$stdout"
	
	# Launch a node, using distant script
	ssh -f $computer_address $command_line >> $stdout 2>> $stderr
	echo "Launched node on $computer_address!"
done


