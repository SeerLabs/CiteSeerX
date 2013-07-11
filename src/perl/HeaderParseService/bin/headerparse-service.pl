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
exit(0) if fork;

use utf8;
#use SOAP::Lite +trace=>'debug';
use SOAP::Transport::HTTP;
use FindBin;
use lib "$FindBin::Bin/../lib";
use HeaderParse::Config::API_Config;
use vars qw($ServerURL $ServerPort);
use Log::Log4perl qw(get_logger :levels);

$SIG{'PIPE'} = $SIG{'INT'} = 'IGNORE';

open(STDERR, ">>$FindBin::Bin/../headerparse.err");

my $daemon = SOAP::Transport::HTTP::Daemon
    ->new (LocalAddr => "$ServerURL" , LocalPort => $ServerPort)
    ->dispatch_to(Parser);

## Initialize Logging
my $logger = get_logger("HeaderParse");
$logger->level($INFO);
my $appender = Log::Log4perl::Appender
    ->new("Log::Dispatch::File",
	  filename => "$FindBin::Bin/../headerparse.log",
	  mode => "append",
	  );
my $layout = Log::Log4perl::Layout::PatternLayout
    ->new("%d %p> %F{1}:%L - %m%n");
$appender->layout($layout);
$logger->add_appender($appender);

$logger->info("Server started at ".$daemon->url);

$daemon->handle;


##
# Request Handler for the Header Parse Service
##
package Parser;

use FindBin;
use lib "$FindBin::Bin/../lib";
use HeaderParse::API::Parser;
use HeaderParse::Config::API_Config;
use vars qw($ServerURL $offlineD);
use Time::HiRes qw(tv_interval gettimeofday);
use Log::Log4perl qw(get_logger);

sub parseHeader{
    my ($class, $filePath, $repositoryID) = @_;

    my $logger = get_logger("HeaderParse");
    my $t0 = [gettimeofday];

    if ($repositoryID ne "LOCAL") {
	my $repositoryLocation =
	    $HeaderParse::Config::API_Config::repositories{$repositoryID};
	if (!defined $repositoryLocation) {
	    my $msg = "Unknown repository: $repositoryID";

	    $logger->error($msg);
	    die_with_headerfault('Sender', $msg);

	} else {
	    $filePath = "$repositoryLocation/$filePath";
	}
    }

    if (! -e $filePath) {
	my $msg = "File does not exist: $filePath";
	$logger->error($msg);
	die_with_headerfault('Sender', $msg);
    }
    if (-d $filePath) {
	my $msg = "Specified file is a directory: $filePath";
	$logger->error($msg);
	die_with_headerfault('Sender', $msg);
    }

    my $jobID;

    # Obtain a random job ID that will serve as the prefix for the temporary
    # files generated during the SVM classification
    while($jobID = rand(time)){
        unless(-f $offlineD."$jobID"){
	    last;
	}
    }

    my ($status, $msg, $rxml) =
	&HeaderParse::API::Parser::_parseHeader($filePath, $jobID);
    if ($status <= 0) {
	$logger->error($msg);
	die_with_headerfault($msg);
    }
    my $elapsed = tv_interval($t0, [gettimeofday]);
    $logger->info("parseHeader: $elapsed");

    return SOAP::Data->name('headerInfo')->value($$rxml);

}  # parseHeader


sub die_with_headerfault {
    my ($faultcode, $msg) = @_;
    my $obj = SOAP::Data
	->name('SVMHeaderParseFault' =>
	       \SOAP::Data->value(SOAP::Data->name('message' => $msg)))
	->uri('http://citeseerx.org/algorithms/svm-header-parse/wsdl');

    die SOAP::Fault
	->faultcode($faultcode)
	->faultstring($msg)
	->faultdetail($obj)
	->faultactor($serverURL);
}


1;
