#!/usr/bin/env python3

import os, re

for mdfile in os.listdir('./'):
  if mdfile.endswith('.md'):
    content = open(mdfile, 'rt').read()
    newcontent = re.sub(r'\[([^]]+)\]\(([^)]+)\)', r'[\1](\2.md)', content)
    open(mdfile, 'wt').write(newcontent)

