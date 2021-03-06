ico-topo
========
This project injects the EC2 configuration topology into YiDB (yidb.org)/MongoDB to create a graph view of your whole data center. It is intended to be a reference application on how to use YiDB. It is also an alternative of Amazon config (aws.amazon.com/config).

EC2 data model (modelled by EC2 command line JSON output using describeXYZ API) is converted into YiDB data graph using configuration mapping files defined in config folder. 

## How it works?

1. Retrieve the resource description, for example, the AvailabilityZone info:

        {
                "Messages": [], 
                "RegionName": "sa-east-1", 
                "State": "available", 
                "ZoneName": "sa-east-1a"
        }
        
2. Topo server converts the JSON service model into YiDB data model based on specification in mapping file. json-path is used to pick up the right value from the original service model.

        {
                "className" : "AvailabilityZone",
                "fields" : {
                        "id" : {
                                "path" : "$.ZoneName",
                                "dataType" : "string"
                        },
                        "region": {
                                "path" : "$.RegionName",
                                "class" : "Region"
                        }
                }
        }
        
3. The result model in YiDB graph representation will be the following JSON objects:
        
        AvailabilityZone:
        {
                "_oid" : "sa-east-1a",
                "region" : {
                        "_type" : "Region",
                        "_oid" : "sa-east-1"
                }
        }
        Region:
        {
                "_oid" : "sa-east-1"
        }


## Steps to run ico-topo at your local environment

1. Run ico-topo server

        cd server
        mvn jetty:run

2. Install AWS python client with

        pip install awscli

   Follow AWS CLI docs to configure the credentials, default region and output format: http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
   AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY must be configured correctly and the output format must be set as 'json'. The following config needs to be at ~/.aws/config folder.

        [default]
        output = json
        region = us-west-2


3. Install some packages used by ico python code

        pip install requests
	pip install jsonpath-rw

4. Init the ico client and run ec2 sync and s3 sync
   
        cd client
        python setup.py install	
        cd bin
        python init.py
        python ec2.py
        python s3.py

5. Browse the inserted ec2 instances at: http://localhost:9090/topo/repositories/topocms/branches/main/Instance


  
