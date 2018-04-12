#!/bin/bash
if [ $# -ne 2 ]
then
    echo "No arguments supplied. Please pass CURRENT_WAR NEW_WAR_URL"
    exit 1
fi

CURRENT_WAR=$1
NEW_WAR_URL=$2
WEBAPPS=/opt/app/jetty/base/fe/webapps
BACKUP_DIR=/opt/app/jetty/base/fe
LOG_DIR=/opt/logs/fe

# echo "sudo to root user..."
# sudo /usr/localcw/bin/eksh -c "sudo -i"

echo "creating BACKUPS directory if not already exists..."
mkdir -p $BACKUP_DIR/BACKUPS
chmod 777 $BACKUP_DIR/BACKUPS

echo "jettyFE server stopping..."
service jettyFE stop

cd $WEBAPPS

echo "backing up current war file…"
# Define a timestamp function
timestamp=$(date +%Y%m%d%H%M%S)
mv $CURRENT_WAR* $BACKUP_DIR/BACKUPS/$CURRENT_WAR.backup_${timestamp}

echo "downloading file from NEXUS. File="$NEW_WAR_URL
cd $WEBAPPS
wget $NEW_WAR_URL

echo "chown and chmod..."
chown m98835:mechid *
chmod 775 *

echo "Deleting all logs…"
if [ -d LOG_DIR ];
then
    rm –rf LOG_DIR/*;
fi

echo "jettyFE server starting..."
service jettyFE start

echo "open log"
cd $LOG_DIR
tailf "`ls -t | head -1`"