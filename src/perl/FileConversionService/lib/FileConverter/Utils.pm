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
package FileConverter::Utils;
#
# Container for subroutines that may be shared across multiple
# FileConverter modules.
#
# Isaac Councill, 09/06/07
#
use strict;
use Encode;

##
# Returns the file extension of a file name, if there is one.
##
sub getExtension {
    my ($fn) = @_;
    if ($fn =~ m/^.*\.(.*)$/) {
	return $1;
    }
    return undef;

} # getExtension


##
# Strips off the last extension of the file name.
##
sub stripExtension {
    my ($fn) = @_;
    $fn =~ s/^(.*)\..*$/$1/;
    return $fn;

} # stripExtension


##
##
# Routine for checking that a filename ends with an expected
# extension.  Returns 1 if it does, 0 if not.
##
sub checkExtension {
    my ($fn, $ext) = @_;
    if ($fn =~ m/^.*\.(.*)$/) {
	if ($1 =~ m/$ext/i) {
	    return 1;
	}
    }
    return 0;

} # checkExtension


##
# Simple routine for changing the extension of a file.
# Example: $newFileName = changeExtension($oldFileName, "txt");
##
sub changeExtension {
    my ($fn, $ext) = @_;
    unless ($fn =~ s/^(.*)\..*$/$1\.$ext/) {
	$fn .= ".$ext";
    }
    return $fn;

} # changeExtension


##
# Returns the directory part of a file path.
##
sub getDirectory {
    my ($filePath) = @_;
    if ($filePath =~ m/^(.*)\/.*$/) {
	return $1;
    } else {
	return $filePath;
    }

} # getDirectory

##
##
# Routine for checking if a process is running or not
# Returns 1 if it is runnig, 0 if not.
##
sub checkProcess {
	my ($process) = @_;
	my $cmd = "ps -ef | grep " . $process . " | grep -v grep";
	my $result = `$cmd`;
	if ($result eq '') {
		return 0;
	}
	else {
		return 1;
	}
} # checkProcess


##
# Convert an file of the specified encoding to UTF-8
##
sub convertToUTF8 {
    my ($fn, $encoding) = @_;
    my $octets;
    open (FILE, "<$fn") or die "could not open file $fn: $!";
    binmode FILE, ":bytes";
    {
	local $/ = undef;
	$octets = <FILE>;
    }
    close FILE;
    
    Encode::from_to($octets, $encoding, "utf8");
    open (FILE, ">:utf8", "$fn") or die "could not open file $fn: $!";
    print FILE Encode::decode_utf8($octets);
    close FILE;

}

1;
