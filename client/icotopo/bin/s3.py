__author__ = 'icloudobject'

import json
from icotopo.yidbclient.client import YidbClient
from icotopo.s3.s3sync import S3BillingSync

config = json.load(open("../config/s3/config.json"))
query = 'CloudConfig[@cloudName="aws"]{@accessKey,@accessSecret,@topoRepoName}'
clouds = YidbClient(config['cms_endpoint']).query(query)
if (clouds != None and len(clouds) > 0):
    for cloud in clouds:
        topo_sync = S3BillingSync(config['cms_endpoint'], cloud['billingDataBucketName'], cloud['topoRepoName'], cloud['accessKey'], cloud['accessSecret'])
        topo_sync.sync()
else:
    topo_sync = S3BillingSync(config['cms_endpoint'], config['billing_bucket_name'], config['topo_repo'])
    topo_sync.sync()