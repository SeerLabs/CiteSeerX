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
package Winnow::Winnower;
#
#
#
# Isaac Councill, 09/11/07
#
use strict;
use Winnow::Config;

##
# Winnowing parameters
##
my $k = $Winnow::Config::k;
my $t = $Winnow::Config::t;
my $L = $Winnow::Config::L;
my $maxSentences = $Winnow::Config::maxSentences;
my $maxPrints = $Winnow::Config::maxSentencePrints;
my $hashSpace = $Winnow::Config::hashSpace;
my $winSize = $t-$k+1;


##
#
##
sub fingerprintDoc {
    my $textFile = shift;
    my $text;
    open (IN, "<:utf8", $textFile);
    while(my $line = <IN>) {
	chomp($line);
	trim(\$line);
	if ($text =~ m/\-$/) {
	    $text =~ s/\-$/$line/;
	} else {
	    if ($line =~ m/^\p{IsUpper}/ && $text !~ m/\.$/) {
		$text .= ". $line";
	    } else {
		$text .= " $line";
	    }
	}
    }
    close IN;

    my $commonAbbreviations = "Dr|Mr|Mrs|Prof|Fig";
    $text =~ s/ ($commonAbbreviations)\./$1/gi;

    my $nPrints = 0;
    my $sentenceID = 0;

    my %fingerprints;
    while ($text =~ m/(.*?)(?<! .|\..)\.(?=\s)/g) {
	my $sentence = $1;
	my $rFingerprints = fingerprintSentence(\$sentence, \$nPrints);
	if (defined $rFingerprints && @$rFingerprints) {
	    foreach my $fp (@$rFingerprints) {
		my $fpMatches = {};
		if (defined $fingerprints{$fp}) {
		    $fpMatches = $fingerprints{$fp};
		} else {
		    $fingerprints{$fp} = $fpMatches;
		}
		$fpMatches->{$sentenceID}++;
	    }
	    $sentenceID++;
	}
	if ($sentenceID >= $maxSentences) {
	    last;
	}
    }

    # Convert fingerprint line hashes to arrays
    foreach my $fp (keys %fingerprints) {
	my $hash = $fingerprints{$fp};
	my @arr = keys %$hash;
	$fingerprints{$fp} = \@arr;
    }
#    print "NPRINTS: $nPrints\n";
    return (\%fingerprints, $nPrints);

} # fingerprintDoc


sub fingerprintSentence {
    my ($rSentence, $rNPrints) = @_;
    clean($rSentence);
    my @tokens = split " +", $$rSentence;
    my @kgramHashes = ();
    for (my $i=0; $i+($k-1)<=$#tokens; $i++) {
	my @kgram = @tokens[$i..$i+($k-1)];
	my $kgram = join "", @kgram;
	push @kgramHashes, [perlhash($kgram), $i];
    }
    if ($#kgramHashes<0) {
	return undef;
    }
    my @windows = ();
    for (my $i=0; $i+$winSize<=$#kgramHashes; $i++) {
	my @window = @kgramHashes[$i..$i+($k-1)];
	push @windows, \@window;
    }
    my %fingerprints;
    my $nPrintsFound = 0;
    if ($#windows<0) {
	my $ref = findMin(\@kgramHashes);
	if (defined $ref && @$ref) {
	    $$rNPrints++;
	    my ($val, $pos) = @$ref;
	    return [$val];
	}
    } else {
	foreach my $rWin (@windows) {
	    my $ref = findMin($rWin);
	    if (defined $ref && @$ref) {
		my ($val, $pos) = @$ref;
		$fingerprints{$pos} = $val;
	    }
	}
    }
    my @fingerprints;
    foreach my $pos (sort {$a <=> $b} keys %fingerprints) {
	if ($#fingerprints+1 >= $maxPrints) {
	    last;
	}
	push @fingerprints, $fingerprints{$pos};
	$$rNPrints++;
    }
    return \@fingerprints;

} # fingerprintSentence


sub clean {
    my $rSentence = shift;
    $$rSentence =~ s/[^\p{IsAlpha}\s]//g;
    $$rSentence = lc($$rSentence);
    trim($rSentence);

} # clean


sub trim {
    my $rText = shift;
    $$rText =~ s/^\s+//;
    $$rText =~ s/\s+$//;
}


sub perlhash {
    my $text = shift;
    my $hash = 0;
    foreach (split //, $text) {
	$hash = ($hash*33 + ord($_)) % $hashSpace;
    }
#    print "$hash\n";
    return $hash;

} # perlhash



sub findMin {
    my $rArray = shift;
    if ($#$rArray<0) {
	return undef;
    }
    my $min = $rArray->[0];
    foreach my $val (@$rArray) {
	if ($val->[0]<=$min->[0]) {
	    $min = $val;
	}
    }
    return $min;

} # findMin


1;
