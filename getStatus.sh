#!/bin/bash

fileNamePart=".json"
#echo $1$2
curl $1$2 > $3".json"

#cat $1$fileNamePart$2$jsonName

python parseStatusJson.py -input $3".json" -output $3



