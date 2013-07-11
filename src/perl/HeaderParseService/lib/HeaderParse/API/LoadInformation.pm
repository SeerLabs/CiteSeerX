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
package HeaderParse::API::LoadInformation;
#this is the file containing major global variants for the file TokenRepresentation_Modual.pl

use utf8;
require Exporter;
use vars qw($VERSION @ISA @EXPORT @EXPORT_OK %EXPORT_TAGS);

use HeaderParse::Config::API_Config qw($Database_Dir $Data_Dir);

@ISA = qw(Exporter); # important!!
@EXPORT =  qw($debug %dictH %nameH %firstnameH %lastnameH %monthH %affiH %addrH %introH %phoneH %degreeH %pubnumH %noteH %pageH %conjH %prepH %postcodeH %cityH %stateH %countryH %keywordH %abstractH %emailH %urlH $counter %tagMap %InverseTagMap %InverseSVMMap @typeArray @tagArray %typeHash %InverseTypeArray %FeatureDictH %BasicFeatureDictH);

######################Global Variable################################
$debug = 0; #print debug message or not


$dict = "$Database_Dir/words";
$firstname = "$Database_Dir/firstNames.txt";
$lastname = "$Database_Dir/surNames.txt";
$ChineseSurname = "$Database_Dir/ChineseSurNames.txt";
$month = "$Database_Dir/month.txt";
$affi = "$Database_Dir/affi.txt";
$addr = "$Database_Dir/addr.txt";

$intro = "$Database_Dir/intro.txt";
$phone = "$Database_Dir/phone.txt";
$degree = "$Database_Dir/degree.txt";
$pubnum = "$Database_Dir/pubnum.txt";
$note = "$Database_Dir/note.txt";
$page = "$Database_Dir/page.txt";

$postcode = "$Database_Dir/postcode.txt";
$cityname = "$Database_Dir/cityname.txt";
$statename = "$Database_Dir/statename.txt";
$countryname = "$Database_Dir/countryname.txt";
$abstract = "$Database_Dir/abstract.txt";
$keyword = "$Database_Dir/keyword.txt";
$email = "$Database_Dir/email.txt";
$url = "$Database_Dir/url.txt";
######################Databases######################################


%tagMap = (title => 1,
           author => 2,
           affiliation => 3,
           address => 4,
           note => 5,
           email => 6,
           date => 7,
           abstract => 8,
           intro => 9,
           phone => 10,
           keyword => 11,
           web => 12,
           degree => 13,
           pubnum => 14,
           page => 15,
#	   plaintext => 16,
	   others =>16
);


%InverseTagMap = (
	   1 => 'title',
           2 => 'author',
           3 => 'affiliation',
           4 => 'address',
           5 => 'note',
           6 => 'email',
           7 => 'date',
           8 => 'abstract',
           9 => 'intro',
           10 => 'phone',
           11 => 'keyword',
           12 => 'web',
           13 => 'degree',
           14 => 'pubnum',
           15 => 'page',
	#   16 => plaintext,
	   16 => 'others'
);

%InverseSVMMap = (
	1 => 'Title',
	2 => 'Author',
	3 => 'Affi',
	4 => 'Addr',
	5 => 'Note',
	6 => 'email',
	7 => 'date',
	8 => 'ABS',
	9 => 'intro',
	10 => 'phone',
	11 => 'keyword',
	12 => 'web',
	13 => 'degree',
	14 => 'pub',
	15 => 'page',
#	16 => 'plaintext',
	16 => 'others'
);

@typeArray = ("Title", "Author", "Affi","Addr");
@tagArray = ("TitleTag", "AuthorTag", "AffiTag", "AddrTag", "OtherTag");

%typeHash = (
   "Title" => "1",
   "Author" => "2",
   "Affi" => "3",
   "Addr" => "4",
   "Others" => "5"
);

%InverseTypeArray = (
   "1" => "Title",
   "2" => "Author",
   "3" => "Affi",
   "4" => "Addr",
   "5" => "Others"
);


#read prep words and conj words in
%conjH = (
	and =>1,
	or =>1
);

%prepH = (
	of =>1,
	at =>1,
	in =>1
); 

