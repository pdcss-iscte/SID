@echo off
start "SERVER db1" /MIN mongod --config \replicaimdb\server1\server1.conf
start "SERVER db2" /MIN mongod --config \replicaimdb\server2\server2.conf
start "SERVER db3" /MIN mongod --config \replicaimdb\server3\server3.conf