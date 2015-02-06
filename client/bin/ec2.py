__author__ = 'icloudobject'

import json
from icotopo.yidbclient.client import YidbClient
from icotopo.ec2.ec2sync import EC2TopoSync
import time
import argparse
import os

parser = argparse.ArgumentParser(description='run script to talk to cloud')
parser.add_argument('-a','--action',help='The action of the command, e.g. sync: sync all resources,init: initial sync, refresh: refresh one resource', required=True)
parser.add_argument('-r','--resource', help='The resourceId, e.g. i78xded34')
parser.add_argument('-c','--cloud', help='The cloud config id')
parser.add_argument('-g','--region', help='The region of the resource')
parser.add_argument('-t','--type', help='The resource type, e.g. Instance')
parser.add_argument('-k','--task', help='The taskId, e.g. uuid')

args = parser.parse_args()
ec2_config_dir = os.path.dirname(os.path.abspath(__file__)) + "/../config/ec2"

def sync():
    if clouds and len(clouds) > 0:
        for cloud in clouds:
            topo_sync = EC2TopoSync(config['cms_endpoint'], ec2_config_dir, cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])
            topo_sync.sync()
    else:
        topo_sync = EC2TopoSync(config['cms_endpoint'], ec2_config_dir, config['topo_repo'])
        topo_sync.sync()

'''
If the keys are stored in CloudConfig class in ICO repo, get the keys from there,
otherwise, load from config
'''
config = json.load(open(ec2_config_dir + "/config.json"))
"get the list of cloud config info from ico repo"
query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
yidb = YidbClient(config['cms_endpoint'])
response = yidb.query("ico",query)
clouds = []
if (response.status_code == 200):
    clouds = response.json()['result']
sync_minutes = config['sync_minutes']
if sync_minutes <= 0:
    sync_minutes = 10


interval = sync_minutes * 60

if args.action == 'sync':
    while (True):
        sync()
        print "Sleep for " + str(interval) + " seconds"
        time.sleep(interval)

if args.cloud:
    query = 'CloudConfig[@_oid="' + args.cloud + '"]{@accessKey,@accessSecret,@topoRepoName}'
    yidb = YidbClient(config['cms_endpoint'])
    response = yidb.query("ico",query)
    if (response.status_code == 200):
        cloud = response.json()['result'][0]
        topo_sync = EC2TopoSync(config['cms_endpoint'], ec2_config_dir, cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])

        if args.action == 'init':
            topo_sync.sync(args.task, True)

        if args.action == 'refresh':
            if (args.type == None or args.resource == None or args.task == None):
                print "resourceType and resource and taskId cannot be None"
            else:
                topo_sync.sync_one_resource(args.task, args.type, args.resource, args.region)

    else:
        print "cannot find CloudConfig for " + args.cloud
