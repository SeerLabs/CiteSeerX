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
package FileConverter::TET;
#
# Wrapper to execute the TET command-line tool for extracting
# text from PDF files.
#
# Isaac Councill, 09/06/07
#
use strict;
use FileConverter::Config;
use FileConverter::Utils;
use FileConverter::CheckSum;

my $TETPath = $FileConverter::Config::TETPath;
my $TETLicensePath = $FileConverter::Config::TETLicensePath;

$ENV{'PDFLIBLICENSEFILE'} = $TETLicensePath;

##
# Execute the TET utility.
##
sub extractText {
    my ($filePath, $rTrace, $rCheckSums) = @_;
    my ($status, $msg) = (1, "");

    if (FileConverter::Utils::checkExtension($filePath, "pdf") <= 0) {
	return (0, "Unexpected file extension at ".
		__FILE__." line ".__LINE__);
    }

    my $textFilePath =
	FileConverter::Utils::changeExtension($filePath, "txt");
    my @commandArgs = ($TETPath, "-o", $textFilePath, $filePath);

    system(@commandArgs);

    if ($? == -1) {
	return (0, "Failed to execute TET: $!");
    } elsif ($? & 127) {
	return (0, "TET died with signal ".($? & 127));
    }

    my $code = $?>>8;
    if (($code == 0) || ($code == 1)) {
	if ($code == 1) {
	    print STDERR "TET completed with errors: $filePath\n";
	}

	push @$rTrace, "PDFLib TET";

	my $sha1 = new FileConverter::CheckSum();
	$sha1->digest($filePath);
	push @$rCheckSums, $sha1;

	return ($status, $msg, $textFilePath, $rTrace, $rCheckSums);

    } else {
	return (0, "Error executing TET (code $code): $!");
    }

} # extractText


1;
