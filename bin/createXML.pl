#!/usr/bin/perl -CSD
use strict;
use Encode;
use File::Spec;


my $repository = "rep1";

my ($importDir) = @ARGV;
if (!$importDir) {
    print "Usage: $0 importDir\n";
    exit;
}

sub fileWithoutExtension($) {
	my $fileName = shift;
	my ($volume,$directories,$file) = File::Spec->splitpath( $fileName );
	$file=~s/(.*?)\.(.*)$/$1/;
	return $file;
}

my $count = 0;
foreach my $i (<$importDir/*.txt>) {
    if (! -e $i) {
	print "$i does not exist !\n";
	next;
    }
    my $docID = &fileWithoutExtension($i);
    my $pdffile = $i; my $PDFfile = $i; my $PSfile=$i; my $psfile=$i;
    $pdffile=~s/\.txt$/\.pdf/;
    $PDFfile=~s/\.txt$/\.PDF/;
    $PSfile=~s/\.txt$/\.PS/;
    $psfile=~s/\.txt$/\.ps/;

    my $filePath = "";
    if( -e $pdffile) {
	$filePath = $pdffile;
    }
    elsif(-e $PDFfile) {
	$filePath = $PDFfile;
    }
    elsif(-e $PSfile) {
        $filePath = $PSfile;
    }
    elsif(-e $psfile) {
	$filePath = $psfile;
    }
    else {
	#print "$docID: no pdf or ps\n";
	next;
    }
    my $xml = "<document id=\"unset\">\n";
    open(IN, "<:utf8", "$importDir/$docID.file") or next;
    $xml .= "<fileInfo>\n";
    $xml .= "<repository>$repository</repository>\n";
    $xml .= "<filePath>$filePath</filePath>\n";
    $xml .= "<bodyFile>$importDir/$docID.body</bodyFile>\n";
    $xml .= "<citeFile>$importDir/$docID.cite</citeFile>\n";
    while(<IN>) {
	if (m/xml version/) {
	    next;
	}
	s/checksum/checkSum/g;
	$xml .= $_;
    };
    close IN;
    $xml .= "</fileInfo>\n";
    open(IN, "<:utf8", "$importDir/$docID.header") or next;
    while(<IN>) {
	$xml .= $_;
    }
    close IN;
    open(IN, "<:utf8", "$importDir/$docID.parscit") or next;
    while(<IN>) {
	$xml .= $_;
    }
    close IN;

    $xml .= "</document>\n";

    open (XML, ">:utf8", "$importDir/$docID.xml")
	or die "$docID: could not open xml file for writing";
    print XML $xml;
    close XML;
}
