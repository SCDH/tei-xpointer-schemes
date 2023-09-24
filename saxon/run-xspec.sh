#!/bin/sh

LIB="-lib ${project.build.directory}/tei-xpointer-saxon-impl-${project.version}.jar"

for jar in ${project.build.directory}/lib/*.jar; do
    LIB=$LIB" -lib $jar";
done

echo "ant libraries: $LIB"

ant $LIB -Dxspec.version=${xspec.version} $@
