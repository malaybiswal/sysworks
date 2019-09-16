#!/bin/bash

#!/bin/sh
SERVICE='jsonParser'
 
if ps ax | grep -v grep | grep $SERVICE > /dev/null
then
    echo "$SERVICE service running, everything is fine"
else
    echo "$SERVICE is not running"
	cd /root/malay/cronjobs
	sh /root/malay/cronjobs/start.sh &
   # echo "$SERVICE is not running!" | mail -s "$SERVICE down" root
fi

ZABBIXSERVICE='SysWorksZabbix'

if ps ax | grep -v grep | grep $ZABBIXSERVICE > /dev/null
then
    echo "$ZABBIXSERVICE service running, everything is fine"
else
    echo "$ZABBIXSERVICE is not running"
        cd /root/malay/cronjobs
        sh /root/malay/cronjobs/startZabbix.sh &
   # echo "$SERVICE is not running!" | mail -s "$SERVICE down" root
fi
