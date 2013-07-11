#!/usr/bin/perl -CSD
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
# Simple command script for executing SVMHeaderParse in an
# offline mode (direct API call instead of going through the
# web service).
#
# Isaac Councill, 08/30/07
#
use strict;
use utf8;
use FindBin;
use lib "$FindBin::Bin/../lib";
use HeaderParse::API::Parser;
use HeaderParse::Config::API_Config;
use vars qw( $offlineD);

my $textFile = $ARGV[0];
my $outFile = $ARGV[1];

if (!defined $textFile) {
    print "Usage: $0 textFile [outFile]\n";
    exit;
}

# Obtain a random job ID that will serve as the prefix for the temporary
# files generated during the SVM classification
my $jobID;
while($jobID = rand(time)){
    unless(-f $offlineD."$jobID"){
	last;
    }
}

my ($status, $msg, $rXML)
    = &HeaderParse::API::Parser::_parseHeader($textFile, $jobID);

if ($status <= 0) {
    print STDERR "ERROR processing $textFile: $msg\n";
    exit;
}

if (defined $outFile) {
    open (OUT, ">$outFile") or die "Could not open $outFile for writing: $!";
    print OUT $$rXML;
    close OUT;
} else {
    print $$rXML;
}
