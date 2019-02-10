#!/usr/bin/env bash

if [ -z "$VERSION" ]; then
    VERSION=$(curl -sq https://chromedriver.storage.googleapis.com/LATEST_RELEASE)
fi
OS=win32
case "$(uname -s)" in
   Darwin)
     OS=mac64
     ;;
   Linux)
     OS=linux64
     ;;
esac
ZIP_URL=https://chromedriver.storage.googleapis.com/$VERSION/chromedriver_$OS.zip
ZIP_FILE=$HOME/browserdrivers/chromedriver/zip/chromedriver_${VERSION}.zip
mkdir -p $HOME/browserdrivers/chromedriver
mkdir $HOME/browserdrivers/chromedriver/zip/
mkdir $HOME/browserdrivers/chromedriver/current/
mkdir $HOME/browserdrivers/chromedriver/$VERSION/
if ! [ -s "${ZIP_FILE}" ]; then
    curl -sS $ZIP_URL > $ZIP_FILE
fi
unzip -q -d $HOME/browserdrivers/chromedriver/$VERSION/ $ZIP_FILE
cp $HOME/browserdrivers/chromedriver/$VERSION/* $HOME/browserdrivers/chromedriver/current/
