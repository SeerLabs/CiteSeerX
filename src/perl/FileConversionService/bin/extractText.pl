#!/usr/bin/perl
#
# Copyright 2007 Penn State University
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#     http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
# Command script for executing FileConverter in an offline mode
# (direct API call instead of going through the web service).
#
# Isaac Councill, 09/06/07
#
use strict;
use FindBin;
use lib "$FindBin::Bin/../lib";
use FileConverter::Controller;

my $filePath = $ARGV[0];

if (!defined $filePath) {
    print "Usage: $0 filePath\n";
    exit;
}

my ($status, $msg, $textFile, $rTrace, $rCheckSums) =
    FileConverter::Controller::extractText($filePath);

if ($status <= 0) {
    print STDERR "Error: $msg\n";
} else {
    print "TEXTFILE: $textFile\n";
    print "TRACE: ";
    print (join ",", @$rTrace);
    my @checkSums = @$rCheckSums;
    print "\n";
    print "<checksums>\n";
    foreach my $checkSum (@checkSums) {
        print "<checksum>\n";
        print "<fileType>".$checkSum->getFileType()."</fileType>\n";
        print "<sha1>".$checkSum->getSHA1()."</sha1>\n";
        print "</checksum>\n";
    }
    print "</checkSums>\n";

}
