package ParsCit::PreProcess;
#
# Utilities for finding and normalizing citations within
# text files, including separating citation text from
# body text and segmenting citations.
#
# Isaac Councill, 7/19/07
#

use strict;
use utf8;
use ParsCit::Citation;

my %markerTypes = (
		   'SQUARE' => '\\[.+?\\]',
		   'PAREN' => '\\(.+?\\)',
		   'NAKEDNUM' => '\\d+',
		   'NAKEDNUMDOT' => '\\d+\\.',
		   );


##
# Looks for reference section markers in the supplied text and
# separates the citation text from the body text based on these
# indicators.  If it looks like there is a reference section marker
# too early in the document, this procedure will try to find later
# ones.  If the final reference section is still too long, an empty
# citation text string will be returned.  Returns references to
# the citation text, normalized body text, and original body text.
##
sub findCitationText {
    my ($rText) = @_;
    my $text = $$rText;
    my $bodyText = '0';
    my $citeText = '0';

    while ($text =~ m/\b(References?|REFERENCES?|Bibliography|BIBLIOGRAPHY|References?\s+and\s+Notes?|References?\s+Cited|REFERENCE?\s+CITED|REFERENCES?\s+AND\s+NOTES?):?\s*\n+/sg) {
	$bodyText = substr $text, 0, pos $text;
	$citeText = substr $text, pos $text unless (pos $text < 1);
    }
    if (length($citeText) >= 0.8*length($bodyText)) {
	print STDERR "Citation text longer than article body: ignoring\n";
	$citeText = "";
	return \$citeText, \normalizeBodyText(\$bodyText), \$bodyText;
    }
    my ($sciteText, $tmp) = split(/^([\s\d\.]+)?(Acknowledge?ments?|Autobiographical|Tables?|Appendix|Exhibit|Annex|Fig|Notes?)(.*?)\n+/m, $citeText);
    if (length($sciteText)>0) {
	$citeText = $sciteText;
    }

    if ($citeText eq '0' || !defined $citeText) {
	print STDERR "warning: no citation text found\n";
    }

    return (normalizeCiteText(\$citeText),
	    normalizeBodyText(\$bodyText),
	    \$bodyText);

}  # findCitationText


##
# Removes lines that appear to be junk from the citation text.
##
sub normalizeCiteText {
    my ($rCiteText) = @_;

    my @lines = split "\n", $$rCiteText;
    my @newLines = ();
    foreach my $line (@lines) {
	if ($line =~ m/^[\s\d]*$/) {
	    next;
	}
	push @newLines, $line;
    }
    my $newText = join "\n", @newLines;
    return \$newText;

}  # normalizeCiteText


##
# Removes lines that appear to be junk from the body text,
# de-hyphenates words where a hyphen occurs at the end of
# a line, and normalizes strings of blank spaces to only
# single blancks.
##
sub normalizeBodyText {
    my ($rText) = @_;
    my @lines = split "\n", $$rText;
    my $text = "";
    foreach my $line (@lines) {
	if ($line =~ m/^\s*$/) {
	    next;
	}
	if ($text =~ s/(\w)\-$/$1/) {
	    $text .= $line;
	} else {
	    $text .= " ".$line;
	}
    }
    $text =~ s/\s\s+/\s/g;
    return \$text;

} # normalizeBodyText


##
# Controls the process by which citations are segmented,
# based on the result of trying to guess the type of
# citation marker used in the reference section.  Returns
# a reference to a list of citation objects.
##
sub segmentCitations {
    my ($rCiteText) = @_;
    my $markerType = guessMarkerType($rCiteText);

    my $rCitations;

    if ($markerType ne 'UNKNOWN') {
	$rCitations = splitCitationsByMarker($rCiteText, $markerType);
    } else {
	$rCitations = splitUnmarkedCitations($rCiteText);
    }

    return $rCitations;

}  # segmentCitations


##
# Segments citations that have explicit markers in the
# reference section.  Whenever a new line starts with an
# expression that matches what we'd expect of a marker,
# a new citation is started.  Returns a reference to a
# list of citation objects.
##
sub splitCitationsByMarker {
    my ($rCiteText, $markerType) = @_;
    my @citations;
    my $currentCitation = new ParsCit::Citation();
    my $currentCitationString;

    # TODO: Might want to add a check that marker number is
    # increasing as we'd expect, if the marker is numeric.

    foreach my $line (split "\n", $$rCiteText) {
	if ($line =~ m/^\s*($markerTypes{$markerType})\s*(.*)$/) {
	    my ($marker, $citeString) = ($1, $2);
	    if (defined $currentCitationString) {
		$currentCitation->setString($currentCitationString);
		push @citations, $currentCitation;
		$currentCitationString = undef;
	    }
	    $currentCitation = new ParsCit::Citation();
	    $currentCitation->setMarkerType($markerType);
	    $currentCitation->setMarker($marker);
	    $currentCitationString = $citeString;
	} else {
	    if ($currentCitationString =~ m/\w\-$/) {
		# merge words when lines are hyphenated
		$currentCitationString =~ s/\-$//;
		$currentCitationString .= $line;
	    } else {
		$currentCitationString .= " ".$line;
	    }
	}
    }
    if (defined $currentCitation && defined $currentCitationString) {
	$currentCitation->setString($currentCitationString);
	push @citations, $currentCitation;
    }
    return \@citations;

}  # splitCitationsByMarker


