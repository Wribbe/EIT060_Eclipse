#!/bin/bash
cd bin
client_name=$1
port=$2
username=$3
password=$4
echo "mate-terminal -x java "main.$client_name" localhost $port"
mate-terminal -x java "main.$client_name" localhost $port $username $password
