#!/bin/sh

java -cp lib/*:dist/wintermute.jar icfpc2020.Main || echo "run error code: $?"

# java -jar /solution/app/build/Main.jar "$@" || echo "run error code: $?"
