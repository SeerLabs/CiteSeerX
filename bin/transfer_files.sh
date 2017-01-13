#!/bin/sh
#useage: sh ingestone.sh [directory to import]
# e.g., sh ingestone.sh ext20140916
START=$(date +%s)
STARTH=$(date)
BATCHID=`date +%Y-%m-%d`

# specify the set number from command line
setn=$1

# copy the extraction results from extraction machine
echo "rsync -arvz citeseerx@csxextraction02.ist.psu.edu:/data/exports/$setn /data/ingest/"
rsync -arvz citeseerx@csxextraction02.ist.psu.edu:/data/exports/$setn /data/ingest/
echo "done: rsync -arvz citeseerx@csxextraction02.ist.psu.edu:/data/exports/$setn /data/ingest/"

# build the xml files:
echo "./createXML.pl /data/ingest/$setn"
./createXML.pl /data/ingest/$setn
echo "done: ./createXML.pl /data/ingest/$setn"

# timing ends here
END=$(date +%s)
ENDH=$(date)
DIFF=$(( $END - $START ))
echo "Job ID: $setn"
echo "Started at: $STARTH. "
echo "  Ended at: $ENDH." 
echo "  Job took: $DIFF seconds."
