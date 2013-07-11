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
package FileConverter::Compression;
#
# Utilities for handling various compression formats.
#
# Isaac Councill, 09/06/07
#
use strict;
use FileConverter::Config;
use FileConverter::Utils;

# Should all be lower case.
my %supportedCompressionExt = ("gz" => 1,
			       "zip" => 1,
			       "z" => 1,
			       );

my $gunzip = $FileConverter::Config::gunzip;
my $uncompress = $FileConverter::Config::uncompress;
my $unzip = $FileConverter::Config::unzip;


sub decompress {
    my ($fn, $rTrace) = @_;
    my $ext = FileConverter::Utils::getExtension($fn);
    if ($ext =~ m/^gz$/i) {
	return gunzip($fn, $rTrace);
    }
    if ($ext =~ m/^z$/i) {
	return uncompress($fn, $rTrace);
    }
    if ($ext =~ m/^zip$/i) {
	return unzip($fn, $rTrace);
    }
    return (0, "Unsupported compression extension: $ext");

} # decompress


sub canDecompress {
    my ($fn) = @_;
    my $ext = FileConverter::Utils::getExtension($fn);
    if (defined $supportedCompressionExt{lc($ext)}) {
	return 1;
    } else {
	return 0;
    }

} # canDecompress


sub gunzip {
    my ($fn, $rTrace) = @_;
    my @commandArgs = ($gunzip, "-f", $fn);

    system(@commandArgs);

    if ($? == -1) {
	return (0, "Failed to execute gunzip: $!");
    } elsif ($? & 127) {
	return (0, "gunzip died with signal ".($? & 127));
    };
    my $code = $?>>8;
    if ($code == 1) {
	return (0, "Error executing gunzip (code $code): $!");
    }

    push @$rTrace, "gunzip";

    my $newFile = FileConverter::Utils::stripExtension($fn);
    return (1, "", $newFile, $rTrace);

} # gunzip


sub uncompress {
    my ($fn, $rTrace) = @_;
    my @commandArgs = ($uncompress, "-f", $fn);

    system(@commandArgs);

    if ($? == -1) {
	return (0, "Failed to execute uncompress: $!");
    } elsif ($? & 127) {
	return (0, "uncompress died with signal ".($? & 127));
    };
    my $code = $?>>8;
    if ($code == 1) {
	return (0, "Error executing uncompress (code $code): $!");
    }

    push @$rTrace, "uncompress";

    my $newFile = FileConverter::Utils::stripExtension($fn);
    return (1, "", $newFile, $rTrace);

} # uncompress


sub unzip {
    my ($fn, $rTrace) = @_;

    my $dir = FileConverter::Utils::getDirectory($fn);
    my @commandArgs = ($unzip, "-qqo", $fn, "-d", $dir);

    system(@commandArgs);

    if ($? == -1) {
	return (0, "Failed to execute unzip: $!");
    } elsif ($? & 127) {
	return (0, "unzip died with signal ".($? & 127));
    };
    my $code = $?>>8;
    if ($code > 2) {
	return (0, "Error executing unzip (code $code): $!");
    }

    push @$rTrace, "unzip";

    my $newFile = FileConverter::Utils::stripExtension($fn);
    return (1, "", $newFile, $rTrace);

} # unzip


1;
