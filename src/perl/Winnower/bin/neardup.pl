#!/opt/ActivePerl-5.8/bin/perl -CSD
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
use Winnow::Winnower;
use Winnow::Overlap;
use Winnow::DB;
use Time::HiRes qw(tv_interval gettimeofday);

my $textFile = $ARGV[0];
my $id = $ARGV[1];

if (!defined $textFile || !defined $id) {
    print "Usage: $0 textFile ID\n";
    exit;
}

my $database = new Winnow::DB;
my $overlap = new Winnow::Overlap($database);

my ($rFingerprints, $nPrints) = Winnow::Winnower::fingerprintDoc($textFile);
#for (my $i = 10; $i<2000; $i++) {
my $t0 = [gettimeofday];
my ($isExact, $rMatchSet) = $overlap->getOverlaps($id, $rFingerprints);
if ($isExact>0) {
    print "exact match\n";
}
my $elapsed = tv_interval($t0, [gettimeofday]);
print "TIME $id: $elapsed\n";
#}

undef $database;

foreach my $match (@$rMatchSet) {
    my ($indicator, $id1, $ratio1, $id2, $ratio2) = @$match;
    print "$indicator, $id1, $ratio1, $id2, $ratio2\n";
}

#my @sent = @$rFingerprints;
#my $N = $#sent+1;
#print "SENT COUNT: $N\n";
#foreach my $sent (@$rFingerprints) {
#  my @fp = @$sent;
#  print "SENTENCE: ".(join " ", @fp)."\n";
#}


