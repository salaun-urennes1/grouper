#!/bin/sh

# -----------------------------------------------------------------------------
# Script for command line util to load Tree XML
#
# Environment Variable Prequisites
#
#   JAVA_HOME     Must point to your JDK.
# -----------------------------------------------------------------------------

BASEDIR=../..
CLASSPATH=$BASEDIR/config

for file in `ls $BASEDIR/lib/*.jar`;
do
if [ "$CLASSPATH" != "" ]; then
   CLASSPATH=${CLASSPATH}:$file
else
   CLASSPATH=$file
fi
done

$JAVA_HOME/bin/java -cp $CLASSPATH edu.internet2.middleware.signet.util.TreeXMLLoader $1
