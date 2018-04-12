#!python -u

import sys
import json


def is_vfcmt(str_json):
  data = json.loads(str_json)
  try:
    resource_type = data["properties"]["resourceType"]
    value = resource_type[0]["value"]
    return value == "VFCMT"
  except:
    return False


for line in sys.stdin:
  if is_vfcmt(line):
    print line,