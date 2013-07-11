package ParsCit::Citation;
#
# Container object for citation data.  Stores all metadata fields
# associated with a citation and utility methods for data access
# and transformation.
#
# Isaac Councill, 7/19/07
#

use strict;
use CSXUtil::SafeText qw(cleanAll cleanXML);

sub new {
    my ($class) = @_;
    my @authors = ();
    my @contexts = ();
    my $self = {
	'_rawString' => undef,
	'_markerType' => undef,
	'_marker' => undef,
        '_authors' => \@authors,
	'_title' => undef,
	'_year' => undef,
	'_publisher' => undef,
	'_location' => undef,
	'_booktitle' => undef,
	'_journal' => undef,
	'_pages' => undef,
	'_volume' => undef,
	'_number' => undef,
	'_contexts' => \@contexts,
	'_tech' => undef,
	'_institution' => undef,
	'_editor' => undef,
	'_note' => undef,
    };
    bless $self, $class;
    return $self;

} # new


##
# Looks for various combinations of data that could be used to
# uniquely identify a citation.  If too much data is missing,
# returns 0; otherwise, returns 1.
##
sub isValid {
    my ($self) = @_;
    my @authors = $self->getAuthors();
    my $title = $self->getTitle();
    my $venue = $self->getJournal();
    my $date = $self->getDate();
    if (!defined $venue) {
	$venue = $self->getBooktitle();
    }
    if ($#authors>=0 && (defined $title || defined $date)) {
	return 1;
    }
    if (defined $venue && defined $date) {
	return 1;
    }
    if (defined $title) {
	return 1;
    }
    return 0;

} # isValid


##
# Utility for loading in a datum based on a tag from Tr2crfpp output.
##
sub loadDataItem {
    my ($self, $tag, $data) = @_;
    if ($tag eq "authors") {
	my @authors = @$data;
	foreach my $auth (@authors) {
	    $self->addAuthor($auth);
	}
    }
    if ($tag eq "contexts") {
	my @contexts = @$data;
	foreach my $context (@contexts) {
	    $self->addContext($context);
	}
    }
    if ($tag eq "title") {
	$self->setTitle($data);
    }
    if ($tag eq "date") {
	$self->setDate($data);
    }
    if ($tag eq "journal") {
	$self->setJournal($data);
    }
    if ($tag eq "booktitle") {
	$self->setBooktitle($data);
    }
    if ($tag eq "tech") {
	$self->setTech($data);
    }
    if ($tag eq "location") {
	$self->setLocation($data);
    }
    if ($tag eq "volume") {
	$self->setVolume($data);
    }
    if ($tag eq "note") {
	$self->setNote($data);
    }
    if ($tag eq "editor") {
	$self->setEditor($data);
    }
    if ($tag eq "publisher") {
	$self->setPublisher($data);
    }
    if ($tag eq "pages") {
	$self->setPages($data);
    }
    if ($tag eq "institution") {
	$self->setInstitution($data);
    }
    if ($tag eq "marker") {
	$self->setMarker($data);
    }

} # loadDataItem


##
# Returns a well-formed XML snippet containing all the data
# in a citation object.
##
sub toXML {
    my $self = shift;

    my $valid = $self->isValid();
    if ($valid>0) {
	$valid = "true";
    } else {
	$valid = "false";
    }

    my $xml = "<citation valid=\"$valid\">\n";
    my @authors = $self->getAuthors();
    map { $_ =~ s/_/ /g } @authors;

    if ($#authors >= 0) {
	$xml .= "<authors>\n";
	foreach my $auth (@authors) {
	    cleanAll(\$auth);
	    $xml .= "<author>$auth</author>\n";
	}
	$xml .= "</authors>\n";
    }

    my $title = $self->getTitle();
    if (defined $title) {
	cleanAll(\$title);
	$xml .= "<title>$title</title>\n";
    }

    my $date = $self->getDate();
    if (defined $date) {
	cleanAll(\$date);
	$xml .= "<date>$date</date>\n";
    }

    my $journal = $self->getJournal();
    if (defined $journal) {
	cleanAll(\$journal);
	$xml .= "<journal>$journal</journal>\n";
    }

    my $booktitle = $self->getBooktitle();
    if (defined $booktitle) {
	cleanAll(\$booktitle);
	$xml .= "<booktitle>$booktitle</booktitle>\n";
    }

    my $tech = $self->getTech();
    if (defined $tech) {
	cleanAll(\$tech);
	$xml .= "<tech>$tech</tech>\n";
    }

    my $volume = $self->getVolume();
    if (defined $volume) {
	cleanAll(\$volume);
	$xml .= "<volume>$volume</volume>\n";
    }

    my $pages = $self->getPages();
    if (defined $pages) {
	cleanAll(\$pages);
	$xml .= "<pages>$pages</pages>\n";
    }

    my $editor = $self->getEditor();
    if (defined $editor) {
	cleanAll(\$editor);
	$xml .= "<editor>$editor</editor>\n";
    }

    my $publisher = $self->getPublisher();
    if (defined $publisher) {
	cleanAll(\$publisher);
	$xml .= "<publisher>$publisher</publisher>\n";
    }

    my $institution = $self->getInstitution();
    if (defined $institution) {
	cleanAll(\$institution);
	$xml .= "<institution>$institution</institution>\n";
    }

    my $location = $self->getLocation();
    if (defined $location) {
	cleanAll(\$location);
	$xml .= "<location>$location</location>\n";
    }

    my $note = $self->getNote();
    if (defined $note) {
	cleanAll(\$note);
	$xml .= "<note>$note</note>\n";
    }

    my @contexts = $self->getContexts();
    if ($#contexts >= 0) {
	$xml .= "<contexts>\n";
	foreach my $context (@contexts) {
	    cleanAll(\$context);
	    $xml .= "<context>$context</context>\n";
	}
	$xml .= "</contexts>\n";
    }

    my $marker = $self->getMarker();
    if (defined $marker) {
	cleanAll(\$marker);
	$xml .= "<marker>$marker</marker>\n";
    }

    my $rawString = $self->getString();
    if (defined $rawString) {
	cleanXML(\$rawString);
	$xml .= "<rawString>$rawString</rawString>\n";
    }
    $xml .= "</citation>\n";
    return $xml;

} # toXML


