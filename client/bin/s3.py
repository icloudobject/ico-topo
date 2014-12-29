__author__ = 'icloudobject'

import json
from icotopo.yidbclient.client import YidbClient
from icotopo.s3.s3sync import S3BillingSync
import time

config = json.load(open("../config/s3/config.json"))
query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
yidb = YidbClient(config['cms_endpoint'])
response = yidb.query("topoconfig",query)
sync_minutes = config['sync_minutes']
keep_days = config['keep_days']
if sync_minutes <= 0:
    sync_minutes = 10

clouds = []
if (response.status_code == 200):
    clouds = response.json()['result']

def sync():
    if clouds and len(clouds) > 0:
        for cloud in clouds:
            topo_sync = S3BillingSync(config['cms_endpoint'],  "../config/s3", cloud['billingDataBucketName'], cloud['topoRepoName'], keep_days, cloud['accessKey'], cloud['accessSecret'])
            topo_sync.sync()
    else:
        topo_sync = S3BillingSync(config['cms_endpoint'],  "../config/s3", config['billing_bucket_name'], config['topo_repo'], keep_days)
        topo_sync.sync()


while (True):
    sync()
    time.sleep(sync_minutes * 60)