#!/bin/sh

cd /usr/local/kongliangzhong/yr/static/mongodb/mongodb-2.0.1

./mongo -host dd02:6000 <<EOF
  use comitium
  db.reviews.count()
  DBQuery.shellBatchSize = 4000
  db.reviews.find({}, {"fp" : 1})._addSpecial("$maxscan", 4000).limit(4000).sort({"fp" : -1})
EOF

echo "<<<<<<<<<<<<<<<<<<<<<<<end"
	

