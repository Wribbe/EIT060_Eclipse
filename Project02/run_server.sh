server_name=$1
port=$2
cd bin
mate-terminal -e "bash -c \"java "main.$server_name" $port; exec bash\""
