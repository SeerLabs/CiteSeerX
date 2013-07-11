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
use DBI;

my $fpFile = $Winnow::Config::fpFile;
my $sizeFile = $Winnow::Config::sizeFile;


sub new {
    my ($class) = @_;

    $ENV{'MYSQL_UNIX_PORT'} = "/tmp/mysql.sock";
    my $dbh = DBI->connect("DBI:mysql:winnow");
    my $get_fp = $dbh->prepare("select doc, line from fingerprints ".
			       "where fp=?");
    my $store_fp = $dbh->prepare("insert into fingerprints values ".
				 "(NULL, ?, ?, ?)");
    my $get_size = $dbh->prepare("select size from sizes where doc=?");
    my $store_size = $dbh->prepare("insert into sizes values (?, ?)");

    my $semaphore = new Thread::Semaphore;

    my $self = {
	_dbh        => $dbh,
	_get_fp     => $get_fp,
	_store_fp   => $store_fp,
	_get_size   => $get_size,
	_store_size => $store_size,
	_sem        => $semaphore,
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

    my $sth = $self->{'_get_fp'};
    $sth->execute($key)
	or die "Couldn't execute statement: ".$sth->errstr;
    my $table = $sth->fetchall_arrayref;
    $sth->finish;
    return $table;

} # lookupFP


sub storeNewFPs {
    my ($self, $rTriples) = @_;
    foreach my $triple (@$rTriples) {
	my $sth = $self->{'_store_fp'};
	$sth->execute($triple->[0], $triple->[1], $triple->[2])
	    or die "Couldn't execute statement: ".$sth->errstr;
	$sth->finish;
    }

} # storeFP


sub lookupSize {
    my ($self, $key) = @_;

    my $sth = $self->{'_get_size'};
    $sth->execute($key)
	or die "Couldn't execute statement: ".$sth->errstr;
    my ($size) = $sth->fetchrow_array;
    $sth->finish;

    return $size;

} # lookupSize


sub storeNewSize {
    my ($self, $key, $val) = @_;

    my $sth = $self->{'_store_size'};
    $sth->execute($key, $val)
	or die "Couldn't execute statement: ".$sth->errstr;
    $sth->finish;

} # storeSize


sub DESTROY {
    my $self = shift;
    $self->{'_dbh'}->disconnect;

} # DESTROY


1;
