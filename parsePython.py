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
    parser = argparse.ArgumentParser(description='Parse the json result.')
    parser.add_argument('-file', type=str)
    args = parser.parse_args()

    data=json.load(open(args.file+'.json', 'r'))
    f = open(args.file, 'w')

    if data['complete']==True:
        f.write("1\n")
    else:
        f.write("0\n")

    f.write(str(data['timestamp'])+"\n")
    f.close()
#    pprint(data['complete'])
#    pprint(data['timestamp'])








