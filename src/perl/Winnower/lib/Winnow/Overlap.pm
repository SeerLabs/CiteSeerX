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
package Winnow::Overlap;

use strict;
use Winnow::Config;
use Time::HiRes qw(gettimeofday tv_interval);

my $nearDupOverlap = $Winnow::Config::nearDupOverlap;
my $minOverlap = $Winnow::Config::minOverlap;

sub new {
    my ($class, $database) = @_;

    my $self = {
	_DB => $database,
    };

    bless $self, $class;
    return $self;

} # new


sub getOverlaps {
    my ($self, $id, $rFingerprints) = @_;

    my @uniqueFP = keys %$rFingerprints;
    my $size = $#uniqueFP+1;

    my $rTriples = $self->buildTriples($id, $rFingerprints);
    my %matches;

    $self->{'_DB'}->getLock();

    foreach my $fp (@uniqueFP) {

	my %MIDsSeen;

	my $rValues = $self->{'_DB'}->lookupFP($fp);
	if (!defined $rValues || !@$rValues) {
	    next;
	}

	foreach my $pair (@$rValues) {
	    my ($mid, $mline) = ($pair->[0], $pair->[1]);
	    if (defined $MIDsSeen{$mid}) {
		next;
	    }
	    $matches{$mid}++;
	    $MIDsSeen{$mid}++;
	}
    }

    my ($isExact, $rMatchSet) =
	$self->calcOverlaps($id, $size, \%matches);

    if ($isExact<=0) {
	$self->{'_DB'}->storeNewFPs($rTriples);
	$self->{'_DB'}->storeNewSize($id, $size);
    }
    $self->{'_DB'}->releaseLock();

    return ($isExact, $rMatchSet);

} # getOverlaps


sub buildTriples {
    my ($self, $id, $rFingerprints) = @_;
    my @triples;
    foreach my $fp (keys %$rFingerprints) {
	my @lines = @{$rFingerprints->{$fp}};
	foreach my $line (@lines) {
	    push @triples, [$fp, $id, $line];
	}
    }
    return \@triples;

} # _buildTriples


sub calcOverlaps {
    my ($self, $id, $size, $rMatches) = @_;

    my @matchSet = ();
    my $isExact = 0;

    foreach my $mid (sort {$rMatches->{$b} <=> $rMatches->{$a}}
		     keys %$rMatches) {
	my $overlap = $rMatches->{$mid};
	if ($overlap < $minOverlap) {
	    last;
	}
	my $msize = $self->{'_DB'}->lookupSize($mid);

	my $ratio = $overlap / $size;
	my $mratio = $overlap / $msize;
	if ($ratio==1 && $mratio==1) {
	    $isExact = 1;
	    push @matchSet, ["==", $id, $ratio, $mid, $mratio];
	}

	elsif (($ratio >= $nearDupOverlap) && ($mratio >= $nearDupOverlap)) {
	    push @matchSet, ["=", $id, $ratio, $mid, $mratio];
	} elsif ($ratio >= $nearDupOverlap) {
	    push @matchSet, [">", $id, $ratio, $mid, $mratio];
	} elsif ($mratio >= $nearDupOverlap) {
	    push @matchSet, ["<", $id, $ratio, $mid, $mratio];
	}
    }

    return ($isExact, \@matchSet);

} # calcOverlaps


1;