##
# Uses several heuristics to decide where individual citations
# begin and end based on the length of previous lines, strings
# that look like author lists, and punctuation.  Returns a
# reference to a list of citation objects.
##
sub splitUnmarkedCitations {
    my ($rCiteText) = @_;
    my @content = split "\n", $$rCiteText;
    my @citeStarts = ();
    my $citeStart = 0;
    my @citations = ();

    for (my $i=0; $i<=$#content; $i++) {
	if ($content[$i] =~ m/\b\(?[1-2][0-9]{3}[\p{IsLower}]?[\)?\s,\.]*(\s|\b)/s) {
	    for (my $k=$i; $k > $citeStart; $k--) {
		if ($content[$k] =~ m/\s*[\p{IsUpper}]/g) {

		    # If length of previous line is extremely small,
		    # start a new citation here.
		    if (length($content[$k-1]) < 2) {
			$citeStart = $k;
			last;
		    }

		    # Start looking backwards for lines that could
		    # be author lists - these usually start the
		    # citation, have several separation characters (,;),
		    # and shouldn't contain any numbers.
		    my $beginningAuthorLine = -1;
		    for (my $j=$k-1; $j>$citeStart; $j--) {
			if ($content[$j] =~ m/\d/) {
			    last;
			}
			$_ = $content[$j];
			my $nSep = s/([,;])/\1/g;
			if ($nSep >= 3) {
			    if (($content[$j-1] =~ m/\.\s*$/) || $j==0) {
				$beginningAuthorLine = $j;
			    }
			} else {
			    last;
			}
		    }
		    if ($beginningAuthorLine >= 0) {
			$citeStart = $beginningAuthorLine;
			last;
		    }

		    # Now that the backwards author search failed
		    # to find any extra lines, start a new citation
		    # here if the previous line ends with a ".".
		    if ($content[$k-1] =~ m/\.\s*$/) {
			$citeStart = $k;
			last;
		    }
		}
	    }
	    push @citeStarts, $citeStart
		unless (($citeStart <= $citeStarts[$#citeStarts]) &&
			($citeStart != 0));
	}
    }
    for (my $k=0; $k<$#citeStarts; $k++) {
	my $firstLine = $citeStarts[$k];
	my $lastLine = ($k==$#citeStarts) ? $#content : ($citeStarts[$k+1]-1);
	my $citeString =
	    mergeLines(join "\n", @content[$firstLine .. $lastLine]);
	my $citation = new ParsCit::Citation();
	$citation->setString($citeString);
	push @citations, $citation;
    }
    return \@citations;

}  # splitUnmarkedCitations


##
# Merges lines of text by dehyphenating where appropriate,
# with normal spacing.
##
sub mergeLines {
    my ($text) = shift;
    my @lines = split "\n", $text;
    my $mergedText = "";
    foreach my $line (@lines) {
	$line = trim($line);
	if ($mergedText =~ m/\w\-$/) {
	    $mergedText =~ s/\-$//;
	    $mergedText .= $line;
	} else {
	    $mergedText .= " ".$line;
	}
    }
    return trim($mergedText);

}  # mergeLines


##
# Uses a list of regular expressions that match common citation
# markers to count the number of matches for each type in the
# text.  If a sufficient number of matches to a particular type
# are found, we can be reasonably sure of the type.
##
sub guessMarkerType {
    my ($rCiteText) = @_;
    my $markerType = 'UNKNOWN';
    my %markerObservations;
    foreach my $type (keys %markerTypes) {
	$markerObservations{$type} = 0;
    }

    my $citeText = "\n".$$rCiteText;
    $_ = $citeText;
    my $nLines = s/\n/\n/gs - 1;

    while ($citeText =~ m/\n\s*($markerTypes{'SQUARE'}([^\n]){10})/sg) {
	$markerObservations{'SQUARE'}++;
    }

    while ($citeText =~ m/\n\s*($markerTypes{'PAREN'}([^\n]){10})/sg) {
	$markerObservations{'PAREN'}++;
    }

    while ($citeText =~ m/\n\s*($markerTypes{'NAKEDNUM'} [^\n]{10}) /sg) {
	$markerObservations{'NAKEDNUM'}++;
    }

    while ($citeText =~ m/\n\s*$markerTypes{'NAKEDNUMDOT'}([^\n]){10}/sg) {
	$markerObservations{'NAKEDNUMDOT'}++;
    }

    my @sortedObservations =
	sort {$markerObservations{$b} <=> $markerObservations{$a}}
    keys %markerObservations;

    my $minMarkers = $nLines / 6;
    if ($markerObservations{$sortedObservations[0]} >= $minMarkers) {
	$markerType = $sortedObservations[0];
    }
    return $markerType;

}  # guessMarkerType


sub trim {
    my $text = shift;
    $text =~ s/^\s+//;
    $text =~ s/\s+$//;
    return $text;

}  # trim


1;
