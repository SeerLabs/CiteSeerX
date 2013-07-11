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
package FileConverter::PDFBox;
#
# Wrapper to call the PDFBox ExtractText command-line tool
# for extracting text from PDF files.  It's recommended to
# use TET instead, if TET is available.
#
# Isaac Councill, 09/06/07
#
use strict;
use FileConverter::Config;
use FileConverter::Utils;

my $PDFBoxLoc = $FileConverter::Config::PDFBoxLocation;

##
# Execute the PDFBox utility.
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
    my @commandArgs = ("java", "-cp", $PDFBoxLoc,
		       "org.pdfbox.ExtractText", "-encoding",
		       "utf8", $filePath, $textFilePath);

    system(@commandArgs);

    if ($? == -1) {
	return (0, "Failed to execute PDFBox: $!");
    } elsif ($? & 127) {
	return (0, "Java died with signal ".($? & 127));
    }

    my $code = $?>>8;
    if ($code == 0) {
	push @$rTrace, "PDFBox";

	my $sha1 = FileConverter::CheckSum->new();
	$sha1->digest($filePath);
	push @$rCheckSums, $sha1;

	return ($status, $msg, $textFilePath, $rTrace, $rCheckSums);
    } else {
	return (0, "Error executing PDFBox (code $code): $!");
    }

} # extractText


1;
