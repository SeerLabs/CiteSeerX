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
use strict;
use FindBin;
use lib "$FindBin::Bin/../lib";
use DocFilter::Filter;

my $textFile = $ARGV[0];

if (! defined $textFile) {
    print "Usage: $0 textFile\n";
    exit;
}

my ($sysStatus, $filterStatus, $msg) =
    DocFilter::Filter::filter($textFile);
if ($sysStatus > 0) {
    if ($filterStatus > 0) {
	print "document passed filtration\n";
    } else {
	print "document failed filtration\n";
    }
} else {
    print "An error occurred during filtration: $msg\n";
}
