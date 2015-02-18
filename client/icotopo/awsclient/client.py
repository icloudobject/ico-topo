from StringIO import StringIO
import sys
import json
import awscli.clidriver
import os

class AwsClient:
    def __init__(self, key, secret):

        if (key):
            os.environ["AWS_ACCESS_KEY_ID"] = key
            os.environ["AWS_SECRET_ACCESS_KEY"] = secret

        if not "AWS_ACCESS_KEY_ID" in os.environ:
            exit("No AWS_ACCESS_KEY_ID defined in env")

        self.driver = awscli.clidriver.create_clidriver()

    def call_cli(self, args):
        "call aws cli to execuate the args command"
        result_string = ""
        err_result_string = ""
        try:
            old_stdout = sys.stdout
            old_stderr = sys.stderr
            result = StringIO()
            sys.stdout = result
            err_result = StringIO()
            sys.stderr = err_result
            self.driver.main(args)
            sys.stdout = old_stdout
            sys.stderr = old_stderr
            result_string = result.getvalue()
            err_result_string = err_result.getvalue()
        except:
            print "Unexpected error:", sys.exc_info()[0]
        finally:
            if (result_string == "" and err_result_string != ""):
                raise Exception(err_result_string)
            if result_string == "":
                return {}
            elif result_string[0] == "{":
                return json.loads(result_string)
            else:
                return result_string


