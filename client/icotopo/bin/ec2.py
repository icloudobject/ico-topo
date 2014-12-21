__author__ = 'icloudobject'

import json
from icotopo.yidbclient.client import YidbClient
from icotopo.ec2.ec2sync import EC2TopoSync

config = json.load(open("../config/ec2/config.json"))
"get the list of cloud config info from ico repo"
query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
clouds = YidbClient(config['cms_endpoint']).query(query)
if (clouds != None and len(clouds) > 0):
    for cloud in clouds:
        topo_sync = EC2TopoSync(config['cms_endpoint'], cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])
        topo_sync.sync()
else:
    topo_sync = EC2TopoSync(config['cms_endpoint'], config['topo_repo'])
    topo_sync.sync()