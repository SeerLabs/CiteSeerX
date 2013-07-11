#!/bin/bash
START=$(date +%s)
# do something
# start your script work here
sudo ./cde.py
# your logic ends here
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "It took $DIFF seconds"


