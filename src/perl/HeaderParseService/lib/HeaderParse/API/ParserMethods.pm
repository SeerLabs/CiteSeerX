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
package HeaderParse::API::ParserMethods;

#06/27/2003, start to make this program to handle real data. So there is no evaluation, and off line classifiers should be trained be trained beforehand.
#02/10/2004 Apply to citeseer data (with the same format of EbizSearch data)

use utf8;
use Data::Dumper;
use FindBin;
use HeaderParse::API::NamePatternMatch;
use HeaderParse::API::MultiClassChunking; #default to use all export by this module
use HeaderParse::API::LoadInformation;
use HeaderParse::Config::API_Config;
use HeaderParse::API::AssembleXMLMetadata;
use vars qw($debug %dictH %nameH %firstnameH %lastnameH %BasicFeatureDictH %InverseTagMap);
use vars qw($SVM_Dir $offlineD $Tmp_Dir $nMinHeaderLength $nMaxHeaderLength);
use HeaderParse::API::Function qw(&AddrMatch &printDict &GenTrainVecMatrix &LineFeatureRepre &FillSpace &SeparatePunc);

my $FeatureDictH = \%BasicFeatureDictH;
my $ContextFeatureDictH;
my $SpaceAuthorFeatureDictH; #do not know if it is OK to define a hash
my $PuncAuthorFeatureDictH;
my $NameSpaceTrainVecH;
my $NameSpaceTrainF = "$offlineD"."NameSpaceTrainF";
my $SVMNameSpaceModel = "$offlineD"."NameSpaceModel";
my $TestH;
my $TrainH;
my $TotalHea = 0;

my $timestamp;

my $learner = "${SVM_Dir}svm_learn";
my $Classifier = "${SVM_Dir}svm_classify";

#my $offlineD = "../../offline/";
#my $TestOutF = "$TestF"."\.parsed";
#my $tmpCacheVecB = "$Tmp_Dir/tmpVec";
#my $SVMTmpResultB = "$Tmp_Dir/tmpresult";

my $FeatureDict = "$offlineD"."WrapperBaseFeaDict";
my $ContextFeatureDict = "$offlineD"."WrapperContextFeaDict";
my $SpaceAuthorFeatureDictF = "$offlineD"."WrapperSpaceAuthorFeaDict";
my $PuncAuthorFeatureDictF = "$offlineD"."WrapperPuncAuthorFeaDict";

my $linear = 1; # just want to be fast

my %evalH; # global hash to record classification result for baseline, each context round and IE
my $norm = 1;
my $testp = 1; # this is only to make the program run, no meaning.


my %TestDataIndex; #It indexes the header no in the testing dataset

#Read dictionary files
undef $/;
open(dumpFH, "$FeatureDict") || die "SVMHeaderParse: could not open $FeatureDict to read: $!";
my $string = <dumpFH>;
close(dumpFH);
eval $string;
$FeatureDictH = $VAR1;
$string ="";

open(dumpFH, "$ContextFeatureDict") || die "SVMHeaderParse: could not open $ContextFeatureDict to read: $!";
$string = <dumpFH>;
close(dumpFH);
eval $string;
$ContextFeatureDictH = $VAR1;
$string ="";

open(dumpFH, "$SpaceAuthorFeatureDictF") || die "SVMHeaderParse: could not open $SpaceAuthorFeatureDictF to read: $!";
$string = <dumpFH>;
close(dumpFH);
eval $string;
$SpaceAuthorFeatureDictH = $VAR1;
$string ="";
$/ = "\n";
#End read dictionary files


sub Parse{
    my $header=shift;
    $timestamp = shift;
    my $success = 0;
   # $tmpCacheVec = $tmpCacheVec . "\_$timestamp\_";
   # $SVMTmpResult = $SVMTmpResult . "\_$timestamp\_";
    my $tmpCacheVec = "$Tmp_Dir/tmpVec"."\_$timestamp\_";

    my $SVMTmpResult = "$Tmp_Dir/tmpresult"."\_$timestamp\_";
    $TestH = &HashEbizHeader(\$header);
    $TestH = &VectorizeUnknownHeaderLine($TestH);

    my $baseline = 1;
    $TestH = &LineClassify($testp, "", $baseline, $FeatureDictH,
			   $TestH, $tmpCacheVec, $SVMTmpResult);
    $TestH = &UpdatePretag($TestH);

    my $maxLoop = 2;
    for my $loop(1 .. $maxLoop) {
        $baseline = 0;
        my $NowContext = "context"."$loop";

        $TestH = &LineClassify($testp, $NowContext, $baseline,
			       $ContextFeatureDictH, $TestH,
			       $tmpCacheVec, $SVMTmpResult);
        $TestH = &UpdatePretag($TestH);
    }

    #Phase 2: Extraction Information from Multi-Class Lines and Author Lines Chunks
    my $LastContext = "context"."$maxLoop";

    # BUG: InfoExtract hangs on some documents.
    # this is reproducible with data extracted using TET from doc 654835
    # from the legacy citeseer system.
    eval {
	local $SIG{'ALRM'} = sub { die "alarm\n"; };
	alarm 15;
	$TestH = &InfoExtract($testp, $TestH,$SpaceAuthorFeatureDictH, $PuncAuthorFeatureDictH, $SVMNameSpaceModel, $tmpCacheVec, $SVMTmpResult);
	alarm 0;
    };
    if ($@) {
	if ($@ eq "alarm\n") {
	    return 0;
	}
    }
    $rXML = &ExportRDF($TestH);

    for my $i(1..15){
	unlink "$Tmp_Dir/tmpVec\_$timestamp\_test$i";
	unlink "$Tmp_Dir/tmpresult\_$timestamp\_$i";
    }
    return $rXML;
}


# This is the header extraction module from CiteSeer.
# Only the parts related to header extraction is used.
sub ExtractHeaderInformation {
    my $papertext = shift;
    my $header='';

    if (!(length($$papertext))){
	return ('Paper text is empty');
    }

#  $$papertext =~ s/<[SEFC][\d\.e\+\-]*>//sgi; # remove S|E|F|C tags

    if ($$papertext =~ /^(.*?\b(?:Introduction|INTRODUCTION|Contents|CONTENTS)(?:.*?\n){6})/s) {
	$header = $1;
    } else {
	my $nLines = 150;
	my @lines = split '\n', $$papertext;
	my $contentLines = 0;
	for (my $i=0; $i<=$#lines; $i++) {
	    if ($lines[$i] !~ m/^\s*$/) {
		$contentLines++;
	    }
	    $header .= $lines[$i]."\n";
	    if ($contentLines >= $nLines) {
		last;
	    }
	}
    }

#  if ($$papertext =~ /^(.*?)\b(?:Abstract|ABSTRACT|Introduction|INTRODUCTION|Contents|CONTENTS|[Tt]his\s+(paper|memo|technical|article|document|report|dissertation))\b/s) { $header = $1; }
#  elsif ($$papertext =~ /^(.*?)\n[\d\.\s]*(Reference|Bibliography)/si) { $header = $1; }
#  else{
#      return ('Header could not be extracted');
#  }

    if ((defined $header) && (length ($header) > $nMaxHeaderLength)) {
	$header = substr ($header, 0, $nMaxHeaderLength) . '...';
    }
    if (length($header) < $nMinHeaderLength) {
	return ('Header could not be extracted');
    }
    return ('',$header);
}


sub UpdatePretag() {
    my $testH = shift;
 #   foreach my $testHea(sort {$a <=> $b} keys %{$testH}) {
	foreach my $LN(sort {$a <=> $b} keys %{$testH}) {
	    delete($$testH{$LN}{Pretag});
	    if ($$testH{$LN}{PClass} eq "s") {
		$$testH{$LN}{Pretag}{$$testH{$LN}{PSClsName}} = 1;
	    }elsif ($$testH{$LN}{PClass} eq "m") {
		foreach my $mytag(keys %{$$testH{$LN}{PClsName}}) {
		    $$testH{$LN}{Pretag}{$mytag} = 1;
		}
	    }
	}
  #  }
    return($testH);
}


#input: the file with all Training and testing samples
#output: $HeaderH{$HeaNO}{$LineNO} = "";
sub HashAllHeader() {
    my $simulateHeaNum = shift;
    my $tagF= shift;
    my %HeaH = ();
    my $HeaNO = 1; #start from 1
    my $LineNO = 1;

    open(tagFH, "$tagF") || die "SVMHeaderParse: could not open tag file\: $tagF to read: $!";
    while (my $line = <tagFH>) {
	$line =~ s/\+L\+//g;
	$line =~ s/^\s+//g;
	$line =~ s/\s+$//g;

	if ($line =~ /^\s*\<NEW\_HEADER\>/) {
	    $HeaNO++;
	    $LineNO = 1;
	#remove the line with only tag like </author>
	}elsif (($line =~ /^\s*$/) || ($line =~ /^\<(\/)*(\w+)\>$/)) {
	    next;
	}else {
	    $HeaH{$HeaNO}{$LineNO}{RawContent} = $line;
	    $LineNO++;
	}

	if ($simulateHeaNum > 0 && $HeaNO >= $simulateHeaNum) {
	    last;
	}
    }
    close(tagFH);
    return($HeaNO, \%HeaH);
}


#HEADER_DID[1]
#TRECS: Developing a Web-based e-Commerce Business Simulation
#TRECS: Developing a Web-based
sub HashEbizHeader() {
    my $headerRef= shift;
    my %HeaH = ();
#    my $HeaNO = 1; #start from 1
    my $LineNO = 1;

    my @lines = split(/\n/, $$headerRef);
    my $line;

    #open(FH, "$F") || die "SVMHeaderParse: could not open file\: $F to read: $!";
    #while (my $line = <FH>) {
    foreach $line (@lines){
	$line =~ s/^\s+//g;
	$line =~ s/\s+$//g;

#	if ($line =~ /^\s*HEADER\_DID\[(\d+)\]/) {
#	    $HeaNO = $1;
#	    $LineNO = 1;
#	}elsif ($line !~ /^\s*$/) {
	    #$HeaH{$HeaNO}{$LineNO}{RawContent} = $line;
            $HeaH{$LineNO}{RawContent} = $line;
	    $LineNO++;
#	}
    }
    #close(FH);
    return(\%HeaH);
}


