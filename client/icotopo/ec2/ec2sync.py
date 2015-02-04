'''
This aws client will read the aws_resource configuration and post all the
resource data to CMS in a batch mode.
'''
from jsonpath_rw import jsonpath, parse
import json
import unicodedata
import logging
from icotopo.yidbclient.client import YidbClient
from icotopo.awsclient.client import AwsClient
import uuid
import time

class EC2TopoSync ():

    def __init__(self, endpoint, config_path, repo=None, key=None, secret=None):
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
        self.endpoint = endpoint
        self.repo = repo
        self.yidb = YidbClient(endpoint)
        self.aws = AwsClient(key, secret)

        resource_str = open(config_path + '/resource.json').read()
        self.resources = json.loads(resource_str)
        self.regions = []

        self.regions = self.get_region()

    def u2s(self, unicode):
        "convert unicode string to string"
        return unicodedata.normalize('NFKD', unicode).encode('ascii','ignore')

    def get_region(self):
        "get all ec2 regions"
        args = ['ec2','describe-regions']
        return self.aws.call_cli(args)

    def process_object(self, region, class_name, json_array):
        "process on one object in the cloud, post to CMS"
        if (len(json_array) == 0):
            return
        if (region):
            for j in json_array:
                j['region'] = region
        r = self.yidb.post_service_model(self.repo, class_name, json_array, "ec2")

        self.logger.info(r.content)
        return r.json()

    def log_call(self, task_id, args, message):
        payload = {}
        payload['taskId'] = task_id
        payload['logTime'] = int(time.time())
        payload['command'] = str(args)
        payload['message'] = message
        r = self.yidb.insert_object(self.repo,"TopoTaskLog",payload)
        return r

    def process_class(self, task_id, region, class_name, args, listPath=None):
        "process on one class"
        args1 = list(args)
        while True:
            response_json = self.aws.call_cli(args1)
            if type(response_json) is dict:
                self.log_call(task_id, args1, "OK")
            else:
                self.log_call(task_id, args1, response_json)
                return
            if response_json == '[]':
                return
            if (listPath):
                jsonpath_expr = parse(listPath)
                matches = jsonpath_expr.find(response_json)
                for match in matches:
                    response_json = match.value
                    self.process_object(region, class_name, response_json)
            else:
                self.process_object(region, class_name, response_json)

            if ('NextToken' in response_json):
                args1 = list(args)
                args1.append("--starting-token")
                args1.append(self.u2s(response_json['NextToken']))
            else:
                break

    def sync(self, task_id=None):
        if (task_id == None):
            task_id = str(uuid.uuid4())
        payload = {}
        payload['taskId'] = task_id
        payload['clientId'] = "ec2"
        payload['startTime'] = int(time.time()*1000)
        r = self.yidb.insert_object(self.repo,"TopoTask",payload)

        for resource in self.resources:
            print resource
            self.sync_resource(task_id, resource)

        payload = {}
        payload['taskId'] = task_id
        payload['status'] = "DONE"
        payload['clientId'] = "ec2"
        payload['endTime'] = int(time.time()*1000)
        r = self.yidb.insert_object(self.repo,"TopoTask",payload)


    def sync_resource(self, task_id, resource, region=None, resource_id=None):
        args = ['ec2']
        command = str(resource['command'])
        if ('listPath' in resource):
            list_path = resource['listPath']
        else:
            list_path = None
        com_args = command.split(" ")
        args.extend(com_args)
        if ('max-items' in resource):
            args.append("--max-items")
            args.append(str(resource['max-items']))

        if (region == None):
            regions = self.regions['Regions']
        else:
            regions = [{"RegionName" : region}]
        if 'useRegion' in resource and resource['useRegion']:
            for region in regions:
                r_args = list(args)
                r_args.append("--region")
                region_name = self.u2s(region['RegionName'])
                r_args.append(region_name)
                if (resource_id):
                    r_args.append("--" + resource['id_name'])
                    r_args.append(resource_id)

                self.process_class(task_id, region_name, resource['className'], r_args, list_path)
        else:
            self.process_class(task_id, None, resource['className'], args, list_path)

