#!/usr/bin/perl
use strict;
use SOAP::Lite +trace=>'debug';
use MIME::Base64;
use FindBin;

my ($url, $type) = @ARGV;
if (!$url) {
    print "Usage: $0 serviceEndpoint [type]\n".
        "where type is an optional integer input.\n";
    exit;
}
if (!$type) {
    $type = 1;
} elsif ($type !~ m/^\d+$/) {
    print "WARNING: Type must be an integer! Backing off to a value of 1\n";
    $type = 1;
}


my $doiService = SOAP::Lite
    ->default_ns('http://doi.citeseerx.psu.edu/xsd')
    ->uri('http://doi.citeseerx.psu.edu/xsd')
    ->proxy($url)
    ->on_fault(
	       sub {
		   my($soap, $res) = @_;
		   die $res->faultcode, ": ", $res->faultstring;
	       });
my $data = SOAP::Data->name('doiType' => $type);
my $som = $doiService->getDOI($data);
print "Response: ", $som->result,  "\n";
