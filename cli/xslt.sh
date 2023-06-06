#!/bin/sh

JAVAOPTS="-Ddebug=true -Dorg.slf4j.simpleLogger.defaultLogLevel=info"

java $JAVAOPTS -cp "${project.build.directory}/lib/*" net.sf.saxon.Transform $@
