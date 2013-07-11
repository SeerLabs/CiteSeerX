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
#
# Isaac Councill, 7/31/07
#
exit(0) if fork;
use strict;
#use SOAP::Lite +trace=>'all';
use SOAP::Transport::HTTP;
use FindBin;
use lib "$FindBin::Bin/../lib";
use DocFilter::Config;
use DocFilter::CustSerializer;
use Log::Log4perl qw(get_logger :levels);

$SIG{'PIPE'} = $SIG{'INT'} = 'IGNORE';

open(STDERR, ">>$FindBin::Bin/../docfilter.err");

my $serverUrl = $DocFilter::Config::serverUrl;
my $serverPort = $DocFilter::Config::serverPort;

my $daemon = SOAP::Transport::HTTP::Daemon
    ->new ('LocalAddr' => $serverUrl, 'LocalPort' => $serverPort)
    ->dispatch_to('Filter');

## Initialize Logging
my $logger = get_logger("DocFilter");
$logger->level($INFO);
my $appender = Log::Log4perl::Appender
    ->new("Log::Dispatch::File",
	  filename => "$FindBin::Bin/../docfilter.log",
	  mode => "append",
	  );
my $layout = Log::Log4perl::Layout::PatternLayout
    ->new("%d %p> %F{1}:%L - %m%n");
$appender->layout($layout);
$logger->add_appender($appender);

$logger->info("Server started at ".$daemon->url);

$daemon->handle;


##
# Request Handler
##
package Filter;
use FindBin;
use lib "$FindBin::Bin/../lib";
use DocFilter::Filter;
use DocFilter::Config;
use Time::HiRes qw(tv_interval gettimeofday);
use Log::Log4perl qw(get_logger);

sub filter {
    my ($class, $textFile, $repositoryID) = @_;

    my $logger = get_logger("DocFilter");
    my $t0 = [gettimeofday];

    my $repositoryLocation;

    if ($repositoryID ne "LOCAL") {
	$repositoryLocation =
	    $DocFilter::Config::repositories{$repositoryID};
	if (!defined $repositoryLocation) {
	    my $msg = "Unknown repository: $repositoryID";

	    $logger->error($msg);
	    die_with_docfilterfault('Sender', $msg);

	} else {
	    $textFile = "$repositoryLocation/$textFile";
	}
    }

    if (! -e $textFile) {
	my $msg = "File does not exist: $textFile";
	$logger->error($msg);
	die_with_docfilterfault('Sender', $msg);
    }
    if (-d $textFile) {
	my $msg = "Specified file is a directory: $textFile";
	$logger->error($msg);
	die_with_docfilterfault('Sender', $msg);
    }

    my ($sysStatus, $filterStatus, $msg) =
	DocFilter::Filter::filter($textFile);
    if ($sysStatus > 0) {

	my $elapsed = tv_interval($t0, [gettimeofday]);
	$logger->info("filter: $elapsed");

	return SOAP::Data->name('status' => $filterStatus),
	SOAP::Data->name('msg' => $msg);

    } else {
	$logger->error($msg);
	die_with_docfilterfault('Receiver', $msg);
    }

} # filter;


sub die_with_docfilterfault {
    my ($faultcode, $msg) = @_;

    my $uri = $DocFilter::Config::URI;
    my $obj = SOAP::Data
	->name('DocFilterFault' =>
	       \SOAP::Data->value(SOAP::Data->name('message' => $msg)))
	->uri($uri);

    my $serverURL = $DocFilter::Config::serverURL;

    die SOAP::Fault
	->faultcode($faultcode)
	->faultstring($msg)
	->faultdetail($obj)
	->faultactor($serverURL);

} # die_with_docfilterfault


1;
