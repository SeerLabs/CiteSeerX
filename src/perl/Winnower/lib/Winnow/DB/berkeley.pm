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
package Winnow::DB;

use strict;
use Winnow::Config;
use Thread::Semaphore;
use BerkeleyDB;
use FileHandle;

my $fpFile = $Winnow::Config::fpFile;
my $sizeFile = $Winnow::Config::sizeFile;


sub new {
    my ($class) = @_;

    my %fingerprints;
    my $fpdb = tie %fingerprints, 'BerkeleyDB::Hash',
    -Filename => $fpFile,
    -Flags    => DB_CREATE
	or die "Cannot open fingerprint file $fpFile: $!";

    my %filesizes;
    my $sizedb = tie %filesizes, 'BerkeleyDB::Hash',
    -Filename => $sizeFile,
    -Flags    => DB_CREATE
	or die "Cannot open size file $sizeFile: $!";

    my $semaphore = new Thread::Semaphore;

    my $self = {
	_fpData   => \%fingerprints,
	_fpdb     => $fpdb,
	_sizeData => \%filesizes,
	_sizedb   => $sizedb,
	_sem      => $semaphore,
    };

    bless $self, $class;
    return $self;

} # new


sub initializeData {
    my ($rFPHash, $rSizeHash) = @_;

    if ((! -e $fpFile) && (! -e $sizeFile)) {
	return;
    }
    open(IN, "<$fpFile") or die "Cannot open $fpFile for reading: $!";
    while(<IN>) {
	chomp;
	my ($fp, $id, $sent) = split " ";
	my $rFPArray = $rFPHash->{$fp};
	unless ($rFPArray) {
	    $rFPHash->{$fp} = [];
	    $rFPArray = $rFPHash->{$fp};
	}
	push @$rFPArray, [$id, $sent];
    }
    close IN;

    open(IN, "<$sizeFile") or die "Cannot open $sizeFile for reading: $!";
    while(<IN>) {
	chomp;
	my ($id, $size) = split " ";
	if (defined $id && defined $size) {
	    $rSizeHash->{$id} = $size;
	}
    }
    close IN;

} # initializeData


sub getLock {
    my ($self) = @_;
    $self->{'_sem'}->down;
}


sub releaseLock {
    my ($self) = @_;
    $self->{'_sem'}->up;
}


sub lookupFP {
    my ($self, $key) = @_;
    my $val;
    $self->{'_fpdb'}->db_get($key, $val);
    my @pairs = ();
    foreach my $pair (split "\:", $val) {
	push @pairs, [ split "=", $pair ];
    }
    return \@pairs;

} # lookupFP


sub storeNewFPs {
    my ($self, $rTriples) = @_;
    foreach my $triple (@$rTriples) {
	my $val;
	$self->{'_fpdb'}->db_get($triple->[0], $val);
	if (defined $val) {
	    $val.=":";
	}
	$val .= $triple->[1]."=".$triple->[2];
	$self->{'_fpdb'}->db_put($triple->[0], $val);
    }

} # storeFP


sub lookupSize {
    my ($self, $key) = @_;
    my $val;
    $self->{'_sizedb'}->db_get($key, $val);
    return $val;

} # lookupSize


sub storeNewSize {
    my ($self, $key, $val) = @_;
    $self->{'_sizedb'}->db_put($key, $val);

} # storeSize


sub DESTROY {
    my $self = shift;
    print "Destroying DB\n";
    $self->{'_fpdb'}->db_close;
    $self->{'_sizedb'}->db_close;

} # DESTROY


1;
