#!/bin/bash

# curl -v http://sdn0-ui:6666/task -H 'Content-type: application/json' -d '[ { "id": 1, "jobs": [ { "potential_srcs": [ { "ip": "10.10.14.6", "port": 1234 } ], "potential_dsts": [ { "ip": "10.10.14.7", "port": 4444 } ], "protocol": "tcp", "file-size": 100000 }, { "potential_srcs": [ { "ip": "10.10.14.7", "port": 1235 } ], "potential_dsts": [ { "ip": "10.10.14.6", "port": 4445 } ], "protocol": "tcp", "file-size": 100000 }]}]'
curl -v http://140.221.221.124:6666/task -H 'Content-type: application/json' -d '[
{ "id": 1,
	    "jobs": [
		    { "potential_srcs": [ { "ip": "10.10.15.6", "port": 1234 } ], "potential_dsts": [ { "ip": "10.10.15.7", "port": 4444 }, { "ip": "10.10.15.8", "port": 4444 } ], "protocol": "tcp", "file-size": 100000 },
				    { "potential_srcs": [ { "ip": "10.10.16.6", "port": 1235 } ], "potential_dsts": [ { "ip": "10.10.16.7", "port": 4445 }, { "ip": "10.10.16.8", "port": 4445 } ], "protocol": "tcp", "file-size": 100000 }
					    ]
					}]'