sub BaseLineTrainSys() {
  my $HeaderH = shift;
  my $FeatureDictH = shift;

  my %InitialHash = ();
  $InitialHash{FeatureCounter} = 0;

  my $PuncAuthorDictH = \%InitialHash;
  my $SpaceAuthorDictH;
  #this is the place to generate feature dictionrauy and name pattern dictionary
  ($HeaderH, $FeatureDictH, $SpaceAuthorDictH) = &FormFeaDict($HeaderH, $FeatureDictH);
  #Prune features in Dictionary with DF < 3
  $FeatureDictH = &PruneDict($FeatureDictH);

  #prune features not in the pruned dict from the feature vector
  foreach my $HeaNO (sort {$a <=> $b} keys %{$HeaderH}) {
    foreach my $line(sort {$a <=> $b} keys %{$$HeaderH{$HeaNO}}) {
      foreach my $fea(keys %{$$HeaderH{$HeaNO}{$line}{FeaVec}}) {
	if (! $$FeatureDictH{$fea}{ID})  {
	  delete ($$HeaderH{$HeaNO}{$line}{FeaVec}{$fea});
	}
      }

      if ($$HeaderH{$HeaNO}{$line}{FeaVec} ne "") {
	my $tmpFeaVec = "";
	foreach my $fea(sort{$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$HeaderH{$HeaNO}{$line}{FeaVec}}) {

	  if ($norm) {
	      #normalization
	      $$HeaderH{$HeaNO}{$line}{FeaVec}{$fea} = sprintf("%.8f", $$HeaderH{$HeaNO}{$line}{FeaVec}{$fea}/$$FeatureDictH{$fea}{max});
	  }

	  $tmpFeaVec .= "$$FeatureDictH{$fea}{ID}\:$$HeaderH{$HeaNO}{$line}{FeaVec}{$fea} ";
        }
	$$HeaderH{$HeaNO}{$line}{SVMFeaVec} = "$tmpFeaVec";
      }

    }
  }

  my %NameSpaceTrainVecH = (); #a separate hash for later printing
  my $Lcount = 0;
  #Prune acordingly features
  foreach my $HeaNO (sort {$a <=> $b} keys %{$HeaderH}) {
      foreach my $line(sort {$a <=> $b} keys %{$$HeaderH{$HeaNO}}) {
	  if (exists $$HeaderH{$HeaNO}{$line}{NamePattern}) {
	      foreach my $CandidateNamePattern(keys %{$$HeaderH{$HeaNO}{$line}{NamePattern}}) {
		  foreach my $fea(keys %{$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}}) {
		      if (! $$SpaceAuthorDictH{$fea}{ID})  {
			  delete($$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea});
		      }
		  }

		  #normalization
		  if ($$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec} ne "") {
		      $Lcount++;
		      my $tmpFeaVec = "$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{tag} ";
		      my $tmpTextVec = "$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{tag} ";

		      foreach my $fea(sort{$$SpaceAuthorDictH{$a}{ID} <=> $$SpaceAuthorDictH{$b}{ID}} keys %{$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}}) {
			  $$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea} = sprintf("%.8f", $$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea}/$$SpaceAuthorDictH{$fea}{max});
			  $tmpFeaVec .= "$$SpaceAuthorDictH{$fea}{ID}\:$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea} ";
			  $tmpTextVec .= "$fea\:$$HeaderH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea} ";
		      }
		      $NameSpaceTrainVecH{$Lcount}{SpaceNameVec}=$tmpFeaVec;
		      $NameSpaceTrainVecH{$Lcount}{SpaceTextNameVec}=$tmpTextVec; #for debugging
		  }
	      }
	  }
      }
  }

  return($HeaderH, $FeatureDictH, $PuncAuthorDictH, $SpaceAuthorDictH, \%NameSpaceTrainVecH);
}

sub ContextTrainSys() {
  my $FeatureDictH = shift;
  my $HeaderH = shift;

  foreach my $HeaNO (sort {$a <=> $b} keys %{$HeaderH}) {
    #assign neighour line's tag
    ($FeatureDictH, $$HeaderH{$HeaNO}) = &TrainAssignLineTag($FeatureDictH, $$HeaderH{$HeaNO});
  }
  return($FeatureDictH, $HeaderH);
}

#this is to write all the testing lines into one file to speed up
sub LineClassify() {
    my ($testp, $nowLoop, $baseline, $FeatureDictH,
	$HeaderH, $tmpCacheVec, $SVMTmpResult) = @_;
    my %memoryH = ();
    my $GlobalLineNO = 0;

    #step1: collect all test data and write into one file
    #       keep a hash to record the global lineNO and the header no its local line no
    # here is the file for all the testing data

    #  foreach my $testHea(sort {$a <=> $b} keys %{$HeaderH}) {
    if ($baseline) {
	#Filter feature vector by Feature Dictionary
	### $$HeaderH{$testHea} = &FormTestFeaVec($FeatureDictH, $$HeaderH{$testHea});
	$HeaderH = &FormTestFeaVec($FeatureDictH, $HeaderH);
    }else {
	$HeaderH = &TestAssignLineTag($FeatureDictH, $HeaderH);
    }

    foreach my $LN(sort {$a <=> $b} keys %{$HeaderH}) {
	if (! $baseline) {
	    #To make the iteration correct, we should initialize $$HeaderH{$testHea} by removing all the single and multiple classes in the hash
	    delete($$HeaderH{$LN}{PClass});
	    delete($$HeaderH{$LN}{PSClsName});
	    delete($$HeaderH{$LN}{PClsName});
	}elsif ($baseline && ($$HeaderH{$LN}{FeaVec} ne "")) {
	    #modify the feature vector(normalization)
	    my $tmpFeaVec = "";
	    foreach my $fea(sort{$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$HeaderH{$LN}{FeaVec}}) {
		if (exists ($$FeatureDictH{$fea}{ID})) {

		    if ($norm) {
			if ($debug) {
			    if ($$FeatureDictH{$fea}{max} == 0) {
				print STDERR "fea $fea has max value 0! \n";
			    }
			}
			$$HeaderH{$LN}{FeaVec}{$fea} = sprintf("%.8f", $$HeaderH{$LN}{FeaVec}{$fea}/$$FeatureDictH{$fea}{max});
		    }

		    $tmpFeaVec .= "$$FeatureDictH{$fea}{ID}\:$$HeaderH{$LN}{FeaVec}{$fea} ";
		}
	    }

	    $$HeaderH{$LN}{SVMFeaVec} = "$tmpFeaVec";

	    #be carefull here!!
	    if ($$HeaderH{$LN}{SVMFeaVec} eq "") {
		if ($debug) {
		    print STDERR "header($testHea) -- Line($LN) has a null feature vector ($$HeaderH{$testHea}{$LN}{RawContent}) \n";
		}
		next;
	    }
	}

	$GlobalLineNO++;
	$memoryH{$GlobalLineNO}{HeaNO} = $testHea;
	$memoryH{$GlobalLineNO}{LocalLineNO} = $LN;
    }
 #  }

    #step2:we print 15 files with labelled feature vectors
    for my $clsNO(1 .. 15) {
	my $testF = "$tmpCacheVec"."test"."$clsNO";
	open(testFH, ">$testF") || die "SVMHeaderParse: could not open $testF to write: $!";
	#      foreach my $HeaNO (sort {$a <=> $b} keys %{$HeaderH}) {
	foreach my $LN(sort {$a <=> $b} keys %{$HeaderH}) {
	    my $tag = 1; # just to conform to the format
	    if ($baseline) {
		print testFH "$tag $$HeaderH{$LN}{SVMFeaVec}\n";
	    }else {
		print testFH "$tag $$HeaderH{$LN}{ContextSVMFeaVec}\n";
		#print "context feature vec is $$HeaderH{$HeaNO}{$LN}{ContextSVMFeaVec}\n";
	    }
	}
	#     } # end of collecting all the testing data into a file
	close(testFH);
    }

    #step3: SVM classify
    for my $clsNO(1 .. 15) {
	my $testF = "$tmpCacheVec"."test"."$clsNO";
	my $mySVMResult = "$SVMTmpResult"."$clsNO";
	my $SVMModelF;
	my $printstr = "";
	if ($baseline) {
	    $printstr = "baseline";
	    $SVMModelF = "$offlineD"."$clsNO"."Model"."fold"."$testp";
	}else {
	    $printstr = "context"."$nowLoop";
	    $SVMModelF = "$offlineD"."$clsNO"."ContextModel"."fold"."$testp";
	}

#     print "classification result from fold($testp)-class($clsNO)-$printstr\:\n";
	system("$Classifier -v 0 $testF $SVMModelF $mySVMResult");
   }

    #step4:Read all the result into a hash
    my %SVMResultHash = ();
    my %OrphanTagAssignHash = (); #This records the accuracy of assigned tags
    my %NegMeanH = (); #record the mean of the negative value each classifier made
    my %PosMinH = ();

    for my $clsNO(1 .. 15) {
	my $mySVMResult = "$SVMTmpResult"."$clsNO";
	my $myLineNO = 0;

	#initialize %PosMinH 's value
	$PosMinH{$clsNO} = 100;

	open(mySVMResultFH, "$mySVMResult") || die "SVMHeaderParse: could not open $mySVMResult to read: $!";
	while (my $myline = <mySVMResultFH>) {
	    $myline =~ s/^\s+//g;
	    $myline =~ s/\s+$//g;
	    if ($myline !~ /^\s*$/) {
		$myLineNO++;
		if ($debug) {
		    print STDERR " current lineNo is $myLineNO and score for class $clsNO is $myline \n";
		}
		$SVMResultHash{$myLineNO}{$clsNO} = $myline;
		if ($myline < 0) {
		    $NegMeanH{$clsNO} += $myline;
		}else {
		    if ($PosMinH{$clsNO} > $myline) {
			$PosMinH{$clsNO} = $myline;
		    }
		}
	    }
	}

	if ($myLineNO < 1) {
	    if ($debug) {
		print STDERR "yahoo: $mySVMResult has myLineNO 0 \n";
	    }
	}else {
	    $NegMeanH{$clsNO} = sprintf("%.8f", $NegMeanH{$clsNO}/$myLineNO);
	}

	close(mySVMResultFH);
    }

    my $PredTagbyMinNeg = 0;
    my $PredValbyMinNeg = 100;
    my $PredTagbyMinPos = 0;
    my $PredValbyMinPos = 100;

    #analyze the results from the hash and fill the Test Hash(HeaderH)
    for my $myline(1 .. $GlobalLineNO) {
	my @PredictTags = ();
	my $minVal = 100;
	my $CandidateTag = -1;
	my $myHeaNO = $memoryH{$myline}{HeaNO};
	my $myLineNO = $memoryH{$myline}{LocalLineNO};

	for my $clsNO(1 .. 15) {
	    my $myresult = $SVMResultHash{$myline}{$clsNO};
	    #keep the classification results for multi-class line
	    $$HeaderH{$myLineNO}{ClassifyResult}{$clsNO} = $myresult;
	    if ($debug) {
		print STDERR "\t\t result by class $clsNO -- $result \n";
	    }
	    my $myRelDiv = 10;

	    if ($myresult > 0) {
		push @PredictTags, $clsNO;
	    }else {
		$myRelDiv = sprintf("%.8f", $myresult/$NegMeanH{$clsNO});
		if ($myRelDiv < $minVal) {
		    $minVal = $myRelDiv;
		    $CandidateTag = $clsNO;
		}
		if ( (0 - $myresult) < $PredValbyMinNeg) {
		    $PredValbyMinNeg = -$myresult;
		    $PredTagbyMinNeg = $clsNO;
		}
		if (($PosMinH{$clsNO}- $myresult) < $PredValbyMinPos) {
		    $PredValbyMinPos = $PosMinH{$clsNO}- $myresult;
		    $PredTagbyMinPos = $clsNO;
		}
	    }
	}
	#Assign ONLY class nearest to the hyperplane to the orphan point
	if ($#PredictTags < 0) {
	    push @PredictTags, $CandidateTag;
	    $OrphanTagAssignHash{TotalLineNum}++;
	}

	#Fill the hash with the classification result
	if ($#PredictTags eq 0) {
	    $$HeaderH{$myLineNO}{PClass} = "s";
	    $$HeaderH{$myLineNO}{PSClsName} = $PredictTags[0];
	}elsif ($#PredictTags > 0)  {
	    $$HeaderH{$myLineNO}{PClass} = "m";
	    # the multi tags predicted in one line has no sense of the order
	    for my $i(0 .. $#PredictTags) {
		$$HeaderH{$myLineNO}{PClsName}{$PredictTags[$i]} = 1;
		if ($debug) {
		    print STDERR "hea($myHeaNO)-- line($myLineNO) is classified as multi-class $PredictTags[$i] \n";
		}
	    }
	}else { #impossible
	    if ($debug) {
		print STDERR "hea($myHeaNO)-- line($myLineNO) is orphan\n";
	    }
	}
    }
    return($HeaderH);
}


