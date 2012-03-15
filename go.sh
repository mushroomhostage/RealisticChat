#!/bin/sh -x
CLASSPATH=../craftbukkit-1.1-R8.jar javac *.java -Xlint:deprecation -Xlint:unchecked
rm -rf me
mkdir -p me/exphc/RealisticChat
mv *.class me/exphc/RealisticChat/
jar cf RealisticChat.jar me/ *.yml *.java README.md ChangeLog LICENSE
