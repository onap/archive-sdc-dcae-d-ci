#!python -u

import sys
import json


uniques = set()
for line in sys.stdin:
  uniques.add(line)

for item in uniques:
  print item,