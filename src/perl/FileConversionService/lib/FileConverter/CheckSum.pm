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
package FileConverter::CheckSum;
#
# Container for checksum info and utilities for individual files.
#
# Isaac Councill
#
use strict;
use Digest::SHA1;
use FileConverter::Utils;


sub new {
    my ($class) = @_;
    my $self = {
	'_fileType' => undef,
	'_sha1'     => undef,
    };
    bless $self, $class;
    return $self;

}  # new


sub getFileType {
    my $self = shift;
    return $self->{'_fileType'};
}


sub setFileType {
    my ($self, $fileType) = @_;
    $self->{'_fileType'} = $fileType;
}


sub getSHA1 {
    my $self = shift;
    return $self->{'_sha1'};
}


sub setSHA1 {
    my ($self, $sha1) = @_;
    $self->{'_sha1'} = $sha1;
}


sub digest {
    my ($self, $filePath) = @_;

    open(FILE, "<$filePath") or die ("Could not open for reading: $filePath");
    my $digester = Digest::SHA1->new;
    $digester->addfile(*FILE);

    my $ext = FileConverter::Utils::getExtension($filePath);
    my $sha1 = $digester->hexdigest;
    close FILE;

    $self->setFileType($ext);
    $self->setSHA1($sha1);

}  # digest

1;

