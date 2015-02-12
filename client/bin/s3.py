__author__ = 'icloudobject'


import json
from icotopo.yidbclient.client import YidbClient
from icotopo.s3.s3sync import S3BillingSync
import time
import argparse
import os

parser = argparse.ArgumentParser(description='sync AWS billing from S3 to YIDB.')
parser.add_argument('-a','--action',help='The action of the command, e.g. sync: sync billing data. init: initial sync', required=True)
parser.add_argument('-c','--cloud', help='The cloud config id')

'''
If the keys are stored in CloudConfig class in ICO repo, get the keys from there,
otherwise, load from config
'''
args = parser.parse_args()
s3_config_dir = os.path.dirname(os.path.abspath(__file__)) + "/../config/s3"

config = json.load(open(s3_config_dir + "/config.json"))

sync_minutes = config['sync_minutes']
keep_days = config['keep_days']
if sync_minutes <= 0:
    sync_minutes = 10

interval = int(sync_minutes) * 60

if args.action == 'sync':
    query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
    yidb = YidbClient(config['cms_endpoint'])
    response = yidb.query('ico',query)
    clouds = []
    if (response.status_code == 200):
        clouds = response.json()['result']
    while (True):
        for cloud in clouds:
            topo_sync = S3BillingSync(config['cms_endpoint'],  s3_config_dir , cloud['billingDataBucketName'], cloud['topoRepoName'], keep_days, cloud['accessKey'], cloud['accessSecret'])
            topo_sync.sync()
        print "Sleep for " + str(interval) + " seconds"
        time.sleep(interval)

if (args.cloud):
    query = 'CloudConfig[@_oid="' + args.cloud + '"]{*}'
    yidb = YidbClient(config['cms_endpoint'])
    response = yidb.query("ico",query)
    if (response.status_code == 200 and len(response.json()['result']) > 0):
        cloud = response.json()['result'][0]
        if args.action == 'init':
            payload = {}
            payload['_oid'] = "ROOT"
            payload['initBillingStatus'] = "init"
            r = yidb.upsert_object(cloud['topoRepoName'],"Cloud",payload)
            print(r)
            topo_sync = S3BillingSync(config['cms_endpoint'],  s3_config_dir , cloud['billingDataBucketName'], cloud['topoRepoName'], keep_days, cloud['accessKey'], cloud['accessSecret'])
            topo_sync.sync()
            payload = {}
            payload['_oid'] = "ROOT"
            payload['initBillingStatus'] = "done"
            r = yidb.upsert_object(cloud['topoRepoName'],"Cloud",payload)
            print(r)
    else:
        topo_sync = S3BillingSync(config['cms_endpoint'],  s3_config_dir, config['billing_bucket_name'], config['topo_repo'], keep_days)
        topo_sync.sync()

