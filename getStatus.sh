#!/bin/bash

fileNamePart="-monitor"
jsonName=".json"
curl $1$2 > $1$fileNamePart$2$jsonName

python parseStatus.py $1$fileNamePart$2