%FeatureDictH = (
	CsenLen => {
		    ID =>1,
		    max =>0,
		    mean =>0
		    },
        CdateNumPer => {
		    ID =>2,
		    max =>0,
		    mean =>0
		       },
        CDictWordNumPer => {
		     ID =>3,
		     max =>0,
		     mean =>0
			   },
        CNonDictWordNumPer => {
		     ID =>4,
		     max =>0,
		     mean =>0
			  },
	CCap1DictWordNumPer => {
		     ID =>5,
		     max =>0,
		     mean =>0
			   },
        CCap1NonDictWordNumPer => {
		     ID =>6,
		     max =>0,
		     mean =>0
			  },
        CdigitNumPer => {
		     ID =>7,
		     max =>0,
		     mean =>0
			 },
	CaffiNumPer => {
		     ID =>8,
		     max => 0,
		     mean =>0
			},
	CaddrNumPer =>{ 
		     ID =>9,
		     max =>0,
		     mean =>0
		       },
	CintroNumPer =>{ 
		     ID =>10,
		     max =>0,
		     mean =>0
		       },
	CphoneNumPer =>{ 
		     ID =>11,
		     max =>0,
		     mean =>0
		       },
	CdegreeNumPer =>{ 
		     ID =>12,
		     max =>0,
		     mean =>0
		       },
        CpubNumPer =>{ 
		     ID =>13,
		     max =>0,
		     mean =>0
		       },
        CnoteNumPer =>{ 
		     ID =>14,
		     max =>0,
		     mean =>0
		       },
        CpageNumPer =>{ 
		     ID =>15,
		     max =>0,
		     mean =>0
		       },
	CcapNumPer => {
		     ID =>16,
		     max =>0,
		     mean =>0
		       },
        CothersPer => {
		     ID =>17,
		     max =>0,
		     mean =>0
		       },
	ClinePos => {
		     ID =>18,
		     max =>0,
		     mean =>0
		     },
	FeatureCounter => 19 # original it is 18 
);

%BasicFeatureDictH = %FeatureDictH; # to avoid same name problems


$counter = 19; # because the initialized several features


#read lower cased dictwords into hash
open(dictFH, $dict) || die "SVMHeaderParse: could not open $dict to read \n";
while (my $line = <dictFH>) {
	$line =~ s/^\s+//g;
	$line =~ s/\s+$//g;
	if ($line !~ /^\s*$/) {	
		$dictH{lc($line)}++;
	}
}
close(dictFH);

open(emailFH, $email) || die "SVMHeaderParse: could not open $email to read \n";
while (my $line = <emailFH>) {
        $line =~ s/^\s+//g;
        $line =~ s/\s+$//g;
        if ($line !~ /^\s*$/) {
                $emailH{lc($line)}++;
        }
}
close(emailFH);

open(urlFH, $url) || die "SVMHeaderParse: could not open $url to read \n";
while (my $line = <urlFH>) {
        $line =~ s/^\s+//g;
        $line =~ s/\s+$//g;
        if ($line !~ /^\s*$/) {
                $urlH{lc($line)}++;
        }
}
close(urlFH);

open(monthFH, $month) || die "SVMHeaderParse: could not open $month to read \n";
while (my $line = <monthFH>) {
        $line =~ s/^\s+//g;
        $line =~ s/\s+$//g;
        if ($line !~ /^\s*$/) {
                $monthH{lc($line)}++;
        }
}
close(monthFH);

