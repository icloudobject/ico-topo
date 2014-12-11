ico-topo
========
This project injects the EC2 topology into YiDB/MongoDB. 

EC2 data model (modelled by EC2 command line JSON output using describeXYZ API) is converted into YiDB data graph using configuration mapping files defined in aws/mapping folder. 

# Steps to run ico-topo at your local environment

1. Run ico-topo server

        cd server
        mvn jettry:run

2. Init the ico client
   
        set environment variable for AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
        cd client
        python aws-init.py
        cd aws
        python aws.py

3. Browse the inserted ec2 instances at: http://localhost:9090/cms/repositories/topocms/branches/main/Instance

  
