server_name=$1
port=$2
cd bin
mate-terminal -x java "main.$server_name" $port
