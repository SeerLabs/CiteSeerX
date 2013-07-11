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
package FileConverter::Controller;
#
# Main interface to FileConverter.  This is where all calls
# should start, and where all conversion sequences should
# be managed.
#
# Isaac Councill, 09/06/07
#
use strict;
use FileConverter::Utils;
use FileConverter::Compression;
use FileConverter::TET;
use FileConverter::PDFBox;
use FileConverter::JODConverter;
use FileConverter::PSConverter;
use FileConverter::PSToText;
use FileConverter::Prescript;
use FileConverter::CheckSum;


##
# Convert the given file to text.  Decompression will occur
# first, and then further conversion and/or text extraction
# will proceed according to the file type.
#
# Supports zip, gz, .Z compression, but only supports the
# PDF file type for now.
##
sub extractText {
    my $fn = shift;

    if (! -e $fn) {
    	return (0, "File does not exist: $fn");
    }

    my ($status, $msg) = (1, "");
    my @trace = ();
    my @checkSums = ();
    my ($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums);

    while(FileConverter::Compression::canDecompress($fn) > 0) {
    	($tstatus, $tmsg, $tfn, $rTrace) =
            FileConverter::Compression::decompress($fn, \@trace);
        if ($tstatus <= 0) {
            return ($tstatus, $tmsg);
        }
        $fn = $tfn;
    }

    my $extension = FileConverter::Utils::getExtension($fn);

    if (!defined $extension) {
        return (0, "File $fn has no extension");
    }

    if ($extension =~ m/^ps$/i) {
        # convert poscript file according config.
        if ($FileConverter::Config::PSConversion eq "TEXT") {
            # go from ps to text directly
	    _convert2pdf($fn, $extension, [], \@checkSums);
            return ps2text($fn, \@trace, \@checkSums);
        }
        if ($FileConverter::Config::PSConversion eq "PDF") {
            # convert to PDF first then to text
            ($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums) = 
                _convert2pdf($fn, $extension, \@trace, \@checkSums);
            if ($tstatus <= 0) {
                return ($tstatus, $tmsg);
            }
            $fn = $tfn;
            $extension = FileConverter::Utils::getExtension($fn);
        }
    }
    elsif (($extension !~ m/^pdf$/i) && ($extension !~ m/^ps$/i)) {
        # first, we need to convert the file to PDF.
        ($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums) =
            _convert2pdf($fn, $extension, \@trace, \@checkSums);
        if ($tstatus <= 0) {
            return ($tstatus, $tmsg);
        }
        $fn = $tfn;
        $extension = FileConverter::Utils::getExtension($fn);
    }
    
    if ($extension =~ m/^pdf$/i) {
        return pdf2text($fn, \@trace, \@checkSums);
    } 
    return (0, "Unsupported file type: $extension");

} # extractText


sub convert2pdf {
    my $fn = shift;

    if (! -e $fn) {
    	return (0, "File does not exist: $fn");
    }

    my ($status, $msg) = (1, "");
    my @trace = ();
    my @checkSums = ();
    my ($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums);

    while(FileConverter::Compression::canDecompress($fn) > 0) {
    	($tstatus, $tmsg, $tfn, $rTrace) =
            FileConverter::Compression::decompress($fn, \@trace);
        if ($tstatus <= 0) {
            return ($tstatus, $tmsg);
        }
        $fn = $tfn;
    }

    my $extension = FileConverter::Utils::getExtension($fn);

    if (!defined $extension) {
        return (0, "File $fn has no extension");
    }

    if ($extension =~ m/^pdf$/i) {

	my $sha1 = FileConverter::CheckSum->new();
	$sha1->digest($fn);
	push @checkSums, $sha1;

	return (1, "", $fn, \@trace, \@checkSums);
    }

    if ($extension =~ m/^ps$/i || $extension =~ m/^rtf$/i) {
	($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums) =
	    _convert2pdf($fn, $extension, \@trace, \@checkSums);
	if ($tstatus <= 0) {
	    return ($tstatus, $tmsg);
	}
	
	my $sha1 = FileConverter::CheckSum->new();
	$sha1->digest($tfn);
	push @$rCheckSums, $sha1;

	return ($tstatus, $tmsg, $tfn, $rTrace, $rCheckSums);
    }

    return (0, "Unsupported file type: $extension");

}  # convert2pdf


sub pdf2text {
    my ($fn, $rTrace, $rCheckSums) = @_;
    if ($FileConverter::Config::PDFTOTEXT eq "TET") {
        return FileConverter::TET::extractText($fn, $rTrace, $rCheckSums);
    }
    if ($FileConverter::Config::PDFTOTEXT eq "PDFBOX") {
        return FileConverter::PDFBox::extractText($fn, $rTrace, $rCheckSums);
    }

} # pdf2text

sub _convert2pdf {
    my ($fn, $extension, $rTrace, $rCheckSums) = @_;
    
    if (($extension =~ m/^rtf$/i) || ($extension =~ m/^doc$/i))  {
        return FileConverter::JODConverter::convertFile($fn, $rTrace,
							$rCheckSums);
    }
    elsif (($extension =~ m/^ps$/i) || ($extension =~ m/^eps$/i)) {
        return FileConverter::PSConverter::convertFile($fn, $rTrace,
						       $rCheckSums);
    }

} # _convert2pdf

sub ps2text {
    my($fn, $rTrace, $rCheckSums) = @_;
    return FileConverter::PSToText::extractText($fn, $rTrace, $rCheckSums);

} # ps2text

1;
