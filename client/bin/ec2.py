__author__ = 'icloudobject'

import json
from icotopo.yidbclient.client import YidbClient
from icotopo.ec2.ec2sync import EC2TopoSync
import time

config = json.load(open("../config/ec2/config.json"))
"get the list of cloud config info from ico repo"
query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
yidb = YidbClient(config['cms_endpoint'])
response = yidb.query("topoconfig",query)
clouds = []
if (response.status_code == 200):
    clouds = response.json()['result']
sync_minutes = config['sync_minutes']
if sync_minutes <= 0:
    sync_minutes = 10

def sync():
    if clouds and len(clouds) > 0:
        for cloud in clouds:
            topo_sync = EC2TopoSync(config['cms_endpoint'], "../config/ec2", cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])
            topo_sync.sync()
    else:
        topo_sync = EC2TopoSync(config['cms_endpoint'], "../config/ec2", config['topo_repo'])
        topo_sync.sync()

interval = sync_minutes * 60
while (True):
    sync()
    print "Sleep for " + str(interval) + " seconds"
    time.sleep(interval)