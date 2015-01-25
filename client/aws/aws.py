''' aws client will read the aws_resource configuration and post all the
resource data to CMS in a batch mode.
'''
import sys
import awscli.clidriver
from StringIO import StringIO
from jsonpath_rw import jsonpath, parse
import json
import requests
import unicodedata
import os

driver = awscli.clidriver.create_clidriver()

# load the configuration
config = json.load(open("aws_config.json"))
# load the command definition
resource_str = open('aws_resource.json').read()
resources = json.loads(resource_str)
regions = []

def u2s(unicode):
    "convert unicode string to string"
    return unicodedata.normalize('NFKD', unicode).encode('ascii','ignore')

def call_cli(args):
    "call aws cli to execuate the args command"
    result_string = ""
    try:
        old_stdout = sys.stdout
        result = StringIO()
        sys.stdout = result
        driver.main(args)
        sys.stdout = old_stdout
        result_string = result.getvalue()
    except:
        print "Unexpected error:", sys.exc_info()[0]
    finally:
        if result_string == "":
            return {}
        else:
            return json.loads(result_string)

def get_region():
    "get all aws regions"
    args = ['ec2','describe-regions']
    return call_cli(args)

def process_object(class_name, json_array):
    "process on one object in the cloud, post to CMS"
    headers = {"Content-Type" : "Application/json"}
    url = config['cms_endpoint'] + config['topo_repo'] + "/branches/main/topo/" + class_name
    print (class_name + "\n" + json.dumps(json_array, indent=4, sort_keys=True))
    r = requests.post(url,data=json.dumps(json_array), headers=headers)
    return r.json()

def process_class(class_name, args, listPath=None):
    "process on one class"
    args1 = list(args)
    while True:
        response_json = call_cli(args1)
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
                            process_object(class_name, node2.value)
                else:
                    process_object(class_name, node.value)

        else:
            process_object(class_name, response_json)

        if ('NextToken' in response_json):
            args1 = list(args)
            args1.append("--starting-token")
            args1.append(u2s(response_json['NextToken']))
        else:
            break

def main(repo=None,key=None,secret=None):
    if (key):
        os.environ["AWS_ACCESS_KEY_ID"] = key
        os.environ["AWS_SECRET_ACCESS_KEY"] = secret
    if (repo):
        config['topo_repo'] = repo

    regions = get_region()
    for resource in resources:
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
            for region in regions['Regions']:
                r_args = list(args)
                r_args.append("--region")
                region_name = u2s(region['RegionName'])
                r_args.append(region_name)
                process_class(resource['className'],r_args, list_path)
        else:
            process_class(resource['className'],args, list_path)

if __name__ == '__main__':
    main()
