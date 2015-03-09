#!/bin/bash
cd bin
#client_name=$1
#address=$2
port=$1
username=$4
password=$5
mate-terminal -e "bash -c \"java "main.client" localhost $port $username $password; exec bash\""
