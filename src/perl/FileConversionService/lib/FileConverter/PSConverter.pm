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
package FileConverter::PSConverter;
#
# Wrapper to execute the ps2pdf command-line tool for converting
# ps to PDF files.
#
# Juan Pablo Fernandez Ramirez, 10/08/07
#
use strict;
use FileConverter::Config;
use FileConverter::Utils;

my $timeout = 20;

##
# Execute the converter utility.
##
sub convertFile {
    my ($filePath, $rTrace, $rCheckSums) = @_;
    my ($status, $msg) = (1, "");
    
    my $pdfFilePath = FileConverter::Utils::changeExtension($filePath, "pdf");
    my @commandArgs = ("ps2pdf13", $filePath, $pdfFilePath);
    my $child;
    eval {
	local $SIG{'ALRM'} = sub { die "alarm\n" };
	alarm $timeout;
	$child = system(@commandArgs);
	alarm 0;
    };

    if ($@) {
	if ($@ eq "alarm\n") {
	    if (defined $child) { kill 9, $child; }
	    return (0, "ps2pdf timeout");
	}
    }
    
    if ($? == -1) {
        return (0, "Failed to execute ps2pdf: $!");
    } elsif ($? & 127) {
        return (0, "ps2pdf died with signal ".($? & 127));
    }

    my $code = $?>>8;
    if ($code == 0) {
        push @$rTrace, "ps2pdf";

	my $sha1 = FileConverter::CheckSum->new();
	$sha1->digest($filePath);
	push @$rCheckSums, $sha1;

        return ($status, $msg, $pdfFilePath, $rTrace, $rCheckSums);
    } else {
        return (0, "Error executing ps2pdf (code $code): $!");
    }
} # convertFile
1;
