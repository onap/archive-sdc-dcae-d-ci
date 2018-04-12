#!python -u

import sys
import json


def get_unique_id(str_json):
  data = json.loads(str_json)
  str_metadata_value = data["properties"]["metadata"][0]["value"]
  metadata_value = json.loads(str_metadata_value)
  return metadata_value["uniqueId"]


for line in sys.stdin:
  print get_unique_id(line)