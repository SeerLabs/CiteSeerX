#!/bin/sh
START=$(date +%s)
STARTH=$(date)

# specify the set number separated by spaces
declare -a sets=(ex20150606)

for set in ${sets[@]}
do
    # batch import the set
    echo "./batchImport /data/ingest/$set"
    ./batchImport /data/ingest/$set
done

# timing ends here
END=$(date +%s)
ENDH=$(date)
DIFF=$(( $END - $START ))
echo "Job ID(s): " ${sets[@]}
echo "Started: $STARTH"
echo "Ended: $ENDH"
echo "Job took: $DIFF seconds."
