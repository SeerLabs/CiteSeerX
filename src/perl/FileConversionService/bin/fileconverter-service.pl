#!/opt/ActivePerl-5.8/bin/perl
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
# Starts a web service using SOAP::Lite that handles requests
# for file conversion.  For message details, see the WSDL file
# in the wsdl/ directory of the FileConverter distribution.
#
# Input messages must include a pointer to the location of a
# file to be converted.  There must be local access to this
# file via a standard or networked file system.
#
# Isaac Councill, 09/07/07
#
exit(0) if fork;

use strict;
#use SOAP::Lite +trace=>'debug';
use SOAP::Transport::HTTP;
use FindBin;
use lib "$FindBin::Bin/../lib";
use FileConverter::Config;
use Log::Log4perl qw(get_logger :levels);

$SIG{'PIPE'} = $SIG{'INT'} = 'IGNORE';

open (STDERR, ">>$FindBin::Bin/../fileconv.err");

my $serverURL = $FileConverter::Config::serverURL;
my $serverPort = $FileConverter::Config::serverPort;

my $daemon = SOAP::Transport::HTTP::Daemon
    ->new ('LocalAddr' => $serverURL, 'LocalPort' => $serverPort)
    ->dispatch_to('FileConverter');

## Initialize Logging
my $logger = get_logger("FileConverter");
$logger->level($INFO);
my $appender = Log::Log4perl::Appender
    ->new("Log::Dispatch::File",
	  filename => "$FindBin::Bin/../fileconv.log",
	  mode => "append",
	  );
my $layout = Log::Log4perl::Layout::PatternLayout
    ->new("%d %p> %F{1}:%L - %m%n");
$appender->layout($layout);
$logger->add_appender($appender);

$logger->info("Server started at ".$daemon->url);

$daemon->handle;


##
# Service Module
#
# Passes control to the FileConverter::Controller module and provides
# a SOAP wrapping for the response.
#
##
package FileConverter;
use FindBin;
use lib "$FindBin::Bin/../lib";
use FileConverter::Controller;
use FileConverter::Config;
use Time::HiRes qw(tv_interval gettimeofday);
use Log::Log4perl qw(get_logger);


sub extractText {
    my ($class, $filePath, $repositoryID) = @_;

    my $logger = get_logger("FileConverter");
    my $t0 = [gettimeofday];

    my $repositoryLocation;

    if ($repositoryID ne "LOCAL") {
	$repositoryLocation =
	    $FileConverter::Config::repositories{$repositoryID};
	if (!defined $repositoryLocation) {
	    my $msg = "Unknown repository: $repositoryID";
	    $logger->error($msg);
	    die_with_fileconvfault('Sender', $msg);

	} else {
	    $filePath = "$repositoryLocation/$filePath";
	}
    }

    if (! -e $filePath) {
	my $msg = "File does not exist: $filePath";
	$logger->error($msg);
	die_with_fileconvfault('Sender', $msg);
    }
    if (-d $filePath) {
	my $msg = "Specified file is a directory: $filePath";
	$logger->error($msg);
	die_with_fileconvfault('Sender', $msg);
    }

    my ($status, $msg, $textFilePath, $rTrace) =
	FileConverter::Controller::extractText($filePath);

    if ($status > 0) {

	my $relTextPath = makeRelative($textFilePath, $repositoryLocation);
	my $trace = join ",", @$rTrace;
	my $elapsed = tv_interval($t0, [gettimeofday]);
	$logger->info("extractText: $elapsed");

	my $uri = $FileConverter::Config::URI;

	return SOAP::Data->name('filePath' =>
				$relTextPath),
	    SOAP::Data->name('conversionTrace' =>
			     $trace);

    } else {
	$logger->error($msg);
	die_with_fileconvfault('Receiver', $msg);
    }

} # extractText


sub die_with_fileconvfault {
    my ($faultcode, $msg) = @_;

    my $uri = $FileConverter::Config::URI;
    my $obj = SOAP::Data
	->name('FileConversionFault' =>
	       \SOAP::Data->value(SOAP::Data->name('message' => $msg)))
	->uri($uri);

    my $serverURL = $FileConverter::Config::serverURL;

    die SOAP::Fault
	->faultcode($faultcode)
	->faultstring($msg)
	->faultdetail($obj)
	->faultactor($serverURL);

} # die_with_fileconvfault


sub makeRelative {
    my ($fpath, $repLoc) = @_;
    if (!defined $repLoc || $repLoc =~ /^\s*$/) {
	return $fpath;
    }
    my $newPath = substr $fpath, length($repLoc);
    $newPath =~ s/^\/+//;
    return $newPath;

} # makeRelative


1;
