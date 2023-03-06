#!/bin/sh

SCRIPT=$(realpath "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

BUILDPATH=$SCRIPTPATH/..

JARS=$BUILDPATH/lib/Saxon-HE-${saxon.version}.jar
JARS=$JARS:$BUILDPATH/lib/xmlresolver-${xmlresolver.version}.jar
JARS=$JARS:$BUILDPATH/lib/slf4j-api-${slf4j.version}.jar
JARS=$JARS:$BUILDPATH/lib/slf4j-simple-${slf4j.version}.jar
JARS=$JARS:$BUILDPATH/lib/antlr4-runtime-${antlr.version}.jar
JARS=$JARS:$BUILDPATH/lib/tei-xpointer-grammar-${project.version}.jar
JARS=$JARS:$BUILDPATH/${project.artifactId}-${project.version}.jar

JAVAOPTS="-Ddebug=true -Dorg.slf4j.simpleLogger.defaultLogLevel=info"

java $JAVAOPTS -cp $JARS net.sf.saxon.Transform $@
