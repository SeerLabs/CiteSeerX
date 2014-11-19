#!/usr/bin/perl -CSD
use strict;
use Encode;
use File::Spec;


my $repository = "rep1";
my $repositoryPath = "/export/csx-data";

my ($importDir) = @ARGV;
if (!$importDir) {
    print "Usage: $0 importDir\n";
    exit;
}

sub fileWithoutExtension($) {
	my $fileName = shift;
	my ($volume,$directories,$file) = File::Spec->splitpath( $fileName );
        # this is Pradeep's solution, but it mistakenly convert 000.000.000.pdf to 000.pdf
        # it returns $file, but I return $without_extension -- J. Wu@2014-08-17
	#$file=~s/(.*?)\.(.*)$/$1/;
        (my $without_extension = $file) =~ s/\.[^.]+$/$1/;
	return $without_extension;
}

my $count = 0;
my @txtfiles = <$importDir/*.txt>;
my $ntxtfiles = @txtfiles;
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

    print "creating XML for $i\n";
    print "docID=$docID\n";
    print "pdffile=$pdffile\n";
    print "PDFfile=$PDFfile\n";
    print "PSfile=$PSfile\n";
    print "psfile=$psfile\n";

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

    $count=$count+1;
    print "$count/$ntxtfiles - write xml file: ","$importDir/$docID.xml\n";
    open (XML, ">:utf8", "$importDir/$docID.xml")
	or die "$docID: could not open xml file for writing";
    print XML $xml;
    close XML;
}
