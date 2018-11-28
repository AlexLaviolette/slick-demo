#!/usr/bin/python

import time
from multiprocessing.dummy import Pool as ThreadPool
from collections import Counter
import requests
import json
import os
import pdb
import datetime
import math
import socket

NUM_ACCOUNTS = 30000
DATA_UPDATES_PER_REQUEST = 50
current_count = 0

PB_BASE = "https://test.polargrizzly.com"

partner = "partner"

hostname = socket.gethostname().replace('-', '.')

def call_auth(body):
    return requests.post(PB_BASE + "/auth", json=body)

def create_request_body(num):
    accountId = "account." + hostname + "." + str(num) + "." + str(time.time())
    deviceId = "device." + str(num)
    return {
        'partner': partner,
        'accountId': accountId,
        'deviceId': deviceId
    }

def auth():
    # Generate bodies for the requests
    bodies = [create_request_body(i) for i in xrange(0, NUM_ACCOUNTS)]
    print "Created " + str(len(bodies)) + " auth request bodies. Going to begin sending requests."

    # Make requests to the auth/account creation endpoint
    pool = ThreadPool(128)
    start = time.time()
    auth_responses = pool.map(call_auth, bodies)
    end = time.time()
    diff = (end - start)
    statuses = Counter(map(lambda x: x.status_code, auth_responses))
    elapsed = sorted(map(lambda x: x.elapsed.total_seconds(), auth_responses), reverse=True)[:100]

    print "Created " + str(NUM_ACCOUNTS) + " accounts in " + str(diff) + " seconds"
    print "Rate: " + str (NUM_ACCOUNTS * 1.0/diff) + " req per second."
    print "Statuses: " + str(statuses)
    print "SLOWEST: " + str(elapsed)




print "\n" + "-" * 60 + "\n"
print "Auth Start: " + str(datetime.datetime.now())
auth()
