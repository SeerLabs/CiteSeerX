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
package HeaderParse::API::MultiClassChunking;

use utf8;
require Exporter;
@ISA = qw(Exporter); 
@EXPORT =  qw(&LocateEmailFromComponent &LocateURLFromComponent &LocateUnIdentifiedChunk &FillChunkH &Cont2ClassChunking &Disc2ClassChunking_2chunk);

use HeaderParse::API::LoadInformation;
use vars qw($debug %emailH %urlH);

use HeaderParse::API::Function qw(&AddrMatch &printDict &GenTrainVecMatrix &LineFeatureRepre2 &SeparatePunc);
use HeaderParse::Config::API_Config qw($offlineD $SVM_Dir $Tmp_Dir);
#return chunk array, but it could contain only text, not real @; needs further processing

#my $offlineD = "../../offline/";
#my $tmpCacheVec = "$offlineD"."tmpVec";
#my $SVMTmpResult = "$offlineD"."tmpresult";

#my $tmpCacheVec = "$Tmp_Dir/tmpVec";
#my $SVMTmpResult = "$Tmp_Dir/tmpresult";
my $Classifier = "$SVM_Dir"."svm_classify";

sub LocateEmailFromComponent() {
  my $component = shift;
  my %FindH = ();
  
  my @FilteredComponent = ();
  for my $i(0 .. $#$component) {
    if (($$component[$i] =~ /\@/) || (exists $emailH{lc($$component[$i])})) {
	$FindH{$i}{cls} = 6;
	$FindH{$i}{startPos} = $i;
	$FindH{$i}{endPos} = $i;
	$FindH{$i}{content} = $$component[$i];
	$$component[$i] = "-1";
    }
  }
  return(\%FindH, $component);
}

sub LocateURLFromComponent() {
  my $component = shift;
  my %FindH = ();

  for my $i(0 .. $#$component) {
    if (($$component[$i] =~ /(http)|(ftp)\:\/\/(\w+\.){1,}/) || (exists $urlH{lc($$component[$i])})) {
	$FindH{$i}{cls} = 12;
	$FindH{$i}{startPos} = $i;
	$FindH{$i}{endPos} = $i;
	$FindH{$i}{content} = $$component[$i];
	$$component[$i] = "-1";
    }
  }
  return(\%FindH, $component);
}

