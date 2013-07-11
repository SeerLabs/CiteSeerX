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
use DBI;

my $output_dir = $ARGV[1];
my $dbuser = $ARGV[2];
my $dbpass = $ARGV[3];
my $start = $ARGV[4];
my $end   = $ARGV[5];

if (!defined $output_dir || !defined $dbuser || !defined $dbpass) {
    print "Usage: $0 output_dir dbuser dbpass [start] [end]\n";
    exit;
}

my $output_dir = "/home/isaac/data/reparse";
my $repository_path = "/repositories/rep1";

my $dbh = DBI->connect("DBI:mysql:mysql_socket=/tmp/mysql.sock;database=csx_citegraph", $dbuser, $dbpass);
my $getIDs = $dbh->prepare("select id from papers where id>? and id<=? limit 100");

my $lastID = 0;
my $counter = 0;

if (defined $start) {
    $lastID = $start;
}
if (!defined $end) {
    ($end) = $dbh->selectrow_array("select max(id) from papers");
}

while(1) {
    my $count = $getIDs->execute($lastID, $end);
    if ($count == 0) {
	last;
    }
    my $table = $getIDs->fetchall_arrayref;
    foreach my $row (@$table) {
	$lastID = $row->[0];
	my $inFile = buildFilePath($lastID,$repository_path);
	my $outFile = "$output_dir/$lastID.xml";
	parse($inFile, $outFile);
	$counter++;
    }
    if (($counter%1000)==0) {
	print "processed $counter\n";
    }
}

$getIDs->finish;
$dbh->disconnect;


sub buildFilePath {
    my ($doi,$path_prefix) = @_;
    my $relPath = $doi;
    $relPath =~ s/\./\//g;
    $relPath .= "/$doi.txt";
    return "$path_prefix/$relPath";
}

sub parse {
    my ($textFile, $outFile) = @_;

    if (!defined $textFile) {
	print "Usage: $0 textFile [outFile]\n";
	return;
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
	return;
    }

    if (defined $outFile) {
	open (OUT, ">$outFile") or die "Could not open $outFile for writing: $!";
	print OUT $$rXML;
	close OUT;
    } else {
	print $$rXML;
    }
}