#this is to
#(1) populate the predicted items(done in the LineClassify)
#(2) Extract related information from multi-author line and multi-classline
#all information to be extracted comes from {Pchunk}
#all word distribution information comes from {Pline} word dist.;

sub InfoExtract() {
    my $testp = shift;
    my $TestH = shift;
    my $PuncAuthorDictH = shift;
    my $SpaceAuthorDictH = shift;
    my $SVMNameSpaceModel = shift;
    my $tmpCacheVec = shift;
    my $SVMTmpResult = shift;

#  foreach my $testHea(sort {$a <=> $b} keys %{$TestH}) {
    foreach my $LN(sort {$a <=> $b} keys %{$TestH}) {
	if ($$TestH{$LN}{'PClass'} eq "s") { # single class
	    if  ($$TestH{$LN}{PSClsName} ne '2') { #non-author single class
		$$TestH{$LN}{Pchunk}{ChunkCounter}++;
		my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
		$$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = $$TestH{$LN}{PSClsName};
		$$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $$TestH{$LN}{PureText};
	    }else {
		if ($$TestH{$LN}{SClsWordCount} < 4) { #obvious single name
		    $$TestH{$LN}{Pchunk}{ChunkCounter}++;
		    my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
		    $$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
		    $$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $$TestH{$LN}{PureText};
		}else { #multi-authors
		    my $Tline = $$TestH{$LN}{RawContent};
		    $Tline =~ s/<(\/)*author>//g;
		    if ($debug) {
			print STDERR "predicted Multi-Author line -- $Tline \n";
		    }
		    my $NamePunc = 0;
		    #judge this is punctuated line or pure text-space
		    if (($$TestH{$LN}{PureText} =~ /([^\p{IsLower}\p{IsUpper}\s+\-\.\d+])/) || ($$TestH{$LN}{PureText} =~ /\band\b/i))  {
			#multi-class needs while ... $punc++;
			$NamePunc = 1;
		    }else {
			$NamePunc = 0;
		    }

		    if ($NamePunc) {
			#Heuristics bases separation based on features learned.
			if (($$TestH{$LN}{PureText} =~ /Jr|Dr/) && ($$TestH{$LN}{SClsWordCount} <5)) {
			    #this is only one name
			    $$TestH{$LN}{Pchunk}{ChunkCounter}++;
			    my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
			    $$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
			    $$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $$TestH{$LN}{PureText};
			}else {
			    my $nameStr = $$TestH{$LN}{PureText};
			    $nameStr =~ s/^\s+//g;
			    $nameStr =~ s/\s+$//g;
			    my @GuessedNames = split(/\,|\&|and/, $nameStr);
			    for my $i(0 .. $#GuessedNames) {
				#chunk starts from 1
				$GuessedNames[$i] =~ s/^\s+//g;
				$GuessedNames[$i] =~ s/\s+$//g;
				if ($GuessedNames[$i] !~ /^\s*$/) {
				    my @Nameparts = split(/\s+/, $GuessedNames[$i]);
				    if ($#Nameparts < 3) {
					$$TestH{$LN}{Pchunk}{ChunkCounter}++;
					my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
					$$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
					$$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $GuessedNames[$i];
				    }else {
					#space separated names [name1 name2 name3 and name4]
					my $PredictedNames = &HeaderParse::API::NamePatternMatch::NamePatternMatch($GuessedNames[$i]);
					if ($#$PredictedNames < 1){
					    #only 1/0 reasonable name pattern, take it
					    $$TestH{$LN}{Pchunk}{ChunkCounter}++;
					    my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
					    $$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
					    $$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $GuessedNames[$i];
					}else { #classify to predict
					    my $BestNamePattern = &PredictBestNamePattern($PredictedNames, $SVMNameSpaceModel, $SpaceAuthorDictH, $tmpCacheVec, $SVMTmpResult);
					    my @names = split(/<>/, $BestNamePattern);
					    for my $i(0 .. $#names) {
						$$TestH{$LN}{Pchunk}{ChunkCounter}++;
						my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
						$$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
						$$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $names[$i];
					    }
					}
				    }
				}
			    }
			}
		    }else {
			#name Space
			my $nameStr = $$TestH{$LN}{PureText};
			my $PredictedNames = &HeaderParse::API::NamePatternMatch::NamePatternMatch($nameStr);
			if ($#$PredictedNames < 1){
			    #only 1/0 reasonable name pattern, take the parser-decided chunks
			    my $tmp_name_container = $$PredictedNames[0];
			    if ($#$tmp_name_container > 0) {
				for my $kk(0 .. $#$tmp_name_container) {
				    $$TestH{$LN}{Pchunk}{ChunkCounter}++;
				    my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
				    $$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
				    $$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $$tmp_name_container[$kk];
				}
			    }else {
				#this branch is original
				$$TestH{$LN}{Pchunk}{ChunkCounter}++;
				my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
				$$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
				$$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $nameStr;
			    }
			}else {
			    #classify to predict
			    my $BestNamePattern = &PredictBestNamePattern($PredictedNames, $SVMNameSpaceModel, $SpaceAuthorDictH, $tmpCacheVec, $SVMTmpResult);
			    my @names = split(/<>/, $BestNamePattern);
			    for my $i(0 .. $#names) {
				$$TestH{$LN}{Pchunk}{ChunkCounter}++;
				my $ChunkPos = $$TestH{$LN}{Pchunk}{ChunkCounter};
				$$TestH{$LN}{Pchunk}{$ChunkPos}{cls} = 2;
				$$TestH{$LN}{Pchunk}{$ChunkPos}{content} = $names[$i];
			    }
			}
		    }
		}
	    }
	    #multiple class
	}elsif ($$TestH{$LN}{PClass} eq "m"){
	    my (%TagH, $emailChunkH, $URLChunkH, @ArrayofHash);
	    #get a hash of all tags
	    foreach my $tag(keys %{$$TestH{$LN}{PClsName}}) {
		$TagH{counter}++;
		$TagH{$tag}++;
	    }
	    my ($PuncNum, $SepH, $component) = &GetSeparatorIndex($$TestH{$LN}{PureText});
	    #Preprocess -- extract email and URL out
	    if ($$TestH{$LN}{PClsName}{6}) {
		#component has holes of "-1", after extracting emailchunk out
		($emailChunkH, $component) = &LocateEmailFromComponent($component);
		delete($TagH{6});
		$TagH{counter}--;
		push @ArrayofHash, $emailChunkH;
	    }
	    if ($$TestH{$LN}{PClsName}{12}) {
		($URLChunkH, $component) = &LocateURLFromComponent($component);
		delete($TagH{12});
		$TagH{counter}--;
		push @ArrayofHash, $URLChunkH;
	    }

	    if($TagH{counter} <1){ #no additional class
		#exception: what if still text left ???????
		$$TestH{$LN} = &FillChunkH($$TestH{$LN},$component, \@ArrayofHash);
		#tag each word
	    }elsif ($TagH{counter} == 1){
		#only one class left ..
		my $lastTag = "";
		foreach my $tag(keys %TagH) {
		    if ($tag ne "counter") {
			$lastTag = $tag;
		    }
		}
		#Get the rest possible chunks separated by the email and URL
		my $UnIdentifiedChunk = &LocateUnIdentifiedChunk($component);
		#Tag all the test chunk as the only left class
		foreach my $chunkNO(sort{$a<=>$b} keys %{$UnIdentifiedChunk}) {
		    $$UnIdentifiedChunk{$chunkNO}{cls} = $lastTag;
		}
		push @ArrayofHash, $UnIdentifiedChunk; #or\%myHash--must be pointer
		#fill in the TestH chunk in a ordered way and tag each word
		$$TestH{$LN} = &FillChunkH($$TestH{$LN}, $component, \@ArrayofHash);
		# two class module
	    }elsif ($TagH{counter} == 2) {
		#needs maping!
		my @TagsArray = ();
		foreach my $mytag(sort keys %TagH) {
		    if ($mytag ne "counter") {
			push @TagsArray, $mytag;
		    }
		}

		my $UnIdentifiedChunk = &LocateUnIdentifiedChunk($component);
		my $chunk1start = $$UnIdentifiedChunk{1}{startPos};
		my $chunk1end = $$UnIdentifiedChunk{1}{endPos};
		my $IdentifiedChunk;
		#continuous
		if ($$UnIdentifiedChunk{counter} == 1) {
		    my $offset;
		    my $newComponent = $component;
		    my $newSepH = $SepH;
		    if (($chunk1start == 0) &&  ($chunk1end == $#$component)) {
			$offset = 0;
		    }else {
			$offset = $chunk1start;
			#adjust $component and $SepH
			$newComponent = ();
			for my $tmpi($chunk1start .. $chunk1end) {
			    $$newComponent[$tmpi-$offset] = $$component[$tmpi];
			}

			foreach my $tmpSep(sort keys %{$newSepH}) {
			    if (($tmpSep >= $chunk1start) && ($tmpSep <= $chunk1end)) {
				my $newSep =  $tmpSep -  $offset;
				$$newSepH{$newSep} = $$newSepH{$tmpSep};
			    }
			    delete($$newSepH{$tmpSep});
			}
		    }

		    if ($PuncNum > 1) {
			$IdentifiedChunk = &Cont2ClassChunking($testp, \@TagsArray, "punc", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    }else {
			$IdentifiedChunk = &Cont2ClassChunking($testp, \@TagsArray, "space", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    }
		    #adjust back $chunk
		    if ($offset > 0) {
			foreach my $tmpi(sort keys %{$IdentifiedChunk}) {
			    $$IdentifiedChunk{$tmpi}{startPos} += $offset;
			    $$IdentifiedChunk{$tmpi}{endPos} += $offset;
			}
		    }
		    push @ArrayofHash, $IdentifiedChunk;
		    $$TestH{$LN} = &FillChunkH($$TestH{$LN}, $component, \@ArrayofHash);
		}elsif ($$UnIdentifiedChunk{counter} == 2) { #discrete
		    $IdentifiedChunk = &Disc2ClassChunking_2chunk($testp, \@TagsArray, $UnIdentifiedChunk, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    push @ArrayofHash, $IdentifiedChunk;
		    $$TestH{$LN} = &FillChunkH($$TestH{$LN}, $component, \@ArrayofHash);
		}elsif ($$UnIdentifiedChunk{counter} > 2) { #disc
		    if ($debug) {
			print STDERR "2 classes with 3+ chunks\n";
		    }
		}
		#see 3 and 4 as one class
	    }elsif (($TagH{counter} == 3) && $TagH{3} && $TagH{4}) {
		#tag array includes only 4 and the other tag
		my @TagsArray = ();
		foreach my $mytag(sort keys %TagH) {
		    if (($mytag ne "3") && ($mytag ne "4") && ($mytag ne "counter")) {
			push @TagsArray, $mytag;
		    }
		}
		push @TagsArray, 4;

		my $UnIdentifiedChunk = &LocateUnIdentifiedChunk($component);
		my $chunk1start = $$UnIdentifiedChunk{1}{startPos};
		my $chunk1end = $$UnIdentifiedChunk{1}{endPos};

		my $IdentifiedChunk;
		my $startPos34 = 0;
		my $endPos34 = 0;
		#continuous
		if ($$UnIdentifiedChunk{counter} == 1) {
		    my $offset;
		    my $newComponent = $component;
		    my $newSepH = $SepH;

		    if (($chunk1start == 0) &&  ($chunk1end == $#$component)) {
			$offset = 0;
		    }else {
			$offset = $chunk1start;
			#adjust $component and $SepH
			$newComponent = ();
			for my $tmpi($chunk1start .. $chunk1end) {
			    $$newComponent[$tmpi-$offset] = $$component[$tmpi];
			}

			foreach my $tmpSep(sort keys %{$newSepH}) {
			    if (($tmpSep >= $chunk1start) && ($tmpSep <= $chunk1end)) {
				my $newSep =  $tmpSep -  $offset;
				$$newSepH{$newSep} = $$newSepH{$tmpSep};
			    }
			    delete($$newSepH{$tmpSep});
			}
		    }

		    #find the boundary between 34 and the other tag
		    if ($PuncNum > 1) {
			$IdentifiedChunk = &Cont2ClassChunking($testp, \@TagsArray, "punc", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    }else {
			$IdentifiedChunk = &Cont2ClassChunking($testp, \@TagsArray, "space", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    }

		    #adjust back $chunk
		    #get the position of the 3 4
		    foreach my $tmpi(sort keys %{$IdentifiedChunk}) {
			if ($offset > 0) {
			    $$IdentifiedChunk{$tmpi}{startPos} += $offset;
			    $$IdentifiedChunk{$tmpi}{endPos} += $offset;
			}
			if ($$IdentifiedChunk{$tmpi}{cls} eq 4) {
			    $startPos34 = $$IdentifiedChunk{$tmpi}{startPos}; #absolute pos
			    $endPos34 = $$IdentifiedChunk{$tmpi}{endPos};
			    delete($$IdentifiedChunk{$tmpi});
			}
		    }
		    push @ArrayofHash, $IdentifiedChunk;

		}else { #if 2 discrete chunks
		    $IdentifiedChunk = &Disc2ClassChunking_2chunk($testp, \@TagsArray, $UnIdentifiedChunk, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		    foreach my $tmpi(sort keys %{$IdentifiedChunk}) {
			if ($$IdentifiedChunk{$tmpi}{cls} eq 4) {
			    $startPos34 = $$IdentifiedChunk{$tmpi}{startPos};
			    $endPos34 = $$IdentifiedChunk{$tmpi}{endPos};
			    delete($$IdentifiedChunk{$tmpi});
			}
		    }
		    push @ArrayofHash, $IdentifiedChunk;
		}

		#find the boundary between 3 and 4
		my $newComponent = (); #modified by Hui 03/19
		my $newSepH = $SepH;
		my $newPuncNum = 0;
		my $offset = $startPos34;
		for (my $tmpi=$startPos34; $tmpi<=$endPos34; $tmpi++) {
		    #modified by Hui 03/19/03 -$offset
		    $$newComponent[$tmpi-$offset] = $$component[$tmpi];
		    if ($$newComponent[$tmpi-$offset] =~ /^\W+$/) {
			$newPuncNum++;
		    }
		}

		if ($newPuncNum > 1) {
		    foreach my $tmpSep(sort keys %{$$newSepH{punc}}) {
			if (($tmpSep >= $startPos34) && ($tmpSep <= $endPos34)) {
			    my $newSep =  $tmpSep -  $offset;
			    $$newSepH{punc}{$newSep} = $$newSepH{punc}{$tmpSep};
			}
			delete($$newSepH{punc}{$tmpSep});
		    }
		}else {
		    foreach my $tmpSep(sort keys %{$$newSepH{space}}) {
			if (($tmpSep >= $startPos34) && ($tmpSep <= $endPos34)) {
			    my $newSep =  $tmpSep -  $offset;
			    $$newSepH{space}{$newSep} = $$newSepH{space}{$tmpSep};
			}
			delete($$newSepH{space}{$tmpSep});
		    }
		}

		my @NewTagsArray = ();
		push @NewTagsArray, 3;
		push @NewTagsArray, 4;
		if ($newPuncNum > 1) {
		    $IdentifiedChunk = &Cont2ClassChunking($testp, \@NewTagsArray, "punc", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		}else {
		    $IdentifiedChunk = &Cont2ClassChunking($testp, \@NewTagsArray, "space", $newSepH, $newComponent, $FeatureDictH, $tmpCacheVec, $SVMTmpResult);
		}

		#adjust back $chunk
		if ($offset > 0) {
		    foreach my $tmpi(sort keys %{$IdentifiedChunk}) {
			$$IdentifiedChunk{$tmpi}{startPos} += $offset;
			$$IdentifiedChunk{$tmpi}{endPos} += $offset;
		    }
		}
		push @ArrayofHash, $IdentifiedChunk;
		$$TestH{$LN} = &FillChunkH($$TestH{$LN}, $component, \@ArrayofHash);
	    }elsif ($TagH{counter} > 2) { #3+ cases.
		#consider about 3 discrete chunks for 3 tags????
		if ($debug) {
		    print STDERR "do not care yet -- here is the case for 3+ classes after preprocessing \n";
		    #find the most likely position and expand to arround some(like 3) words
		}
	    }
	}
    }
#  }

    return($TestH);
}


sub ExportInfo(){
    my $TestH = shift;
    my $outF = "output.txt";
    open(WRITER, ">$outF") || die "SVMHeaderParse: could not open $outF to write: $!";
    #  foreach my $testHea(sort {$a <=> $b} keys %{$TestH}) {
    print WRITER "headerno($testHea) -- ";
    foreach my $LN(sort {$a <=> $b} keys %{$TestH}) {
	print WRITER "lineno($LN)\: \n ";
	foreach my $chunk(sort {$a <=> $b} keys %{$$TestH{$LN}{Pchunk}}) {
	    if ($chunk ne "ChunkCounter") {
		print WRITER "\t chunk($chunk) -- class($$TestH{$LN}{Pchunk}{$chunk}{cls} <> content($$TestH{$LN}{Pchunk}{$chunk}{content} \n";
	    }
	}
    }
    # }
    close(WRITER);
}


sub ExportRDF(){
    my $TestH = shift;
    my $str='';
    my $tempStr='';
    foreach my $LN(sort {$a <=> $b} keys %{$TestH}) {
	foreach my $chunk(sort {$a <=> $b} keys %{$$TestH{$LN}{Pchunk}}) {
	    my $tag = $InverseTagMap{$$TestH{$LN}{Pchunk}{$chunk}{cls}};
	    my $content = $$TestH{$LN}{Pchunk}{$chunk}{content};
	    if ($content =~ /\w+/) {
		$str .="<$tag>$content</$tag>\n";
#                    if($tag =~/(url|note|date|abstract|intro|keyword|web|degree|pubnum|page)/){
#                      $tempStr .= "\n<cs_header:$tag>$content<\/cs_header:$tag>";
#                        }
	    }
	}
    }

#    print "RDF:\n\n $str\n";
#    print "$str\n";
    $rXML = &HeaderParse::API::AssembleXMLMetadata::assemble(\$str);
    return $rXML;
}


#Basic function: popuate information from line -- feature vector and class assignment and name patterns.
#no dictionary would be formed here
sub PopulateLineInfo4Header_unit() {
    my $HeaderH = shift;
    my %curState = ();

    foreach my $line(sort {$a <=> $b} keys %{$HeaderH}) {
	my $PureTextLine = $$HeaderH{$line}{RawContent};
	$PureTextLine =~ s/(\<)*\<(\/)*(\w+)\>(\>)*/ /g; # remove the tags
	$PureTextLine =~ s/\+L\+//g;
	$PureTextLine =~ s/^\s+//g;
	$PureTextLine =~ s/\s+$//g;
	#should make punctuation separate!
	$$HeaderH{$line}{PureText} = &SeparatePunc($PureTextLine);
	$$HeaderH{$line}{FeaVec} = &LineFeatureRepre($$HeaderH{$line}{PureText});
	#add the position of the line here!!!!
	$$HeaderH{$line}{FeaVec}{Clinepos} = $line;
	my $textFeaVec = "";
	foreach my $fea(keys %{$$HeaderH{$line}{FeaVec}}) {
	    if($$HeaderH{$line}{FeaVec}{$fea} == 0) {
		delete ($$HeaderH{$line}{FeaVec}{$fea});
	    }else {
		$textFeaVec .= "$fea($$HeaderH{$line}{FeaVec}{$fea})  ";
	    }
	}
	$$HeaderH{$line}{TextFeaVec} = $textFeaVec; # for read and debug

	#assign class tag to each line -- not separator <<sep>><</sep>> here
	if ($$HeaderH{$line}{RawContent} =~ /([^\<]+|(^\s*))\<(\/)*(\w+)\>($|[^\<]+)/) {
	    %curState = ();
	    my $tmpIndex = 0; # the order of this tag showed up last time
	    my $preTag = -1;
	    my $mul = 0;
	    while ($$HeaderH{$line}{RawContent} =~ /([^\<]+|(^\s*))\<(\/)*(\w+)\>($|[^\<]+)/g) {
		$tmpIndex++;
		my $tmptag = $4;
		if (($preTag > 0) && ($preTag ne $tagMap{$tmptag})) {
		    $mul = 1;
		}
		$curState{$tagMap{$tmptag}} = $tmpIndex;
		$preTag = $tagMap{$tmptag};
	    }

	    if ($mul) {
		$$HeaderH{$line}{TClass} = "m";
		my $order = 1;
		foreach my $tag(sort {$curState{$a} <=> $curState{$b}} keys %curState)         {
		    $$HeaderH{$line}{MClsName}{$tag} = $order;
		    $order++;
		}

		#represent the class distribution only for this multi-class case.
		my $Tline = $$HeaderH{$line}{RawContent};
		#main purpose is to combine </phone><email> as one <s>
		$Tline =~ s/\<(\/)*(\w+)\>/<s>/g; #replace the tags with <s>
		$Tline =~ s/^\s*<s>\s*//g;
		$Tline =~ s/\s*<s>\s*$//g;
		$Tline =~ s/<s>\s*<s>/<s>/g;
		$Tline =~ s/\s+/ /g;

		$Tline = &SeparatePunc($Tline);

		while ($Tline =~ /(\s+(\W+)\s+<s>)/g) {
		    my $whole = $1;
		    my $punc = $2;
		    $punc =~ s/^\s+//g;
		    $punc =~ s/\s+$//g;

		    if ($punc eq "\|") {
			$Tline =~ s/\|/\!\!\!/g;
			$whole =~ s/\|/\!\!\!/g;
		    }
		    $Tline =~ s/$whole/<<sep>>$punc<<\/sep>>/g; #only once no "g"
		    if ($punc eq "\|") {
			$Tline =~ s/\!\!\!//g;
			$whole =~ s/\!\!\!//g;
		    }
		}
		while ($Tline =~ /(<s>\s+(\W+)\s+)/g) {
		    my $whole = $1;
		    my $punc = $2;
		    $punc =~ s/^\s+//g;
		    $punc =~ s/\s+$//g;
		    if ($punc eq "\|") {
			$Tline =~ s/\|/\!\!\!/g;
			$whole =~ s/\|/\!\!\!/g;
		    }
		    $Tline =~ s/$whole/<<sep>>$punc<<\/sep>>/g; #only once no "g"
		    if ($punc eq "\|") {
			$Tline =~ s/\!\!\!/\|/g;
			$whole =~ s/\!\!\!/\|/g;
		    }
		}
		$Tline =~ s/<s>/<<sep>><<\/sep>>/g;
		my ($PuncNum, $SepH, $component) = &GetSeparatorIndex($Tline);
		#Populate Truth Hash by the chunk and word-class distribution
		$$HeaderH{$line} = &AssignWordTagFromChunk($$HeaderH{$line}, $SepH, $component);
	    }else {
		$$HeaderH{$line}{TClass} = "s";
		my @Tarr = split(/\s+/, $PureTextLine);
		$$HeaderH{$line}{SClsWordCount} = $#Tarr +1;
		foreach my $tag(sort {$curState{$a} <=> $curState{$b}} keys %curState) {
		    $$HeaderH{$line}{SClsName} = $tag;
		}

		#Fill in the word-class distribution for single class line
		my $lineContent = &SeparatePunc($$HeaderH{$line}{PureText});
		my @wordArray = split(/\s+/, $lineContent);
		undef $lineContent;

		$$HeaderH{$line} = &AssignWordTag4SingleClassLine("truth", $$HeaderH{$line}{SClsName}, $$HeaderH{$line}, \@wordArray);

		#but only multi-author has multiple chunks
		#all reasonable name patterns for space separated names
		#feature vec for each space namepatterns and puncutation separators
		#Test/prediction will base on the predicted line tag in another module

		#single author
		if ($$HeaderH{$line}{SClsName} eq "2") {
		    #From Truth
		    if ($$HeaderH{$line}{RawContent} !~ /<<sep>>/) {
			#could we save space by indicating the pure text directly
			$$HeaderH{$line}{Tchunk}{$i}{cls} = 2;
			$$HeaderH{$line}{Tchunk}{$i}{content} = $$HeaderH{$line}{PureText};
		    #multiple authors
		    }else {
			my $Tline = $$HeaderH{$line}{RawContent};
			$Tline =~ s/<(\/)*author>//g;

			my ($PuncNum, $SepH, $component) = &GetSeparatorIndex($Tline);
			my $nameStr = join(" ", @$component);

			#judge this is punctuated line or pure text-space
			if ($$HeaderH{$line}{PureText} =~ /([^\p{IsLower}\p{IsUpper}\s+\-\.\d+])|(\W+and\W+)/ig)  {
			    #multi-class needs while ... $punc++;
			    $$HeaderH{$line}{NamePunc} = 1;
			}else {
			    $$HeaderH{$line}{NameSpace} = 1;
			}

			#{NamePuncFeaVec} and {NameSpaceFeaVec} based on number of puncs (>2)
			#{MulClsPuncFeaVec}

			######common to both name space and name punc ######
			my $TrueNames = &HeaderParse::API::NamePatternMatch::GetTrueName($nameStr);
			for my $i(0 .. $#$TrueNames) {
			    my $j = $i+1; #chunk should start from 1
			    $$HeaderH{$line}{Tchunk}{$j}{cls} = 2;
			    $$HeaderH{$line}{Tchunk}{$j}{content} = "$$TrueNames[$i]";
			}
			################################################

			if ($$HeaderH{$line}{NamePunc}) {
			}else {
			    my $PredictedNames = &HeaderParse::API::NamePatternMatch::NamePatternMatch($nameStr);
			    if ($#$PredictedNames < 1) {
				#only one pattern -- do not fill name pattern
			    }else {
				my $TrueIndex = &HeaderParse::API::NamePatternMatch::Duplicate($TrueNames, $PredictedNames);
				#must solve the problem
				if ($TrueIndex eq "-1") {
				    if ($debug) {
					print STDERR "here the true name($TrueNames) is null from the line $content \n";
				    }
				}else {
				    #populate all reasonable name patterns
				    for my $i(0 .. $#$PredictedNames) {
					my $candidateName = "";
					for my $j(0 .. $#{$$PredictedNames[$i]}) {
					    if ($$PredictedNames[$i][$j]) {
						$candidateName .= "$$PredictedNames[$i][$j]<>";
					    }
					}
					#    print "candidate name\: $candidateName ";
					$$HeaderH{$line}{NamePattern}{$candidateName}{content} = $candidateName;
					($$HeaderH{$line}{NamePattern}{$candidateName}{SpaceNameVec}) = &SpaceNameLnFeaRepre_unit($candidateName);
					if ($i eq $TrueIndex) {
					    $$HeaderH{$line}{NamePattern}{$candidateName}{tag} = 1;
					}else {
					    $$HeaderH{$line}{NamePattern}{$candidateName}{tag} = -1;
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}else { #if there is no explicit tag for this line, this line only belongs to the last class of the previous line
	    my $tmpI = 0;
	    foreach my $state (sort {$curState{$b} <=> $curState{$a}} keys %curState) {
		if ($tmpI > 0) {
		    delete ($curState{$state});
		} #only keep the last tag
		$tmpI++;
	    }
	    $$HeaderH{$line}{TClass} = "s";
	    foreach my $tag(sort {$curState{$a} <=> $curState{$b}} keys %curState) {
		$$HeaderH{$line}{SClsName} = $tag;
	    }
	}
    }

    return($HeaderH);
}


sub VectorizeUnknownHeaderLine () {
  my $HeaderH = shift;

  my %curState = ();
  foreach my $line(sort {$a <=> $b} keys %{$HeaderH}) {
      my $PureTextLine = $$HeaderH{$line}{RawContent};
#      print "LINE $line: $PureTextLine\n";
      $PureTextLine =~ s/^\s+//g;
      $PureTextLine =~ s/\s+$//g;
      #should make punctuation separate!
      $$HeaderH{$line}{PureText} = &SeparatePunc($PureTextLine);

      my @Tarr = split(/\s+/, $PureTextLine);
      $$HeaderH{$line}{SClsWordCount} = $#Tarr +1;
      $$HeaderH{$line}{FeaVec} = &LineFeatureRepre($$HeaderH{$line}{PureText});
#      foreach my $key (keys %{$$HeaderH{$line}{FeaVec}}) {
#	  print "$key :: ".${$$HeaderH{$line}{FeaVec}}{$key}."\n";
#      }
#      print "\n";
      #add the position of the line here!!!!
      $$HeaderH{$line}{FeaVec}{Clinepos} = $line;

      my $textFeaVec = "";
      foreach my $fea(keys %{$$HeaderH{$line}{FeaVec}}) {
	  if($$HeaderH{$line}{FeaVec}{$fea} == 0) {
	      delete ($$HeaderH{$line}{FeaVec}{$fea});
	  }else {
	      $textFeaVec .= "$fea($$HeaderH{$line}{FeaVec}{$fea})  ";
	  }
      }
      $$HeaderH{$line}{TextFeaVec} = $textFeaVec; # for read and debug
  }

  return($HeaderH);
}


#training data are assigned the true neighbour lines' tag
sub TrainAssignLineTag() {
    my $FeatureDictH = shift;
    my $HeaderH = shift;
    my %curState = ();

    foreach my $line(sort {$a <=> $b} keys %{$HeaderH}) {
	my $PC = 1; # 0 means the tag for current line (which might be useful)
	my $Pline = $line - $PC;
	while (($PC < 5) && ($Pline > 0)) { #previous line
	    if (exists $$HeaderH{$Pline}{TClass}) {
		if ($$HeaderH{$Pline}{TClass} eq "s") {
		    my $ContextFea = "P"."$PC"."$$HeaderH{$Pline}{SClsName}";
		    if (! $$FeatureDictH{$ContextFea}{ID}) {
			$$FeatureDictH{FeatureCounter}++;
			$$FeatureDictH{$ContextFea}{ID} = $$FeatureDictH{FeatureCounter};
			$$FeatureDictH{$ContextFea}{max} = 0.5;
		    }

		    if ($$FeatureDictH{$ContextFea}{ID}) {
			$$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
			$$FeatureDictH{$ContextFea}{DF}++;
		    }
		}else { # consider the order of the tag
		    foreach my $tag(sort {$$HeaderH{$Pline}{MClsName}{$a} <=> $$HeaderH{$Pline}{MClsName}{$b}} keys %{$$HeaderH{$Pline}{MClsName}}){
			my $ContextFea = "P"."$PC"."$tag";
			if (! $$FeatureDictH{$ContextFea}{ID}) {
			    $$FeatureDictH{FeatureCounter}++;
			    $$FeatureDictH{$ContextFea}{ID} = $$FeatureDictH{FeatureCounter};
			    $$FeatureDictH{$ContextFea}{max} = 0.5;
			}
			if ($$FeatureDictH{$ContextFea}{ID}) {
			    $$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
			    $$FeatureDictH{$ContextFea}{DF}++;
			}
		    }
		}
		$PC++;
		$Pline = $line - $PC;
	    }else {
		last;
	    }
	}

	my $NC = 1;
	my $Nline = $line + $NC;
	while (($NC < 5) && (exists $$HeaderH{$Nline})) { #next line
	    if ($$HeaderH{$Nline}{TClass} eq "s") {
		my $ContextFea = "N"."$NC"."$$HeaderH{$Nline}{SClsName}";
		if (! $$FeatureDictH{$ContextFea}{ID}) {
		    $$FeatureDictH{FeatureCounter}++;
		    $$FeatureDictH{$ContextFea}{ID} = $$FeatureDictH{FeatureCounter};
		    $$FeatureDictH{$ContextFea}{max} = 0.5;
		}
		if ($$FeatureDictH{$ContextFea}{ID}) {
		    $$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
		    $$FeatureDictH{$ContextFea}{DF}++;
		}
	    }else { # consider the order of the tag
		foreach my $tag(sort {$$HeaderH{$Nline}{MClsName}{$a} <=> $$HeaderH{$Nline}{MClsName}{$b}} keys %{$$HeaderH{$Nline}{MClsName}}){
		    my $ContextFea = "N"."$NC"."$tag";
		    if (! $$FeatureDictH{$ContextFea}{ID}) {
			$$FeatureDictH{FeatureCounter}++;
			$$FeatureDictH{$ContextFea}{ID} = $$FeatureDictH{FeatureCounter};
			$$FeatureDictH{$ContextFea}{max} = 0.5;
		    }
		    if ($$FeatureDictH{$ContextFea}{ID}) {
			$$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
			$$FeatureDictH{$ContextFea}{DF}++;
		    }
		}
	    }
	    $NC++;
	    $Nline = $line + $NC;
	}

	#assemble features and their weight into string without normalization
	my $tmpFeaVec = $$HeaderH{$line}{SVMFeaVec};
	foreach my $fea(sort{$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$HeaderH{$line}{ContextFeaVec}}) {
	    if (exists $$FeatureDictH{$fea}{ID}) {
		$tmpFeaVec .= "$$FeatureDictH{$fea}{ID}\:$$HeaderH{$line}{ContextFeaVec}{$fea} ";
	    }
	}
	$$HeaderH{$line}{ContextSVMFeaVec} = "$tmpFeaVec";
    }
    return($FeatureDictH, $HeaderH);
}

sub TestAssignLineTag() {
    my $FeatureDictH = shift;
    my $HeaderH = shift;
    my %curState = ();

    foreach $line(sort {$a <=> $b} keys %{$HeaderH}) {
	#Initialize-remove the $$HeaderH{$line}{ContextFeaVec}
	if(exists ($$HeaderH{$line}{ContextFeaVec})) {
	    delete($$HeaderH{$line}{ContextFeaVec});
	}

	my $PC = 1; # 0 means the tag for current line (which might be useful)
	my $Pline = $line - $PC;
	while (($PC < 5) && ($Pline > 0)) { #previous line
	    if (exists $$HeaderH{$Pline}{Pretag}) {
		foreach my $tag(sort keys %{$$HeaderH{$Pline}{Pretag}}){
		    my $ContextFea = "P"."$PC"."$tag";
		    if ($$FeatureDictH{$ContextFea}{ID}) {
			$$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
		    }
		}
	    }
	    $PC++;
	    $Pline = $line - $PC;
	}

	my $NC = 1;
	my $Nline = $line + $NC;
	while (($NC < 5) && (exists $$HeaderH{$Nline})) { #next line
	    foreach my $tag(sort keys %{$$HeaderH{$Nline}{Pretag}}){
		my $ContextFea = "N"."$NC"."$tag";
		if ($$FeatureDictH{$ContextFea}{ID}) {
		    $$HeaderH{$line}{ContextFeaVec}{$ContextFea} = 0.5;
		}
	    }
	    $NC++;
	    $Nline = $line + $NC;
	}

	#assemble features and their weight into string without normalization
	my $tmpFeaVec = $$HeaderH{$line}{SVMFeaVec};

	foreach my $fea(sort{$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$HeaderH{$line}{ContextFeaVec}}) {
	    if (exists $$FeatureDictH{$fea}{ID}) {
		$tmpFeaVec .= "$$FeatureDictH{$fea}{ID}\:$$HeaderH{$line}{ContextFeaVec}{$fea} ";
	    }
	}
	$tmpFeaVec =~ s/\s+$//g;
	$$HeaderH{$line}{ContextSVMFeaVec} = "$tmpFeaVec";
    }
    return($FeatureDictH, $HeaderH);
}

#given a line, check the number and the position of punctuation/space it contains
sub GetSeparatorIndex() {
    my $line = shift;
    my %SeparatorH = ();

    my $PuncNum = 0;
    $line =~ s/^\s+//g;
    $line =~ s/\s+$//g;

    #punc means this line contains punc or only space
    #each space occupies a position and punctuations are separate
    my ($punc, $spaceLine) = &FillSpace($line);

    #punctuation is specific; space separator contains punctuation separators.
    my @component = split(/\s+/, $spaceLine);
    foreach my $i(0 .. $#component) {
	if ($component[$i] =~ /<<sep>>(\W+|\s*)<<\/sep>>/) {
	    $component[$i] = $1;
	    if ($component[$i] eq "") {
		$component[$i] = "<<sep>><<\/sep>>";
		$SeparatorH{space}{$i} = 2;
	    }else {
		$SeparatorH{punc}{$i} = 2;
		$PuncNum++;
		$SeparatorH{space}{$i} = 2;
	    }
	}elsif ($component[$i] =~ /<space>/) {
	    $SeparatorH{space}{$i} = 1;
	}elsif ($component[$i] =~ /^[^\p{IsLower}\p{IsUpper}\s+\-\d+]+$/) {
	    $SeparatorH{punc}{$i} = 1; #position(not what punc)
	    $PuncNum++;
	    $SeparatorH{space}{$i} = 1;
	}
    }
    return($PuncNum, \%SeparatorH, \@component);
}


#multi-Authors line still has only one class, although 1+ authors
sub AssignWordTagFromChunk() {
    my ($LineH, $SepH, $component) = @_;
    my @tags = ();
    foreach my $tag(sort {$$LineH{MClsName}{$a} <=> $$LineH{MClsName}{$b}} keys %{$$LineH{MClsName}}) {
	push @tags, $tag;
    }

    my $ChunkNO = 1;
    my $curTag = $tags[$tagP];
    my $WordPos = 1;
    my $chunk = "";
    for my $i(0 .. $#$component) {
	#we do not assign class to separators
	if ($$SepH{space}{$i} >1) {
	    if ($chunk ne "") {
		$$LineH{Tchunk}{$ChunkNO}{cls} = $curTag;
		$$LineH{Tchunk}{$ChunkNO}{content} = $chunk;
		$chunk = "";
		$curTag = $tags[$ChunkNO];
		$ChunkNO++;
	    }
	}elsif ($$component[$i] !~ /<space>|^\W+$|\<\<.*\>\>/) { # such as <<sep>> <</sep>>
	    $chunk .= "$$component[$i] ";
	    $$LineH{Tline}{$WordPos}{cls} = $curTag;
	    $$LineH{Tline}{$WordPos}{OriginalWord} = $$component[$i];
	    $WordPos++;
	}
    };

    #Fill in the last chunk
    $$LineH{Tchunk}{$ChunkNO}{cls} = $curTag;
    $$LineH{Tchunk}{$ChunkNO}{content} = $chunk;

    return ($LineH);
}


sub AssignWordTag4SingleClassLine() {
    my ($type, $curTag, $LineH, $component) = @_;

    my $WordPos = 1;
    for my $i(0 .. $#$component) {
	if ($$component[$i] !~ /<space>|^\W+$|\<\<.*\>\>/) { # such as <<sep>> <</sep>>
	    if ($type eq "truth") {
		$$LineH{Tline}{$WordPos}{cls} = $curTag;
		#added 01/08 the original word in a position
		$$LineH{Tline}{$WordPos}{OriginalWord} = $$component[$i];
	    }elsif ($type eq "predict") {
		$$LineH{Pline}{$WordPos}{cls} = $curTag;
		$$LineH{Pline}{$WordPos}{OriginalWord} = $$component[$i];
	    }
	    $WordPos++;
	}
    }

    return ($LineH);
}


sub Analyze() {
    my $resultF = shift;
    open(resultFH, "$resultF") || die "SVMHeaderParse: could not open $resultF to read: $!";
    my $result = <resultFH>;
    close(resultFH);
    $result =~ s/\s+$//g;
    return($result);
}


sub ReadFeatureDict() {
    my $Fname = shift;
    my %FeatureDictH;

    open (FH, "$Fname") || die "SVMHeaderParse: could not open $Fname to read: $!";
    while (my $line = <FH>) {
	my ($ID, $fea, $max, $DF) = split(/<>/, $line);
	$ID =~ s/^\s+//g;
	$ID =~ s/\s+$//g;

	if ($fea =~ /FeatureCounter/) {
	    $FeatureDictH{$fea}{num} = $ID;
	    next;
	}

	$fea =~ s/^\s+//g;
	$fea =~ s/\s+$//g;
	$max =~ s/^\s+//g;
	$max =~ s/\s+$//g;
	$DF =~ s/^\s+//g;
	$Df =~ s/\s+$//g;
	$FeatureDictH{$fea}{ID} = $ID;
	$FeatureDictH{$fea}{max} = $max;
	$FeatureDictH{$fea}{DF} = $DF;
    }
    close(FH);
    return(\%FeatureDictH);
}


sub printTrainData() {
    my $affix = shift;
    my $HeaderH = shift;

    #Sometimes $$HeaderH{$HeaNO}{$LN}{ContextSVMFeaVec} is not null, but
    #$$HeaderH{$HeaNO}{$LN}{SVMFeaVec} is null so they have different file length!
    for my $clsNO(1 .. 15) {
	my $F = "$offlineD"."$clsNO"."\."."$affix";
	open(FH, ">$F") || die "SVMHeaderParse: could not open $F to write: $!";
	foreach my $HeaNO (sort {$a <=> $b} keys %{$HeaderH}) {
	    foreach my $LN(sort {$a <=> $b} keys %{$$HeaderH{$HeaNO}}) {
		if ($$HeaderH{$HeaNO}{$LN}{SVMFeaVec} ne "") {
		    if ($affix eq "train") {
			if ($$HeaderH{$HeaNO}{$LN}{SVMFeaVec} ne "") {
			    if (($$HeaderH{$HeaNO}{$LN}{SClsName} eq "$clsNO") || exists($$HeaderH{$HeaNO}{$LN}{MClsName}{$clsNO})) {
				print FH "1 $$HeaderH{$HeaNO}{$LN}{SVMFeaVec}\n";
			    }else {
				print FH "-1 $$HeaderH{$HeaNO}{$LN}{SVMFeaVec}\n";
			    }
			}
		    }elsif ($affix eq "context") {
			#if ($$HeaderH{$HeaNO}{$LN}{ContextSVMFeaVec} ne "") {
			if (($$HeaderH{$HeaNO}{$LN}{SClsName} eq "$clsNO") || exists($$HeaderH{$HeaNO}{$LN}{MClsName}{$clsNO})) {
			    print FH "1 $$HeaderH{$HeaNO}{$LN}{ContextSVMFeaVec}\n";
			}else {
			    print FH "-1 $$HeaderH{$HeaNO}{$LN}{ContextSVMFeaVec}\n";
			}
		    }else {
			print "weired -- $affix is not context nor train \n";
		    }
		}
	    }
	}
	close(FH);
    }
}


sub printNameSpaceTrainData(){
    my $printF = shift;
    my $NameSpaceTrainVecH = shift;

    open(FH, ">$printF") || die "SVMHeaderParse: could not open $printF to write: $!";
    foreach my $Lcount(sort{$a<=>$b} keys %{$NameSpaceTrainVecH}) {
	print FH "$$NameSpaceTrainVecH{$Lcount}{SpaceNameVec}\n";
    }
    close(FH);
}


sub SpaceNameLnFeaRepre() {
    my $type = shift;
    my $NamePatternStr = shift;
    my $NameDictH = shift;

    #feature generation and representation
    #It is good to make each of the apple's feature(color, shape..) separate.
    my %FeatureH = ();
    $NamePatternStr =~ s/\<\>$//g; #remove the last <>
    my @Names = split(/<>/, $NamePatternStr);

    #try making features binary
    for my $i(0 .. $#Names) {
	my @NameComponent = split(/\s+/, $Names[$i]);
	for my $j(0 .. $#NameComponent){

	    #feature generation($i = 0 is the first one)
	    $FeatureH{"Name"."$i"."part"."$j"."form"} = &HeaderParse::API::NamePatternMatch::RichNameType($NameComponent[$j]);
	    if ($j eq $#NameComponent) {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = "Last";
	    }elsif ($j eq $#NameComponent -1) {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = "SecLast";
	    }else {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = $j;
	    }

	    #firstname, lastname information
#	    print "hello: ".lc($NameComponent[$j])."\n";
	    if (($firstnameH{lc($NameComponent[$j])}) && (!$lastnameH{lc($NameComponent[$j])})) {
		$FeatureH{"Name"."$i"."part"."$j"."FN"} = 1;
	    }elsif (($lastnameH{lc($NameComponent[$j])}) && (!$firstnameH{lc($NameComponent[$j])})) {
		$FeatureH{"Name"."$i"."part"."$j"."LN"} = 1;
	    }elsif (! $dictH{lc($NameComponent[$j])}) {
		$FeatureH{"Name"."$i"."part"."$j"."NonDict"} = 1;
	    }

	    #space for more features
	}
    }

    #Build up FeatureVec
    #code for the attribute ID separately so that the ID for features would be continuous
    if ($type eq "train") {
	foreach my $fea(sort {$a <=> $b} keys %FeatureH) {
	    if (! $$NameDictH{$fea}{ID}) {
		$$NameDictH{FeatureCounter}++;
		$$NameDictH{$fea}{ID} = $$NameDictH{FeatureCounter};
	    }

	    if (! IsNumber($FeatureH{$fea})) {
		if (! exists  $$NameDictH{$FeatureH{$fea}}{ID}) {
		    $$NameDictH{FeatureCounter}++;
		    $$NameDictH{$FeatureH{$fea}}{ID} = $$NameDictH{FeatureCounter};
		}
		$FeatureH{$fea} = $$NameDictH{$FeatureH{$fea}}{ID};
	    }

	    if ($FeatureH{$fea} == 0) {
		delete($FeatureH{$fea});
	    }else {
		if ((! exists $$NameDictH{$fea}{max}) || ($$NameDictH{$fea}{max} < $FeatureH{$fea})) {
		    $$NameDictH{$fea}{max} =  $FeatureH{$fea};
		}
	    }
	}
	return(\%FeatureH, $NameDictH);
    #test
    }else {
	my $SpaceNameFeaVec = "";
	my $SpaceNameTextFeaVec = "";
	foreach my $fea(sort {$$NameDictH{$a}{ID} <=> $$NameDictH{$b}{ID}} keys %FeatureH) {
	    if (! &IsNumber($FeatureH{$fea})) {
		if (exists  $$NameDictH{$FeatureH{$fea}}{ID}) {
		    $FeatureH{$fea} = $$NameDictH{$FeatureH{$fea}}{ID};
		}else {
		    delete($FeatureH{$fea});
		}
	    }

	    if (! ($FeatureH{$fea} && $$NameDictH{$fea}{ID})) {
		delete($FeatureH{$fea});
	    }else {
		$FeatureH{$fea} = sprintf("%.8f", $FeatureH{$fea}/$$NameDictH{$fea}{max});
		$SpaceNameFeaVec .= "$$NameDictH{$fea}{ID}\:$FeatureH{$fea} ";
		$SpaceNameTextFeaVec .= "$fea\:$FeatureH{$fea} ";
	    }
	}
	return($SpaceNameFeaVec, $SpaceNameTextFeaVec);
    }
}


sub SpaceNameLnFeaRepre_unit() {
    my $NamePatternStr = shift;

    #feature generation and representation
    #It is good to make each of the apple's feature(color, shape..) separate.
    my %FeatureH = ();
    $NamePatternStr =~ s/\<\>$//g; #remove the last <>
    my @Names = split(/<>/, $NamePatternStr);

    #try making features binary
    for my $i(0 .. $#Names) {
	my @NameComponent = split(/\s+/, $Names[$i]);
	for my $j(0 .. $#NameComponent){
	    #feature generation($i = 0 is the first one)
	    $FeatureH{"Name"."$i"."part"."$j"."form"} = &HeaderParse::API::NamePatternMatch::RichNameType($NameComponent[$j]);
	    if ($j eq $#NameComponent) {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = "Last";
	    }elsif ($j eq $#NameComponent -1) {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = "SecLast";
	    }else {
		$FeatureH{"Name"."$i"."part"."$j"."pos"} = $j;
	    }
	    #firstname, lastname information
#	    print "hello2: ".lc($NameComponent[$j])."\n";
	    if (($firstnameH{lc($NameComponent[$j])}) && (!$lastnameH{lc($NameComponent[$j])})) {
#		print "NAME MATCH: ".lc($NameComponent[$j])."\n";
		$FeatureH{"Name"."$i"."part"."$j"."FN"} = 1;
	    }elsif (($lastnameH{lc($NameComponent[$j])}) && (!$firstnameH{lc($NameComponent[$j])})) {
#		print "NAME MATCH: ".lc($NameComponent[$j])."\n";

		$FeatureH{"Name"."$i"."part"."$j"."LN"} = 1;
	    }elsif (! $dictH{lc($NameComponent[$j])}) {
#		print "NAME MATCH: ".lc($NameComponent[$j])."\n";

		$FeatureH{"Name"."$i"."part"."$j"."NonDict"} = 1;
	    }

	    #space for more features
	}
    }
    return(\%FeatureH);
}


sub IsNumber ()
{
    my $in = shift;
    if ($in =~ m/^(\d+)(\.\d+)*$/) {
	return 1;
    }else {
	return 0;
    }
}


sub FormFeaDict() {
    my $DataH = shift;
    my $FeatureDictH = shift;
    my %NameSpaceFeaDictH = ();

    foreach my $HeaNO (sort {$a <=> $b} keys %{$DataH}) {
	foreach my $line (sort {$a <=> $b} keys %{$$DataH{$HeaNO}}) {
	    foreach my $fea(keys %{$$DataH{$HeaNO}{$line}{FeaVec}}) {
		if ($$DataH{$HeaNO}{$line}{FeaVec}{$fea} == 0) {
		    delete ($$DataH{$HeaNO}{$line}{FeaVec}{$fea});
		    next;
		}else {
		    if (! $$FeatureDictH{$fea}{ID}) {
			$$FeatureDictH{FeatureCounter}++;
			$$FeatureDictH{$fea}{ID} = $$FeatureDictH{FeatureCounter};
		    }
		    if ($$DataH{$HeaNO}{$line}{FeaVec}{$fea} > $$FeatureDictH{$fea}{max}) {
			$$FeatureDictH{$fea}{max} = $$DataH{$HeaNO}{$line}{FeaVec}{$fea};
		    }
		    $$FeatureDictH{$fea}{DF}++;
		}
		#test needs this line!
		if ((! $$FeatureDictH{$fea}{ID}) || ($$DataH{$HeaNO}{$line}{FeaVec}{$fea} == 0)) { #some basic feature defined in initialization such as pubnumber could be 0
		    delete ($$DataH{$HeaNO}{$line}{FeaVec}{$fea});
		}
	    }

	    #form the Name Space Feature Dict
	    if (exists $$DataH{$HeaNO}{$line}{NamePattern}) {
		foreach my $CandidateNamePattern(keys %{$$DataH{$HeaNO}{$line}{NamePattern}}) {
		    foreach my $fea(keys %{$$DataH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}}) {
			my $wt = $$DataH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea};
			if (! $NameSpaceFeaDictH{$fea}{ID}) {
			    $NameSpaceFeaDictH{FeatureCounter}++;
			    $NameSpaceFeaDictH{$fea}{ID} = $NameSpaceFeaDictH{FeatureCounter};
			}
			if (! &IsNumber($wt)) {
			    if (! exists  $NameSpaceFeaDictH{$wt}{ID}) {
				$NameSpaceFeaDictH{FeatureCounter}++;
				$NameSpaceFeaDictH{$wt}{ID} = $NameSpaceFeaDictH{FeatureCounter};
			    }
			    $$DataH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea} = $NameSpaceFeaDictH{$wt}{ID};
			}

			if ($wt  == 0) {
			    delete($$DataH{$HeaNO}{$line}{NamePattern}{$CandidateNamePattern}{SpaceNameVec}{$fea});
			}else {
			    if ((! exists $NameSpaceFeaDictH{$fea}{max}) || ($NameSpaceFeaDictH{$fea}{max} < $wt)) {
				$NameSpaceFeaDictH{$fea}{max} =  $wt;
			    }
			}
		    }
		}
	    }
	    #end of form the dictionary for the name
	}
    }
    return($DataH, $FeatureDictH, \%NameSpaceFeaDictH);
}


sub FormTestFeaVec(){
    my $FeatureDictH = shift;
    my $TestHeaderH = shift;

    foreach my $line(sort{$a<=>$b} keys %{$TestHeaderH}) {
	foreach my $fea(keys %{$$TestHeaderH{$line}{FeaVec}}) {
	    if ((! $$FeatureDictH{$fea}{ID}) || ($$TestHeaderH{$line}{FeaVec}{$fea} == 0)) {
		delete($$TestHeaderH{$line}{FeaVec}{$fea});
	    }
	}
    }
    return($TestHeaderH);
}


sub PruneDict() {
    my $FeatureDictH = shift;
    my $Recount = 1;

    foreach my $DictFea(sort{$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$FeatureDictH}) {
	if ((($DictFea ne "FeatureCounter") && ($$FeatureDictH{$DictFea}{max} == 0)) || ($$FeatureDictH{$DictFea}{DF} < 2))  {
	    delete($$FeatureDictH{$DictFea});
	}else {
	    $$FeatureDictH{$DictFea}{ID} = $Recount;
	    $Recount++;
	}
    }

    $$FeatureDictH{FeatureCounter} = $Recount-1;

    return($FeatureDictH);
}

#input is an array of name patterns
#return a string of the best name pattern
sub PredictBestNamePattern() {
    my $PredictedNames = shift;
    my $SVMNameSpaceModel = shift;
    my $SpaceNameDictH = shift;
    my $tmpCacheVec = shift;
    my $SVMTmpResult = shift;

    my $MaxVal = -10;
    my $BestNamePattern = "";

    for my $i(0 .. $#$PredictedNames) {
	my $candidateName = "";
	for my $j(0 .. $#{$$PredictedNames[$i]}) {
	    if ($$PredictedNames[$i][$j]) {
		$candidateName .= "$$PredictedNames[$i][$j]<>";
	    }
	}

	my ($RawNameFeaVec) = &SpaceNameLnFeaRepre_unit($candidateName);
	#filter out the non-dictinary features
	my $SpaceNameVec = "";
	my $SpaceNameTextFeaVec = "";
	foreach my $fea(sort {$$SpaceNameDictH{$a}{ID} <=> $$SpaceNameDictH{$b}{ID}} keys %{$RawNameFeaVec}) {
	    my $wt = $$RawNameFeaVec{$fea};
	    if (! &IsNumber($wt)) {
		if (exists  $$SpaceNameDictH{$wt}{ID}) {
		    $$RawNameFeaVec{$fea} = $$SpaceNameDictH{$wt}{ID};
		}else {
		    delete($$RawNameFeaVec{$fea});
		}
	    }

	    if (! (($$RawNameFeaVec{$fea}>0) && $$SpaceNameDictH{$fea}{ID})) {
		delete($$RawNameFeaVec{$fea});
	    }else {
		$$RawNameFeaVec{$fea} = sprintf("%.8f", $$RawNameFeaVec{$fea}/$$SpaceNameDictH{$fea}{max}
						);
		$SpaceNameVec .= "$$SpaceNameDictH{$fea}{ID}\:$$RawNameFeaVec{$fea} ";
		$SpaceNameTextFeaVec .= "$fea\:$$RawNameFeaVec{$fea} ";
	    }
	}
	open(testVec, ">$tmpCacheVec") || die "SVMHeaderParse: could not open $tmpCacheVec to write: $!";
	# print "NamePattern FeatureVec is\: $SpaceNameTextVec\n";
	print testVec "$SpaceNameVec";
	close(testVec);
	`$Classifier -v 0 $tmpCacheVec $SVMNameSpaceModel $SVMTmpResult`;
	my $result = &Analyze($SVMTmpResult);
	if ($result > $MaxVal) {
	    $MaxVal = $result;
	    $BestNamePattern = $candidateName;
	}
    }

    unlink $tmpCacheVec;
    unlink $SVMTmpResult;

    #split the multiple names in order
    $BestNamePattern =~ s/\<\>$//g; #remove the last <>

    return($BestNamePattern);
}


sub WordCount() { #didn't try, but should be OK, since it is borrowed from AddrMatch in function.pm
    my $inStr = shift;
    $inStr =~ s/^\s+//g;
    $inStr =~ s/\s+$//g;

    my $senLen = 0;
    my @words = split(/\s+/, $inStr);
    for my $i(0 .. $#words) {
	if ($words[0] !~ /^\W+\s*$/) { #punctuation
	    $senLen ++;
	}
    }
    return($senLen);
}

1;
