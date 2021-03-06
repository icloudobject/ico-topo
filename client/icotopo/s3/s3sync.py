'''
This aws client will sync the detail billing data
'''
import awscli.clidriver
import json
import unicodedata
import os
import logging
import re
import zipfile,os.path
from icotopo.yidbclient.client import YidbClient
from icotopo.awsclient.client import AwsClient
import datetime

class S3BillingSync ():

    def unzip(self, source_filename, dest_dir):
        with zipfile.ZipFile(source_filename) as zf:
            for member in zf.infolist():
                words = member.filename.split('/')
                path = dest_dir
                for word in words[:-1]:
                    drive, word = os.path.splitdrive(word)
                    head, word = os.path.split(word)
                    if word in (os.curdir, os.pardir, ''): continue
                    path = os.path.join(path, word)
                zf.extract(member, path)

    def __init__(self, endpoint, config_path, bucket=None, repo=None, keep_days=90, key=None,secret=None):
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
        self.driver = awscli.clidriver.create_clidriver()
        self.endpoint = endpoint
        self.bucket = bucket
        self.repo = repo
        self.config_path = config_path
        self.keep_days = keep_days

        self.yidb = YidbClient(endpoint)
        self.regions = []
        self.aws = AwsClient(key, secret)
        resource_str = open(config_path + '/resource.json').read()
        self.resources = json.loads(resource_str)

    def u2s(self, unicode):
        "convert unicode string to string"
        return unicodedata.normalize('NFKD', unicode).encode('ascii','ignore')

    def sync(self):
        "sync billing line item for Instance usage"
        args = ['s3', 'ls', self.bucket]
        r = self.aws.call_cli(args)
        num = 0
        for line in r.split("\n"):
            items = re.split(" +", line)
            if len(items) > 3:
                numstr = items[0].replace("-","") + items[1].replace(":","")
                if (num < int(numstr) and "billing-detailed-line-items-with-resources-and-tags" in items[3]):
                    file = items[3]
                    num = int(numstr)
        local_file = "/tmp/" + file[:len(file)-4]
        args = ['s3','cp', "s3://" + self.bucket + "/" + file, "/tmp"]
        r = self.aws.call_cli(args)
        self.unzip("/tmp/" + file, "/tmp")
        lines = reversed(open(local_file).readlines())
        content = [x.strip('\n') for x in lines]
        header = content[-1].split(",")
        for resource in self.resources:
            class_name = resource['className']
            usage_type = resource['usageType']
            row_list = []
            for line in content:
                if (usage_type in line):
                    row = {}
                    index = 0
                    for item in line.split(","):
                        row[header[index]] = item[1:][:-1]
                        index = index + 1
                    print("%s\t%s\t%s\t%s"  % (row['ResourceId'], row['UsageType'], row['ReservedInstance'], row['UsageStartDate']))
                    index = row['UsageType'].index(":")+1
                    row['InstanceType'] = row['UsageType'][index:]
                    row_list.append(row)
            response = self.yidb.post_service_model(self.repo,class_name,row_list, 's3')
            print(response.content)

            # remove records days ago
            days = datetime.date.today() - datetime.timedelta(self.keep_days)
            unix_time= long(days.strftime("%s")) * 1000
            query = class_name + "[@_createtime < date(" + str(unix_time) + ")]"
            response = self.yidb.query(self.repo, query)
            if (response.status_code == 200):
                obj_list = response.json()['result']
                for obj in obj_list:
                    self.yidb.delete(self.repo, class_name, obj['_oid'])



