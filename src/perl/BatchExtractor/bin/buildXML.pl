#!/usr/bin/perl -CSD
use strict;
use Encode;

my $repository = "labseer";
my $repositoryPath = "/export/csx-data";

my ($importDir, $start, $end) = @ARGV;
if (!$importDir || !$start || !$end) {
    print "Usage: $0 importDir start end\n";
    exit;
}

my $relDir = $importDir;
$relDir =~ s|^$repositoryPath/||;

my $count = 0;
for (my $i=$start; $i<=$end; $i++) {
    if (! -e "$importDir/$i.txt") {
	next;
    }
    my $filePath = $relDir;
    if ( -e "$importDir/$i.pdf") {
	$filePath .= "/$i.pdf";
    } elsif ( -e "$importDir/$i.PDF") {
	$filePath .= "/$i.PDF";
    } elsif ( -e "$importDir/$i.ps") {
	$filePath .= "/$i.ps";
    } elsif ( -e "$importDir/$i.PS") {
	$filePath .= "/$i.PS";
    } else {
	print "$i: no pdf or ps\n";
	next;
    }
    my $xml = "<document id=\"unset\">\n";
    open(IN, "<:utf8", "$importDir/$i.file") or next;
    $xml .= "<fileInfo>\n";
    $xml .= "<repository>$repository</repository>\n";
    $xml .= "<filePath>$filePath</filePath>\n";
    $xml .= "<bodyFile>$relDir/$i.body</bodyFile>\n";
    $xml .= "<citeFile>$relDir/$i.cite</citeFile>\n";
    while(<IN>) {
	if (m/xml version/) {
	    next;
	}
	s/checksum/checkSum/g;
	$xml .= $_;
    };
    close IN;
    $xml .= "</fileInfo>\n";
    open(IN, "<:utf8", "$importDir/$i.header") or next;
    while(<IN>) {
	$xml .= $_;
    }
    close IN;
    open(IN, "<:utf8", "$importDir/$i.parscit") or next;
    while(<IN>) {
	$xml .= $_;
    }
    close IN;

    $xml .= "</document>\n";

    open (XML, ">:utf8", "$importDir/$i.xml")
	or die "$i: could not open xml file for writing";
    print XML $xml;
    close XML;

    if (($i%1000)==0) {
	print "processed $i\n";
    }
}
