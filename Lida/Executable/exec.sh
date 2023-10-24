#!/bin/bash
echo 'Executando simulação'

java -jar ./ws3d-0.0.1-full.jar &

echo 'Aguardando início da simulação'

sleep 5s

echo "Executando controlador"

java -jar ./DemoLIDA.jar 

kill -9 $(ps -ef | pgrep -f "ws3d-0.0.1-full")
