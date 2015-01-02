"""
This is a very simple Python interface to YiDB Service using requests
www.yidb.org
"""

import requests
import json
import urllib

class YidbClient ():
    "A simple YiDB client"
    def __init__(self, endpoint):
        self.endpoint = endpoint

    def delete_all_metadata(self, repo):
        "delete all schema definition for the repo"
        response = requests.get(self.endpoint + "/repositories/" + repo + "/metadata").json()
        result = response['result']
        metadata_list = []
        for metadata_def in result:
            #print r['name']
            if (metadata_def['name'] != 'History' and metadata_def['name'] != 'Branch'):
                metadata_list.append(metadata_def['name'])
        has_delete_error = True
        while has_delete_error:
            has_delete_error = False
            for class_name in metadata_list:
                r = requests.delete(self.endpoint + "/repositories/"  + repo + "/metadata/" + class_name)
                if 'status' in r and r['status']['code'] != "200":
                    has_delete_error = True

    def upsert_repo(self, repo):
        "create repo if not exists"
        response = requests.get(self.endpoint + "/repositories/" + repo)
        if (response.status_code != 200):
            payload = {}
            payload['repositoryName'] = repo
            return self.post_change(self.endpoint + "/repositories/", payload)
        else:
            return response

    def post_change(self, url, payload, query_string = None):
        " post the change to cms for repo creation and schema push"
        headers = {"Content-Type" : "Application/json"}
        if query_string:
            q = ""
            for item in query_string:
                q = q + item + "=" + query_string[item] + "&"
            return requests.post(url + "?" + q,data=json.dumps(payload), headers=headers)
        else:
            return requests.post(url,data=json.dumps(payload), headers=headers)

    def delete(self, repo, class_name, oid):
        response = requests.delete(self.endpoint + "/repositories/" + repo + "/branches/main/" + class_name + "/" + oid)
        return response

    def upsert_metadata(self, repo,class_name, payload):
        "check to see if Mapping schema had been defined"
        response = requests.get(self.endpoint + "/repositories/" + repo + "/metadata/" + class_name)
        if (response.status_code == 200):
            url = self.endpoint + "/repositories/" + repo + "/metadata/" + class_name
        else:
            url = self.endpoint + "/repositories/" + repo + "/metadata/"
        return self.post_change(url,payload)

    def insert_object(self, repo, class_name, payload):
        "upsert an object into repo"
        url = self.endpoint + "/repositories/" + repo + "/branches/main/" + class_name
        return self.post_change(url,payload)

    def upsert_object(self, repo, class_name, payload):
        "upsert an object into repo"
        url = self.endpoint + "/repositories/" + repo + "/branches/main/" + class_name + "/" + payload['_oid']
        response = requests.get(url)
        if (response.status_code != 200):
            url = self.endpoint + "/repositories/" + repo + "/branches/main/" + class_name
        return self.post_change(url,payload)

    def post_service_model(self, repo, class_name, payload, client_id):
        "upsert a object into repo"
        query_string = {}
        query_string["topoClientId"] = client_id
        url = self.endpoint + "/repositories/" + repo + "/branches/main/topo/" + class_name
        return self.post_change(url,payload,query_string)

    def init_metadata_def(self, repo, class_name):
        "init a dummy class first for reference"
        response = requests.get(self.endpoint + "/repositories/" + repo + "/metadata/" + class_name).json()
        if (response['status']['code'] == "404"):
            url = self.endpoint + "/repositories/"  + repo + "/metadata"
            payload = {}
            payload['name'] = class_name
            return self.post_change(url,payload)

    def refresh_metadata(self,repo):
        "refresh metadata in the repo"
        url = self.endpoint + "/repositories/" + self.repo + "/metadata"
        return requests.get(url)

    def query(self,repo, query, query_param=None):
        "query the repo using YiDB query syntax"
        query = urllib.quote_plus(query)
        query = query.replace("+","%20")
        url = self.endpoint + "/repositories/" + repo + "/branches/main/query/" + query + "?allowFullTableScan=True&maxFetch=200000&" + query_param
        return requests.get(url)

    def enable_delete(self):
        payload = {}
        payload['MetadataDelete'] = True
        payload['SysAllowRepositoryDelete'] = True
        return self.post_change(self.endpoint + "/config/", payload)
