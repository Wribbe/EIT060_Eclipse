#!/bin/bash
cd bin
#client_name=$1
#address=$2
port=$1
username=$2
password=$3
mate-terminal -e "bash -c \"java "main.client" localhost $port $username $password; exec bash\""