#Locate the current unidentified chunk 
sub LocateUnIdentifiedChunk() {
    my $component = shift;
    my %UnIdentifiedChunk = ();

    #Chunk[start position -- for ordering  since chunks are non-overlapped,
    #      end postion] -- we could tag each word knowing this scope.
    #however this position is for space filled array!!!!!!

    my $chunk = "";
    my $startPos = 0;
    for my $i(0 .. $#$component) {
	if ($$component[$i] eq "-1") { #end of a chunk
	    if ($chunk ne "") {
		$UnIdentifiedChunk{counter}++;
		$UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{startPos} = $startPos;
		$UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{endPos} = $i - 1;
		$UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{content} = $chunk;
		$chunk = "";
	    }
	    $startPos = $i + 1;
	}elsif ($$component[$i] ne "<space>") {
#	}elsif ($$component[$i] !~ /<space>|^\W+$/) {
	    $chunk .= "$$component[$i] ";
	}

	if (($i == $#$component) && ($$component[$i] ne "-1")) {#last one
            $UnIdentifiedChunk{counter}++;
            $UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{startPos} = $startPos;
            $UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{endPos} = $#$component;
            $UnIdentifiedChunk{$UnIdentifiedChunk{counter}}{content} = $chunk;
        }
    }
   
    return (\%UnIdentifiedChunk);
}

#Assume Chunk class are non-overlapped; (4 include 3 is our own assumption in practical application.
#Questioning on the label and (3 4); but for consistent comparison, we make
#3 and 4 still separate.

#populate the Line Hash with the chunk information(which has all needed info)
#and word-class information
sub FillChunkH() {
    my ($LineHash, $component, $ChunkHashArray) = @_;

    #build an index for all the hash chunk by the start pos
    my %Index = ();
    for my $ChunkH (@$ChunkHashArray) {
	foreach my $chunk(keys %{$ChunkH}) {#make sure if correct
	    if ($chunk ne "counter") {
		$Index{$$ChunkH{$chunk}{startPos}} = $$ChunkH{$chunk};
	    }
	}
    }
    
    my $WordPos = 1; #there might be difference in position between here and teh True Value, esp. separating words like "and" 
    foreach my $ind (sort{$a<=>$b} keys %Index) {
	$$LineHash{Pchunk}{ChunkCounter}++;
	my $ChunkPos = $$LineHash{Pchunk}{ChunkCounter};
	$$LineHash{Pchunk}{$ChunkPos}{cls} = $Index{$ind}{cls};
	$$LineHash{Pchunk}{$ChunkPos}{content} = $Index{$ind}{content};

	#start filling word-class dist.
	for my $i($Index{$ind}{startPos} .. $Index{$ind}{endPos}) {
	    if ($$component[$i] !~ /<space>|^\W+$/) {
	   #  if ($$component[$i] !~ /<space>/) {
		$$LineHash{Pline}{$WordPos}{cls} = $Index{$ind}{cls};
		$$LineHash{Pline}{$WordPos}{OriginalWord} = $$component[$i];
		$WordPos++;
	    }
	}

    }

    return($LineHash);
}

#same data structure as that of LocateUnIdentifiedChunk
#idea: each chunk before/after the separator 
#      -- score PA, Score PB and score NA, score NB. 
#choose the one with MAX((PA-PB)*(NB-NA)) 
#performance: 271(0.742) out of 365 are classified correct 
#if (P1-P2) > 0 then first chunk is assigned the tag corresponding to the first classifier.

#make sure other files (tmpVec and FeaVec) are valid.
#input: a tag array with 2 tags, $type, $SepH and $component, $FeatureDictH
#Output: A chunking hash  

#needs offset to map to the original $component and fill in the chunk info.
#needs to adjust SepH as well.

#this is original chunking. make mapping a outside layer.
sub Cont2ClassChunking() {
  my $fold = shift;
  my $tags = shift; # an array
  my $type = shift; #punc or space+punc
  my $SepH = shift;
  my $component = shift;
  my $FeatureDictH = shift;
  my $tmpCacheVec = shift;
  my $SVMTmpResult = shift;

  my $SVMModelF1 = "$offlineD"."$$tags[0]"."Model"."fold"."$fold";
  my $SVMModelF2 = "$offlineD"."$$tags[1]"."Model"."fold"."$fold";


  my $trueInd = -1; # for evaluation
  my $maxVal = -100;
  my $maxInd = -1;
  my %MemoryH = ();
  my %ChunkH = (); # hold return value
  
  foreach my $index(sort{$a <=> $b} keys %{$$SepH{$type}}) {
    my @part1 = @$component;
    my @part2 = splice(@part1, $index);
    my $part1 = join(" ", @part1);
    $part1 =~ s/<space>//g;
    $part1 =~ s/^\s+//g;
    $part1 =~ s/\s+$//g;
    if ($part1 eq "") {
      next;
    }
    $MemoryH{$index}{part1} = "$part1";

    my $part2 = join(" ", @part2);
    $part2 =~ s/<space>//g;
    $part2 =~ s/^\s+//g;
    $part2 =~ s/\s+$//g;
    if ($part2 eq "") {
      next;
    }
    $MemoryH{$index}{part2} = "$part2";

    my $true = "-1"; # for svm feature vector tag
    if ($$SepH{$type}{$index} > 1) {
      $true = 1;
      $trueInd = $index;
      $MemoryH{trueInd} = $index;
    }
	 
    $MemoryH{$index}{part1FeaVec} = &LineFeatureRepre2($true, $part1, $FeatureDictH, "$tmpCacheVec");
    if ($debug) {
	print STDERR "Classifier $Classifier \n";
	print STDERR "tmpCacheVec $tmpCacheVec \n";
	print STDERR "SVMModelF1 $SVMModelF1 \n";
	print STDERR "SVMTmpResult $SVMTmpResult \n";
    }
    `$Classifier $tmpCacheVec $SVMModelF1 $SVMTmpResult`; #output message

    $MemoryH{$index}{P1} = &Analyze($SVMTmpResult);
    if ($debug) {
	print STDERR "result $MemoryH{$index}{P1}\n";
    }
    `$Classifier $tmpCacheVec $SVMModelF2 $SVMTmpResult`; #silent
    $MemoryH{$index}{P2} = &Analyze($SVMTmpResult);
    if ($debug) {
	print STDERR "result $MemoryH{$index}{P2}\n";
    }
    $MemoryH{$index}{P12} = $MemoryH{$index}{P1}  -  $MemoryH{$index}{P2};
    $MemoryH{$index}{part2FeaVec} = &LineFeatureRepre2($true, $part2, $FeatureDictH, "$tmpCacheVec");

    `$Classifier $tmpCacheVec $SVMModelF1 $SVMTmpResult`;
    $MemoryH{$index}{N1} = &Analyze($SVMTmpResult);
    if ($debug) {
	print STDERR "result $MemoryH{$index}{N1}\n";
    }
    `$Classifier $tmpCacheVec $SVMModelF2 $SVMTmpResult`;
    $MemoryH{$index}{N2} = &Analyze($SVMTmpResult);
    if ($debug) {
	print STDERR "result $MemoryH{$index}{N2}\n";
    }
    $MemoryH{$index}{N21} = $MemoryH{$index}{N2} -  $MemoryH{$index}{N1};
    $MemoryH{$index}{PN1} = $MemoryH{$index}{P1} - $MemoryH{$index}{N1}; #classifier 1
    $MemoryH{$index}{PN2} = $MemoryH{$index}{P2} - $MemoryH{$index}{N2};#classifier 2

    $MemoryH{$index}{eval} = $MemoryH{$index}{P12} * $MemoryH{$index}{N21};
    if ($MemoryH{$index}{eval} > $maxVal) {
	$maxVal = $MemoryH{$index}{eval};
	$maxInd = $index;
    }
}
  
  $MemoryH{maxVal} = $maxVal;
  $MemoryH{maxInd} = $maxInd; #real position 
  
  my ($tag1, $tag2);
#idea is (1) if (P1-N1)(P2-N2) < 0; easy
#        (2) if (P1-N1)(P2-N2) > 0, let the maximal abosulte value decide

    #get the class label for each chunk
    if ($MemoryH{maxInd}{PN1}*$MemoryH{maxInd}{PN2} < 0) {#easy; different sign
	if ($MemoryH{maxInd}{PN1} > 0) {
	    $tag1 = $$tags[0];
	    $tag2 = $$tags[1]; 
	}else {
	    $tag1 = $$tags[1];
	    $tag2 = $$tags[0]; 
	}
    }else {
	if (abs($MemoryH{maxInd}{PN1}) > abs($MemoryH{maxInd}{PN2})) {
	    if ($MemoryH{maxInd}{P1} > $MemoryH{maxInd}{N1}) {
		$tag1 = $$tags[0];
		$tag2 = $$tags[1];
	    }else {
		 $tag1 = $$tags[1];
		 $tag2 = $$tags[0]; 
	    }
	}else {
	    if ($MemoryH{maxInd}{P2} > $MemoryH{maxInd}{N2}) {
		$tag1 = $$tags[1];
		$tag2 = $$tags[0]; 
	    }else {
		$tag1 = $$tags[0];
		$tag2 = $$tags[1];
	    }
	}
    }

  #get chunk according to the maxInd
  my $chunk1 = "";
  for my $i(0 .. $maxInd-1) {
      if ($$component[$i] ne "<space>") {
	  $chunk1 .= "$$component[$i] ";
      }
  }

  $ChunkH{counter}++;
  $ChunkH{$ChunkH{counter}}{cls} = $tag1;
  $ChunkH{$ChunkH{counter}}{startPos} = 0;
  $ChunkH{$ChunkH{counter}}{endPos} = $maxInd-1;
  $ChunkH{$ChunkH{counter}}{content} = $chunk1;

  # we assume that $maxInd is the separator so that we exclude it from any class
  my $chunk2 = "";
  for my $i($maxInd+1 .. $#$component) {
      if ($$component[$i] ne "<space>") {
	  $chunk2 .= "$$component[$i] ";
      }
  }

  $ChunkH{counter}++;
  $ChunkH{$ChunkH{counter}}{cls} = $tag2;
  $ChunkH{$ChunkH{counter}}{startPos} = $maxInd+1; #exclude separator
  $ChunkH{$ChunkH{counter}}{endPos} = $#$component;
  $ChunkH{$ChunkH{counter}}{content} = $chunk2;

  unlink $tmpCacheVec;
  unlink $SVMTmpResult;

  return(\%ChunkH);

}

#if the 2 classes are discrete --
#job is to assign the tag to 2 chunks 
#input is a hash of 2 chunks

sub Disc2ClassChunking_2chunk() {
    my $fold = shift;
    my $tags = shift;
    my $ChunkH = shift;
    my $FeatureDictH = shift;
    my $tmpCacheVec = shift;
    my $SVMTmpResult = shift;
    
    my $SVMModelF1 = "$offlineD"."$$tags[0]"."Model"."fold"."$fold";
    my $SVMModelF2 = "$offlineD"."$$tags[1]"."Model"."fold"."$fold";


    $part1FeaVec = &LineFeatureRepre2($true, $$ChunkH{1}{content}, $FeatureDictH, "$tmpCacheVec");
    if ($debug) {
	if ($part1FeaVec eq "") {
	    print STDERR "$part1FeaVec is null vecor \n";
	}
    }
    `$Classifier $tmpCacheVec $SVMModelF1 $SVMTmpResult`; #output message
    $P1 = &Analyze($SVMTmpResult);
    `$Classifier $tmpCacheVec $SVMModelF2 $SVMTmpResult`; #silent
    $P2 = &Analyze($SVMTmpResult);
    $P12 = $P1  -  $P2;
    
    $part2FeaVec = &LineFeatureRepre2($true, $$ChunkH{2}{content}, $FeatureDictH, "$tmpCacheVec");
    if ($debug) {
	if ($part2FeaVec eq "") {
	    print STDERR "$part2FeaVec is null vecor \n";
	}
    }
    `$Classifier $tmpCacheVec $SVMModelF1 $SVMTmpResult`;
    $N1 = &Analyze($SVMTmpResult);
    `$Classifier $tmpCacheVec $SVMModelF2 $SVMTmpResult`;
    $N2 = &Analyze($SVMTmpResult);
    $N21 = $N2 -  $N1;
    $eval = $P12 * $N21;
    $PN1 = $P1 - $N1; #classifier 1
    $PN2 = $P2 - $N2; #classifier 2

    #idea is (1) if (P1-N1)(P2-N2) < 0; easy
    #        (2) if (P1-N1)(P2-N2) > 0, let the maximal abosulte value decide

    if ($PN1*$PN2 < 0) {#easy; different sign
	if ($PN1 > 0) {
	    $$ChunkH{1}{cls} = $$tags[0];
	    $$ChunkH{2}{cls} = $$tags[1]; 
	}else {
	    $$ChunkH{1}{cls} = $$tags[1];
	    $$ChunkH{2}{cls} = $$tags[0]; 
	}
    }else {
	if (abs($PN1) > abs($PN2)) {
	    if ($P1 > $N1) {
		$$ChunkH{1}{cls} = $$tags[0];
		$$ChunkH{2}{cls} = $$tags[1];
	    }else {
		 $$ChunkH{1}{cls} = $$tags[1];
		 $$ChunkH{2}{cls} = $$tags[0]; 
	    }
	}else {
	    if ($P2 > $N2) {
		$$ChunkH{1}{cls} = $$tags[1];
		$$ChunkH{2}{cls} = $$tags[0]; 
	    }else {
		$$ChunkH{1}{cls} = $$tags[0];
		$$ChunkH{2}{cls} = $$tags[1];
	    }
	}
    }
    unlink $tmpCacheVec;
    unlink $SVMTmpResult;
    return($ChunkH);
}

sub Analyze() {
  my $resultF = shift;
  open(resultFH, "$resultF") || die "SVMHeaderParse: could not open MultiClassChunking\.pm -- Analyze: $resultF to read \n";
  my $result = <resultFH>;
  close(resultFH);
  $result =~ s/\s+$//g;
  return($result);
}


1;
