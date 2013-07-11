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

    my @sentences = @$rFingerprints;
    my $nLines = $#sentences+1;

    my $rFingerprints = [];
    my $rDistinctSentencesMatched = {};
    my $rDistinctSentencesOtherDocs = {};

    my $rOtherDocSentences = {};

    my $sentID = 0;

    $self->{'_DB'}->getLock();

    foreach my $sentence (@sentences) {

	my $rMIDsSeen = {};

	foreach my $key (@$sentence) {

	    push @$rFingerprints, [$key, $id, $sentID];

	    my $rValues = $self->{'_DB'}->lookupFP($key);
	    if (!defined $rValues || !@$rValues) {
		next;
	    }

	    foreach my $pair (@$rValues) {

		my ($mid, $mline) = ($pair->[0], $pair->[1]);
		if (!(defined $mid && defined $mline)) {
		    print STDERR "WARNING: bad pair ID: $mid LINE: $mline\n";
		    next;
		}
		$rMIDsSeen->{$mid}++;

		my $rOtherDoc = $rOtherDocSentences->{$mid};
		if (!defined $rOtherDoc) {
		    $rOtherDocSentences->{$mid} = { $mline => 1 };
		    $rDistinctSentencesOtherDocs->{$mid}++;
		} else {
		    my $l = $rOtherDoc->{$mid}->{$mline};
		    unless (defined $l) {
			$rOtherDoc->{$mid}->{$mline}++;
			$rDistinctSentencesOtherDocs->{$mid}++;
		    }
		}
	    }
	}

	foreach my $mid (keys %$rMIDsSeen) {
	    $rDistinctSentencesMatched->{$mid}++;
	}
	$sentID++;
    }

    my ($isExact, $rMatchSet) =
	$self->calcOverlaps($id, $nLines, $rDistinctSentencesMatched,
			    $rDistinctSentencesOtherDocs);

    if ($isExact<=0) {
	$self->{'_DB'}->storeNewFPs($rFingerprints);
	$self->{'_DB'}->storeNewSize($id, $sentID);
    }
    $self->{'_DB'}->releaseLock();

    return ($isExact, $rMatchSet);

} # getOverlaps


sub calcOverlaps {
    my ($self, $id, $nLines,
	$rSentencesMatched, $rOtherDocSentencesMatched) = @_;

    my @matchSet = ();

    my $isExact = 0;

    foreach my $mid (sort {$rSentencesMatched->{$b} <=>
			       $rSentencesMatched->{$a}}
		     keys %$rSentencesMatched) {

	my $linesOverlap = $rSentencesMatched->{$mid};
	my $MN = $self->{'_DB'}->lookupSize($mid);

	my $Nratio = $linesOverlap / $nLines;
	# Assume that the number of distinct sentences matched
        # from the source document is a good estimate of the
        # number of distinct sentences matched in the target.
	# If lots of duplicate or near-duplicate sentences exist
	# in source or target (but not both), this assumption
	# could cause funny results (but it still lets us go
	# twice as fast).
	#--irrelevant now
	my $mLinesOverlap = $rOtherDocSentencesMatched->{$mid};
	my $MNratio = $mLinesOverlap / $MN;

	if ($Nratio==1 && $MNratio==1) {
	    $isExact = 1;
	    push @matchSet, ["==", $id, $Nratio, $mid, $MNratio];
	}

	elsif (($Nratio >= $minOverlap) && ($MNratio >= $minOverlap)) {
	    push @matchSet, ["=", $id, $Nratio, $mid, $MNratio];
	} elsif ($Nratio >= $minOverlap) {
	    push @matchSet, ["<", $id, $Nratio, $mid, $MNratio];
	} elsif ($MNratio >= $minOverlap) {
	    push @matchSet, [">", $id, $Nratio, $mid, $MNratio];
	}
    }

    return ($isExact, \@matchSet);

} # calcOverlaps

1;

