'''
This aws client will sync the detail billing data
'''
import sys
import awscli.clidriver
from StringIO import StringIO
import json
import unicodedata
import os
import inspect
import logging
import re
import zipfile,os.path
import csv
from icotopo.yidbclient.client import YidbClient

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

    def __init__(self, endpoint, bucket=None, repo=None, key=None,secret=None):
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
        self.driver = awscli.clidriver.create_clidriver()
        self.endpoint = endpoint
        self.bucket = bucket
        self.repo = repo
        self.yidb = YidbClient(endpoint)

        # load the command definition
        path = os.path.dirname(inspect.getfile(self.__class__))

        self.regions = []
        if (key):
            os.environ["AWS_ACCESS_KEY_ID"] = key
            os.environ["AWS_SECRET_ACCESS_KEY"] = secret

    def u2s(self, unicode):
        "convert unicode string to string"
        return unicodedata.normalize('NFKD', unicode).encode('ascii','ignore')

    def call_cli(self, args):
        "call aws cli to execuate the args command"
        result_string = ""
        try:
            old_stdout = sys.stdout
            result = StringIO()
            sys.stdout = result
            self.driver.main(args)
            sys.stdout = old_stdout
            result_string = result.getvalue()
        except:
            print "Unexpected error:", sys.exc_info()[0]
        finally:
            return result_string

    def sync(self):
        "sync billing line item for Instance usage"
        args = ['s3', 'ls', self.bucket]
        r = self.call_cli(args)
        num = 0
        for line in r.split("\n"):
            items = re.split(" +", line)
            if len(items) > 3:
                numstr = items[0].replace("-","") + items[1].replace(":","")
                if (num < int(numstr) and "billing-detailed-line-items-with-resources-and-tags" in items[3]):
                    file = items[3]
                    num = int(numstr)
        local_file = "/tmp/" + file[:len(file)-4]
        args = ['s3','cp', "s3://" + self.bucket + "/" + file, local_file]
        #r = self.call_cli(args)
        self.unzip("/tmp/" + file, "/tmp")
        f = open( local_file, 'r' )
        reader = csv.DictReader( f, fieldnames = ( "InvoiceID","PayerAccountId","LinkedAccountId","RecordType","RecordId","ProductName","RateId","SubscriptionId","PricingPlanId","UsageType","Operation","AvailabilityZone","ReservedInstance","ItemDescription","UsageStartDate","UsageEndDate","UsageQuantity","Rate","Cost","ResourceId" ) )
        skip_first = False
        for row in reader:
            if (skip_first == False):
                skip_first = True
            else:
                print row
        pass

