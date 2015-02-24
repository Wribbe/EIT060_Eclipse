#!/bin/bash
cd bin
client_name=$1
address=$2
port=$3
username=$4
password=$5
echo "mate-terminal -x java "main.$client_name" localhost $port"
mate-terminal -x java "main.$client_name" $address $port $username $password
