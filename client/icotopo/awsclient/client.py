from StringIO import StringIO
import sys
import json
import awscli.clidriver

class AwsClient:
    def __init__(self):
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


