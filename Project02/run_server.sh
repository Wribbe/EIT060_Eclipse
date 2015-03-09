#server_name=$1
port=$1
cd bin
mate-terminal -e "bash -c \"java "main.server" $port; exec bash\""
