import argparse
import numpy as np
import os
import requests
import sys
import threading
import subprocess
import json
from pprint import pprint

if __name__ == "__main__":
    '''Parse the test parameters'''
    parser = argparse.ArgumentParser(description='Parse the task json.')
    parser.add_argument('-input', type=str)
    parser.add_argument('-output', type=str)
    args = parser.parse_args()

    data=json.load(open(args.input, 'r'))
    f = open(args.output, 'w')
    
    f.write("Task "+str(data['id'])+" has "+str(len(data['jobs']))+" jobs.\n")
    totalJob=0
    for i in range (0, len(data['jobs'])):
        srcNum=len(data['jobs'][i]['potential_srcs'])
        dstNum=len(data['jobs'][i]['potential_dsts'])
        f.write("\t Job "+str(i+1)+" has "+str(srcNum)+" potential input data sources and "+str(dstNum)+" potential computation nodes.\n")
        totalJob+=srcNum*dstNum
    f.write("Overvall there are "+str(totalJob)+" candidate jobs.\n")
    f.close






