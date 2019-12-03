#!/usr/bin/env bash

# Run this from the src directory.

# (Re)compile the code
rm -f segmentedfilesystem/*.class
javac segmentedfilesystem/*.java

# Run the client
java segmentedfilesystem.Main

