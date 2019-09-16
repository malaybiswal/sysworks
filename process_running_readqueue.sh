#!/bin/bash

#!/bin/sh
SERVICE='readQueueInsertDB'
 
if ps ax | grep -v grep | grep $SERVICE > /dev/null
then
    echo "$SERVICE service running, everything is fine"
else
    echo "$SERVICE is not running"
	cd /root/malay/cronjobs
	sh /root/malay/cronjobs/startreadqueue.sh &
   # echo "$SERVICE is not running!" | mail -s "$SERVICE down" root
fi

ZabbixSERVICE='SysWorksInsertDBZabbix'

if ps ax | grep -v grep | grep $ZabbixSERVICE > /dev/null
then
    echo "$ZabbixSERVICE service running, everything is fine"
else
    echo "$ZabbixSERVICE is not running"
        cd /root/malay/cronjobs
        sh /root/malay/cronjobs/startreadqueueZabbix.sh &
   # echo "$SERVICE is not running!" | mail -s "$SERVICE down" root
fi
