from StringIO import StringIO
import sys
import json
import awscli.clidriver
import os
import threading

class AwsClient:
    def __init__(self, key, secret):

        env_key = os.environ['AWS_ACCESS_KEY_ID']
        env_secret = os.environ['AWS_SECRET_ACCESS_KEY']
        os.environ['AWS_ACCESS_KEY_ID'] = threading.local()
        os.environ['AWS_SECRET_ACCESS_KEY'] = threading.local()

        if (key):
            os.environ["AWS_ACCESS_KEY_ID"] = key
            os.environ["AWS_SECRET_ACCESS_KEY"] = secret
        else:
            os.environ["AWS_ACCESS_KEY_ID"] = env_key
            os.environ["AWS_SECRET_ACCESS_KEY"] = env_secret

        if not "AWS_ACCESS_KEY_ID" in os.environ:
            exit("No AWS_ACCESS_KEY_ID defined in env")

        self.driver = awscli.clidriver.create_clidriver()

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
            if result_string == "":
                return {}
            elif result_string[0] == "{":
                return json.loads(result_string)
            else:
                return result_string


