#!/usr/bin/env bash

if [ $# -eq 0 ]
then
  echo "USAGE: run.sh APPNAME [ARGS]";
  echo "To list available tools: run.sh list";
  exit
fi

if [ "$1" = "list" ]
then
  echo SimulateTrajectory
  echo VisualizeTrajectory 
  echo ConvertTrajectory 
  echo PDistribution 
  echo LinkerDistribution
  exit
fi

# Get the root directory in case this script is being called from elsewhere
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Verify that the user has Java 7 installed
# Otherwise there will be an obscure UnsupportedClassVersion error
version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$version" < "1.7" ]]; then
    echo "Need Java 7 or greater. You have Java $version installed."
    exit
fi

# Run the simulation with the passed arguments
java -Xmx2000m -Dlog4j.configuration=log4j.properties -cp $DIR:$DIR/build:$DIR/dist/*:$DIR/lib/* us.palpant.science.$@
