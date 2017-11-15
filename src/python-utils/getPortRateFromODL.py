import sys
from subprocess import Popen, PIPE
import json
import time

TIME = 2

def getPort(url, file):
    p = Popen(['curl', '-u', 'admin:admin', url], stdout=PIPE, stderr=PIPE)
    out, err = p.communicate()
    out = out.decode("utf-8")

    # print(out)

    port_info = json.loads(out)

    abw = int(port_info['port'][0]['tx-speed'])

    abw = float(abw * 8 / 1000000000)

    # print(abw)
    fd = open(file, 'w')
   

   # file.seek(0)
    fd.write(str(abw))
    fd.close()


if __name__ == '__main__':

    baseurl = "http://131.215.207.182:8181/restconf/operational/alto-bwmonitor:speeds/port/"
    
    port=[]
    url=[]
    files=[]
    for i in range(1, len(sys.argv)):
        port.append(sys.argv[i])
        url.append(baseurl+sys.argv[i])
        files.append('input-'+str(i))
        #f=open('input-'+str(i), 'w')
        #fd.append(f)
    print(port)

#    port1 = sys.argv[1]
#    port2 = sys.argv[2]
#    port3 = sys.argv[3]
#
#    url1 = baseurl + port1
#    url2 = baseurl + port2
#    url3 = baseurl + port3
#
#    file1 = open('input', 'w')
#    file2 = open('input1', 'w')
#    file3 = open('input2', 'w')
#
    while True:
        for i in range(0, len(port)):
                getPort(url[i], files[i])
#        getPort(url1, file1)
#        getPort(url2, file2)
#        getPort(url3, file3)
#
        time.sleep(TIME)