#read names into hash
open(FnameFH, $firstname) || die "SVMHeaderParse: could not open $firstname to read \n";
while (my $line = <FnameFH>) {
	if (($line !~ /^\#/) && ($line !~ /^\s*$/)) { 
        	$line =~ s/^\s+//g;
        	$line =~ s/\s+$//g;
        	$nameH{lc($line)}++;
		$firstnameH{lc($line)}++;
	}
}
close(FnameFH);

open(LnameFH, $lastname) || die "SVMHeaderParse: could not open $lastname to read \n";
while (my $line = <LnameFH>) {
	if (($line !~ /^\#/) && ($line !~ /^\s*$/)) {   
                $line =~ s/^\s+//g;
                $line =~ s/\s+$//g;
                $nameH{lc($line)}++;
		$lastnameH{lc($line)}++;
        }
}
close(LnameFH);

open(ChinameFH, $ChineseSurname) || die "SVMHeaderParse: could not open $ChineseSurname to read \n";
while (my $line = <ChinameFH>) {
	if (($line !~ /^\#/) && ($line !~ /^\s*$/)) {   
                $line =~ s/^\s+//g;
                $line =~ s/\s+$//g;
                $nameH{lc($line)}++;
		$lastnameH{lc($line)}++;
        }
}
close(ChinameFH);

# this is to read the candidate affilication words in, whch has noise
# of is said to be the location words now
# & is useful, at, and, need to incorporate them
open(affiFH, "$affi") || die "SVMHeaderParse: could not open $affi to read \n";
while (my $line = <affiFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $affiH{lc($1)}++;
  }
}
close(affiFH);


open(addrFH, "$addr") || die "SVMHeaderParse: could not open $addr to read \n";
while (my $line = <addrFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $addrH{lc($1)}++;
  }
}
close(addrFH);


open(introFH, "$intro") || die "SVMHeaderParse: could not open $intro to read \n";
while (my $line = <introFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $introH{lc($1)}++;
  }
}
close(introFH);

open(phoneFH, "$phone") || die "SVMHeaderParse: could not open $phone to read \n";
while (my $line = <phoneFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $phoneH{lc($1)}++;
  }
}
close(phoneFH);


open(degreeFH, "$degree") || die "SVMHeaderParse: could not open $degree to read \n";
while (my $line = <degreeFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $degreeH{lc($1)}++;
  }
}
close(degreeFH);

open(pubnumFH, "$pubnum") || die "SVMHeaderParse: could not open $pubnum to read \n";
while (my $line = <pubnumFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $pubnumH{lc($1)}++;
  }
}
close(pubnumFH);

open(noteFH, "$note") || die "SVMHeaderParse: could not open $note to read \n";
while (my $line = <noteFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $noteH{lc($1)}++;
  }
}
close(noteFH);

open(pageFH, "$page") || die "SVMHeaderParse: could not open $page to read \n";
while (my $line = <pageFH>) {
  if ($line =~ /^\d+\s+(.*)$/) {
      $pageH{lc($1)}++;
  }
}
close(pageFH);

open(cityFH, "$cityname") || die "SVMHeaderParse: could not open $cityname to read\n";
while (my $line = <cityFH>) {
  $line =~ s/^\s+//g;
  $line =~ s/\s+$//g;
  if ($line !~ /^\s*$/) {
    $cityH{lc($line)}++;
  }
}
close(cityFH);

# put the state name and the postcode into a hash
# except the postcode, we store other information of the small case.
open(stateFH, "$statename") || die "SVMHeaderParse: could not open $statename to read\n";
while (my $line = <stateFH>) {
  $line =~ s/^\s+//g;
  $line =~ s/\s+$//g;
  if (($line !~ /^\s*$/) && ($line !~ /^\#/)) {
    my ($state, $postcode) = split(/\s*\,\s*/, $line);
    $stateH{lc($state)}++;
    $postcodeH{$postcode}++;
  }
}
close(stateFH);

open(countryFH, "$countryname") || die "SVMHeaderParse: could not open $countryname to read\n";
while (my $line = <countryFH>) {
  $line =~ s/^\s+//g;
  $line =~ s/\s+$//g;
  $countryH{lc($line)}++;
}
close(countryFH);

open(abstractFH, "$abstract") || die "SVMHeaderParse: could not open $abstract to read \n";
while (my $line = <abstractFH>) {
      $line =~ s/^\s+//g;
      $line =~ s/\s+$//g;
      if ($line !~ /^\s*$/) {
	$abstractH{lc($line)}++;
      }
  }
close(abstractFH);

open(keywordFH, "$keyword") || die "SVMHeaderParse: could not open $keyword to read \n";
while (my $line = <keywordFH>) {
      $line =~ s/^\s+//g;
      $line =~ s/\s+$//g;
      if ($line !~ /^\s*$/) {
        $keywordH{lc($line)}++;
      }
  }
close(keywordFH);

1;
