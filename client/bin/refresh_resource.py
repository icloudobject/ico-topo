__author__ = 'icloudobject'

import sys
import json
from icotopo.yidbclient.client import YidbClient
from icotopo.ec2.ec2sync import EC2TopoSync

print 'Number of arguments:', len(sys.argv), 'arguments.'
print 'Argument List:', str(sys.argv)

if (len(sys.argv) < 5):
    print "Run as: refresh_resource.py cloud_config_id resource resource_id region_name"

config_id = sys.argv[1]
resource = sys.argv[2]
resource_id = sys.argv[3]
region_name = sys.argv[4]

config = json.load(open("../config/ec2/config.json"))
"get the list of cloud config info from ico repo"
query = 'CloudConfig[@_oid="' + config_id + '"]{@accessKey,@accessSecret,@topoRepoName}'
yidb = YidbClient(config['cms_endpoint'])
response = yidb.query("ico",query)
cloud = {}
if (response.status_code == 200):
    cloud = response.json()['result'][0]
else:
    print "Cloud config cannot be found"
    sys.exit(1)

topo_sync = EC2TopoSync(config['cms_endpoint'], "../config/ec2", cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])

if (resource == 'CLOUD'):
    topo_sync.get_region()
else:
    topo_sync.sync_resource(resource, resource_id, region_name)