sub getString {
    my ($self) = @_;
    return $self->{'_rawString'};
}

sub setString {
    my ($self, $str) = @_;
    $self->{'_rawString'} = $str;
}

sub getMarkerType {
    my ($self) = @_;
    return $self->{'_markerType'};
}

sub setMarkerType {
    my ($self, $markerType) = @_;
    $self->{'_markerType'} = $markerType;
}

sub getMarker {
    my ($self) = @_;
    return $self->{'_marker'};
}

sub setMarker {
    my ($self, $marker) = @_;
    $self->{'_marker'} = $marker;
}

sub addAuthor {
    my ($self, $author) = @_;
    my @authors = @{$self->{'_authors'}};
    push @authors, $author;
    $self->{'_authors'} = \@authors;
}

sub getAuthors {
    my ($self) = @_;
    return @{$self->{'_authors'}};
}

sub addContext {
    my ($self, $context) = @_;
    my @contexts = @{$self->{'_contexts'}};
    push @contexts, $context;
    $self->{'_contexts'} = \@contexts;
}

sub getContexts {
    my ($self) = @_;
    return @{$self->{'_contexts'}};
}

sub getTitle {
    my ($self) = @_;
    return $self->{'_title'};
}

sub setTitle {
    my ($self, $title) = @_;
    $self->{'_title'} = $title;
}

sub getDate {
    my $self = shift;
    return $self->{'_year'};
}

sub setDate {
    my ($self, $year) = @_;
    $self->{'_year'} = $year;
}

sub getPublisher {
    my $self = shift;
    return $self->{'_publisher'};
}

sub setPublisher {
    my ($self, $publisher) = @_;
    $self->{'_publisher'} = $publisher;
}

sub getLocation {
    my $self = shift;
    return $self->{'_location'};
}

sub setLocation {
    my ($self, $location) = @_;
    $self->{'_location'} = $location;
}

sub getBooktitle {
    my $self = shift;
    return $self->{'_booktitle'};
}

sub setBooktitle {
    my ($self, $booktitle) = @_;
    $self->{'_booktitle'} = $booktitle;
}

sub getJournal {
    my $self = shift;
    return $self->{'_journal'};
}

sub setJournal {
    my ($self, $journal) = @_;
    $self->{'_journal'} = $journal;
}

sub getPages {
    my $self = shift;
    return $self->{'_pages'};
}

sub setPages {
    my ($self, $pages) = @_;
    $self->{'_pages'} = $pages;
}

sub getVolume {
    my $self = shift;
    return $self->{'_volume'};
}

sub setVolume {
    my ($self, $volume) = @_;
    $self->{'_volume'} = $volume;
}

sub getVolume {
    my $self = shift;
    return $self->{'_volume'};
}

sub setVolume {
    my ($self, $volume) = @_;
    $self->{'_volume'} = $volume;
}

sub getTech {
    my $self = shift;
    return $self->{'_tech'};
}

sub setTech {
    my ($self, $tech) = @_;
    $self->{'_tech'} = $tech;
}

sub getInstitution {
    my $self = shift;
    return $self->{'_institution'};
}

sub setInstitution {
    my ($self, $institution) = @_;
    $self->{'_institution'} = $institution;
}

sub getEditor {
    my $self = shift;
    return $self->{'_editor'};
}

sub setEditor {
    my ($self, $editor) = @_;
    $self->{'_editor'} = $editor;
}

sub getNote {
    my $self = shift;
    return $self->{'_note'};
}

sub setNote {
    my ($self, $note) = @_;
    $self->{'_note'} = $note;
}


##
# Build a marker based on the author list and publication year.
# This should be used when no marker was found during citation
# segmentation.
##
sub buildAuthYearMarker() {
    my $self = shift;
    my @authors = $self->getAuthors();
    my @lastNames = ();
    foreach my $auth (@authors) {
	my @toks = split " +", $auth;
	push @lastNames, $toks[$#toks];
    }
    my $year = $self->getDate();
    map { $_ =~ s/_/ /g } @lastNames;
    return join ", ", @lastNames, $year;

} # buildAuthYearMarker


1;
