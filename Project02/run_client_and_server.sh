#!/bin/bash

server_name="server"
client_name="client"

port=1234

rm -rf run_env
mkdir run_env

rm -rf bin/stores
mkdir bin/stores

cp stores/* bin/stores

cd bin

mate-terminal -x java "main.$server_name" $port 
mate-terminal -x java "main.$client_name" localhost $port 
