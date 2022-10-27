#!/bin/bash

mkdir /var/log/eseal
chown unisys:unisys /var/log/eseal
cp /opt/eseal/eseal /etc/init.d/eseal
chmod +x /etc/init.d/eseal
chkconfig --add eseal
chkconfig --level 2345 eseal on
