'''
This aws client will read the aws_resource configuration and post all the
resource data to CMS in a batch mode.
'''
from jsonpath_rw import jsonpath, parse
import json
import unicodedata
import os
import logging
from  icotopo.yidbclient.client import YidbClient
from icotopo.awsclient.client import AwsClient
import threading

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

    def process_object(self, class_name, json_array):
        "process on one object in the cloud, post to CMS"
        if (len(json_array) == 0):
            return
        return self.yidb.post_service_model(self.repo, class_name, json_array, "ec2")

        self.logger.info(str(r.json()))
        return r.json()

    def process_class(self, class_name, args, listPath=None):
        "process on one class"
        args1 = list(args)
        while True:
            response_json = self.aws.call_cli(args1)
            if (listPath):
                jsonpath_expr = parse(listPath[0])
                node_list = jsonpath_expr.find(response_json)
                for node in node_list:
                    response_json = node.value
                    if (len(listPath )> 1 and len(response_json) > 0):
                        jsonpath_expr = parse(listPath[1])
                        for first_node_list in response_json:
                            node_list2 = jsonpath_expr.find(first_node_list)
                            for node2 in node_list2:
                                self.process_object(class_name, node2.value)
                    else:
                        self.process_object(class_name, node.value)

            else:
                self.process_object(class_name, response_json)

            if ('NextToken' in response_json):
                args1 = list(args)
                args1.append("--starting-token")
                args1.append(self.u2s(response_json['NextToken']))
            else:
                break

    def sync(self):
        for resource in self.resources:
            print resource
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

            if (resource['useRegion']):
                for region in self.regions['Regions']:
                    r_args = list(args)
                    r_args.append("--region")
                    region_name = self.u2s(region['RegionName'])
                    r_args.append(region_name)
                    self.process_class(resource['className'], r_args, list_path)
            else:
                self.process_class(resource['className'],args, list_path)

