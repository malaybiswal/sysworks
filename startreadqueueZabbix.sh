#!/bin/bash
cd /root/malay/javaProject/
export JAVA_HOME=/opt/jdk1.8.0_60
echo "Java Home is $JAVA_HOME"
export PATH=/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/opt/CA/SharedComponents/bin:/opt/CA/SharedComponents/ccs/cam/bin:/root/bin:/root/bin:/opt/jdk1.8.0_60/bin:/opt/jdk1.8.0_60/jre/bin
export CLASSPATH=.:..:$CLASSPATH:
echo "Path is is $PATH"
echo "CLASSPATH is is $CLASSPATH"
$JAVA_HOME/bin/java  SysWorksInsertDBZabbix >  /root/malay/cronjobs/SysWorksInsertDBZabbix.lst
echo "$JAVA_HOME/bin/java  /root/malay/javaProject/SysWorksInsertDBZabbix"
#ls -ltr
