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
    parser.add_argument('-input', type=str)
    parser.add_argument('-output', type=str)
    args = parser.parse_args()

    data=json.load(open(args.input, 'r'))
    f = open(args.output, 'w')

    print data['complete']
    if data['complete']==True:
        f.write("1\n")
    else:
        f.write("0\n")

    f.write(str(data['timestamp'])+"\n")
    f.close()
#    pprint(data['complete'])
#    pprint(data['timestamp'])








