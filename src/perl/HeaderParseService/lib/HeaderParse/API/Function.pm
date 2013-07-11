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
package HeaderParse::API::Function;

use utf8;
use HeaderParse::Config::API_Config qw($Database_Dir);
use HeaderParse::API::LoadInformation;
require Exporter;
use Storable qw(nfreeze thaw);
use Data::Dumper;
use vars qw($VERSION @ISA @EXPORT @EXPORT_OK %EXPORT_TAGS);
use vars qw(%dictH %nameH %monthH %affiH %addrH %conjH %prepH %postcodeH %cityH %stateH %countryH %abstractH);

@ISA = qw(Exporter); # important!!
@EXPORT =  qw(&weired_author &AddrMatch &printDict &GenTrainVecMatrix &GetBorderLine &LineFeatureRepre &LineFeatureRepre2 &OfflineFillSpace &FillSpace &SeparatePunc &hash_stopwords &hash_nickname &hash_affi_stopwords &hash_addrwords &hash_statewords &str_space_clean &dump_hash_to_file &nfreeze_hash_to_file &read_hash_from_file &thaw_hash_from_file &rand_split_samples_to2parts &rand_split_samples_toNparts &rand_split_hash_index_toNparts &ExtractBinaryNfoldSVMResult &GetNameVariations &get_university_emails &compute_std);

sub AddrMatch() {

  ###open (MYLOGGER, ">ADDRMATCH.LOG");
  ###MYLOGGER->autoflush(1);

  my $inline = shift;

  ###$inline="Solitary Waves in the Critical Surface Tension Model";
  ###print MYLOGGER "$inline\n";

  my @words = split(/\s+/, $inline);
  my $senLen = 0;
  
  # match the state and country here using one or two words
  # this step might be very time consuming
  if ($words[0] !~ /^\W+\s*$/) {
      $senLen ++; # punctuation
  }

  ###foreach $word (@words){
  ###print MYLOGGER "before : word is \"$word\"\n";
  ###$word = lc($word);
  ###print MYLOGGER "after : word is \"$word\"\n";
  ###}


###print MYLOGGER "count is $#words\n";

  for my $i(1 .. $#words) {
   ### print MYLOGGER "word is $words[$i]\n";
    if ($words[$i] !~ /^\W+\s*$/) {
      $senLen ++; # punctuation
    }
    #the first letter is capitalized
    if (($words[$i-1] =~ /^[\p{IsUpper}]/) && ($words[$i] =~ /^[\p{IsUpper}]/)) {
      ###print MYLOGGER "before: $words[$i-1],$words[$i]\n";
       my $pre = lc($words[$i-1]);
      
       my $now = lc($words[$i]);
       ###print MYLOGGER "pre is $pre\n now is $now\n";
       if (exists $stateH{"$pre $now"}) { # need to check if it is correct
	 $words[$i-1] = "";
	 $words[$i] = ":state:";
       }elsif (exists $countryH{"$pre $now"}) {
	 $words[$i-1] = "";
	 $words[$i] = ":country:";
       }elsif (exists $cityH{"$pre $now"}) {
	 $words[$i-1] = "";
	 $words[$i] = ":city:";
       }
     }
   }
###CLOSE(MYLOGGER);
  #Broken line is because of the insufficient hard disk
  $inline = "@words"; #nice join!
  $inline =~ s/^\s+//g;
  $inline =~ s/\s+$//g;

  return($inline, $senLen);
}


sub printDict() {
  my ($TotalTrainLineCount, $dictF, %dictH) = @_;
  
  open(DictFH, ">$dictF") || die "SVMHeaderParse: could not open dictfile\: $dictF to write\n";
    # replace the old FeatureDictH with the new IDs
  foreach my $feature (sort{$dictH{$a}{ID} <=> $dictH{$b}{ID}} keys %dictH) {
    if (defined $dictH{$feature}{ID}) {
      $dictH{$feature}{mean} = sprintf("%.8f", $dictH{$feature}{mean}/$TotalTrainLineCount); 
      
      if ($dictH{$feature}{max} == 0) {
	print STDERR "$feature Yahoo1 \n";
      }
      my $ANmean = sprintf("%.8f", $dictH{$feature}{mean}/$dictH{$feature}{max});
      print DictFH "$dictH{$feature}{df} $dictH{$feature}{ID}  $feature\: max\($dictH{$feature}{max}\) BNmean\($dictH{$feature}{mean}\) ANmean\($ANmean\)\n";
    }
  }  
  close(DictFH);

  return (%dictH);
}

sub GenTrainVecMatrix() {
  my ($FeatureDictH, $TrainFeatureVecH, $TrainFeatureVec, $TrainMatrixF, $TrainTagInd, $GenMatrix, $norm, $center) = @_;

   open (TrainFeatureVec, ">$TrainFeatureVec") || die "SVMHeaderParse: could not open TrainFeatureVec $TrainFeatureVec to write\n";
    if ($GenMatrix) {
      open (TrainMatrxFH, ">$TrainMatrixF") || die "SVMHeaderParse: could not open TrainMatrF\: $TrainMatrixF to write\n";
      open (TrainTagIndFH, ">$TrainTagInd") || die "SVMHeaderParse: could not open TrainTagInd\: $TrainTagInd to write\n";
    }
    my $TmpTrainLineNo = 0;
    foreach my $s (sort {$a <=> $b} keys %{$TrainFeatureVecH}) {
      foreach my $li (sort {$a <=> $b} keys %{$$TrainFeatureVecH{$s}}) {
	$TmpTrainLineNo ++;

	#10/17 multi-class
	print TrainFeatureVec "\(";
#	print TrainTagIndFH "";
	foreach my $tmpCurState (keys %{$$TrainFeatureVecH{$s}{$li}{tag}}) {
	  print TrainFeatureVec "$tmpCurState ";
	  print TrainTagIndFH "$tmpCurState ";
	}
	print TrainFeatureVec "\) ";
	print TrainTagIndFH "\n";

	# brute force; insufficient memorty
	if ($GenMatrix == 0) {
	  if ($norm) {
	    if ($center == 1) {
	      #a loop of each feature in the dictionary.
	      foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$FeatureDictH}) {
		if (! exists ($$TrainFeatureVecH{$s}{$li}{content}{$feature})) {
		  $$TrainFeatureVecH{$s}{$li}{content}{$feature} = 0;
		}
		# norm
		my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
		#centering
		$featureVal -= sprintf("%.8f", $$FeatureDictH{$feature}{mean}/$$FeatureDictH{$feature}{max});
		$$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
		if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) {
		  print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
		}
	      }
	      print TrainFeatureVec "\n";
	    }else {
	      foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$TrainFeatureVecH{$s}{$li}{content}}) {
		if (! defined ($$FeatureDictH{$feature}{ID})) {
		  next;
		}
		my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
		$$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
		if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) {
		  print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
		}
	      }
	      print TrainFeatureVec "\n";
	    }
	  }else { # norm = 0;
	    foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$TrainFeatureVecH{$s}{$li}{content}}) {
		if (! defined ($$FeatureDictH{$feature}{ID})) {
		  next;
		}
		if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) { # must be != 0
		  print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
		}
	      }
		print TrainFeatureVec "\n";
	  }
	}else {	  
	  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$TrainFeatureVecH{$s}{$li}{content}}) {
	    if (! defined ($$FeatureDictH{$feature}{ID})) {
	      next;
	    }
	    if ($norm == 1) {
	      my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
	      $$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
	    }
	    if ($$TrainFeatureVecH{$s}{$li}{content}{$feature}) {
	      print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
	      # generate the matrix file for the training samples \n";
	      print TrainMatrxFH " $TmpTrainLineNo $$FeatureDictH{$feature}{ID} $$TrainFeatureVecH{$s}{$li}{content}{$feature}\n";
	    }
	}
	print TrainFeatureVec "\n";
      }# end if

      }# end foreach l(line)
      print TrainFeatureVec "<NEW_HEADER>\n";
      print TrainTagIndFH "<NEW_HEADER>\n";
    }#end foreach s(sample)
    close (TrainFeatureVec);
    undef (%{$TrainFeatureVecH}); # release the training vector hash
    $endTrain = 0;
    close(TrainTagIndFH);
    if ($GenMatrix) {
      close(TrainMatrxFH);
    }
}

# this is for the plaintext class -- no difference from GenTrainVecMatrix
sub GenOriginalTrainVecMatrix() {
  my ($FeatureDictH, $TrainFeatureVecH, $TrainFeatureVec, $TrainMatrixF, $TrainTagInd, $GenMatrix, $norm, $center) = @_;


   open (TrainFeatureVec, ">$TrainFeatureVec") || die "SVMHeaderParse: here1...could not open TrainFeatureVec $TrainFeatureVec to write\n";
    if ($GenMatrix) {
      open (TrainMatrxFH, ">$TrainMatrixF") || die "SVMHeaderParse: here2...could not open TrainMatrF\: $TrainMatrixF to write\n";
      open (TrainTagIndFH, ">$TrainTagInd") || die "SVMHeaderParse: here3...could not open TrainTagInd\: $TrainTagInd to write\n";
    }
    my $TmpTrainLineNo = 0;
    foreach my $s (sort {$a <=> $b} keys %{$TrainFeatureVecH}) {
      foreach my $li (sort {$a <=> $b} keys %{$$TrainFeatureVecH{$s}}) {
	$TmpTrainLineNo ++;

	#10/17 multi-class
	print TrainFeatureVec "\(";
#	print TrainTagIndFH "";
	foreach my $tmpCurState (keys %{$$TrainFeatureVecH{$s}{$li}{tag}}) {
	  print TrainFeatureVec "$tmpCurState ";
	  print TrainTagIndFH "$tmpCurState ";
	}
	print TrainFeatureVec "\) ";
	print TrainTagIndFH "\n";

	#

	if (0) {
	  print TrainFeatureVec "$$TrainFeatureVecH{$s}{$li}{tag} ";
	  print TrainTagIndFH "$$TrainFeatureVecH{$s}{$li}{tag}\n";
	}

	# brute force; insufficient memorty
	if (($GenMatrix == 0) && ($norm == 1) && ($center == 1)) {
	  #a loop of each feature in the dictionary.
	  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$FeatureDictH}) {
	    if (! exists ($$TrainFeatureVecH{$s}{$li}{content}{$feature})) {
	      $$TrainFeatureVecH{$s}{$li}{content}{$feature} = 0;
	    }
	    # norm
	    my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
	    #centering
	    $featureVal -= sprintf("%.8f", $$FeatureDictH{$feature}{mean}/$$FeatureDictH{$feature}{max});
	    $$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
	    if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) {
	      print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
	    }
	  }
	  print TrainFeatureVec "\n";
	}elsif (($GenMatrix == 0) && ($norm == 1)) {
	  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$TrainFeatureVecH{$s}{$li}{content}}) {
	    if (! defined ($$FeatureDictH{$feature}{ID})) {
	      next;
	    }
	    my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
	    $$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
	    if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) {
	      print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
	    }
	  }
	  print TrainFeatureVec "\n";
	}elsif($GenMatrix == 1) {	  
	  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %{$$TrainFeatureVecH{$s}{$li}{content}}) {
	    if (! defined ($$FeatureDictH{$feature}{ID})) {
	      next;
	    }
	    if ($norm == 1) {
	      my $featureVal = sprintf("%.8f", $$TrainFeatureVecH{$s}{$li}{content}{$feature}/$$FeatureDictH{$feature}{max});
	      $$TrainFeatureVecH{$s}{$li}{content}{$feature} = $featureVal;
	    }
	    if ($$TrainFeatureVecH{$s}{$li}{content}{$feature} != 0) {
	      print TrainFeatureVec "$$FeatureDictH{$feature}{ID}\:$$TrainFeatureVecH{$s}{$li}{content}{$feature} ";
	      # generate the matrix file for the training samples \n";
	      print TrainMatrxFH " $TmpTrainLineNo $$FeatureDictH{$feature}{ID} $$TrainFeatureVecH{$s}{$li}{content}{$feature}\n";
	    }
	}
	print TrainFeatureVec "\n";
      }# end if

      }# end foreach l(line)
      print TrainFeatureVec "<NEW_HEADER>\n";
      print TrainTagIndFH "<NEW_HEADER>\n";
    }#end foreach s(sample)
    close (TrainFeatureVec);
    undef (%{$TrainFeatureVecH}); # release the training vector hash
    $endTrain = 0;
    close(TrainTagIndFH);
    if ($GenMatrix) {
      close(TrainMatrxFH);
    }
}



sub GetBorderLine() {
  my $InFile = shift; # this file contains the sample separator
  my %BorderLineH;
  my $LineNO = 0;

  open(INFH, "$InFile") || die "SVMHeaderParse: could not open Infile\: $InFile to read \n";
  while (my $li =<INFH>) {
    $li =~ s/^\s+//g;
    $li =~ s/\s+$//g;
    if ($li !~ /^\s*$/) {
      $LineNO++;
      if ($LineNO == 1) {
        $BorderLineH{$LineNO} = "N"; #only has next line
      }elsif ($li =~ /^\<NEW\_HEADER\>/) {
        $BorderLineH{$LineNO-1} = "P";
        $BorderLineH{$LineNO+1} = "N";
      }
    }
  }
  close(INFH);
  delete($BorderLineH{$LineNO+1}); # delete the last line

  return(\%BorderLineH);
}

#all relevant domain databases are imported as shown at the beginning of this 
#program
#useful for OfflineSeparateMultiClassLine.pl esp. for printing
sub LineFeatureRepre2() {
  my $label = shift;
  my $line = shift;
  my $FeatureDictH = shift; 
  my $FiletoPrint = shift;

  my $neutral = 1;
  my $neutralAddName = 0;
  my $norm = 1;

  my %TestFeatureVecH = (); #very important
  
  #some of these features might not work for single word case such as 
  #senLen, so might just take this factor out for word case
  #########categorical features################
  my $senLen = 0;
  my $dateNum = 0;
  my $DictWordNum = 0;
  my $NonDictWordNum = 0;
  my $Cap1DictWordNum = 0;
  my $Cap1NonDictWordNum = 0;
  my $digitNum = 0;
  my $others = 0;
  my $affiNum = 0;	
  my $addrNum = 0; # let city, state, country all counted as the addr
                   # for word case, we might need more specific recognition
  my $capNum = 0;
  my $introNum = 0;
  my $phoneNum = 0;
  my $degreeNum = 0;
  my $pubNum = 0;
  my $noteNum = 0;
  my $pageNum = 0;
  ###

  my $TokenLine;			 				 
  if (length($line) > 1) {
    ($TokenLine, $senLen) = &AddrMatch($line); # this is to match the bi-grams in the address database; assume bi-gram is unique for address
    #transformed features				  
  }else {
    $TokenLine = $line;
  }
  
  my @words = split(/\s+/, $TokenLine);
  #now start the AddrNameConfu, shared among address and people's name 
  #normally do not use this representation

  for my $i(0 .. $#words) {
    if ($words[$i] =~ /\+PAGE\+/) {
      $words[$i] = ":page:";
      $pageNum++;
    }
  } # end with for each word
  
  #match bi-gram on Pubnum, Note and Degree and affiliation (might make it a separate func)					       
  if (($neutral) && (length($line) > 1)) {
    for my $i(1 .. $#words) {
      my $pre = lc($words[$i-1]);
      my $now = lc($words[$i]);
      my $prestem;
      my $nowstem;
      my $degreeMatch;
      my $pubnumMatch;
      my $noteMatch;
      my $affiMatch;

      if ($stem) {
	$prestem = &PSTEM::stem($pre);
	$nowstem = &PSTEM::stem($now);
	$degreeMatch = $degreeH{lc("$prestem $nowstem")};
	$pubnumMatch = $pubnumH{lc("$prestem $nowstem")};
	$noteMatch = $noteH{lc("$prestem $nowstem")};
	$affiMatch = $affiH{lc("$prestem $nowstem")};
      }else { # for bigram match, we do not request both to be capitalized
	$degreeMatch = $degreeH{lc("$pre $now")};
	$pubnumMatch = $pubnumH{lc("$pre $now")};
	$noteMatch = $noteH{lc("$pre $now")};
	$affiMatch = $affiH{lc("$pre $now")};
      }
      
      
      if (($pre =~ /^\s*$/) || ($pre =~ /\:\w+\:/)) {next; }
      
      my %Confuse4BiGram = (
			    1 => 0,
			    2 => 0,
			    3 => 0,
			    4 => 0
			   );
      my $match = 0;
      if ($degreeMatch) {
	$Confuse4BiGram{1} = 1;
	$match = 1;
      }
      if ($pubnumMatch) {
	$Confuse4BiGram{2} = 1;
	$match = 1;
      }
      if ($noteMatch) {
	$Confuse4BiGram{3} = 1;
	$match = 1;
      }
      
      if ($affiMatch) {
	$Confuse4BiGram{4} = 1;
	$match = 1;
      }
      
      if ($match == 0) { next; }
      
      $words[$i] = "\:Confuse4BiGram";
      foreach my $ind(sort {$a <=> $b} keys %Confuse4BiGram) {
	$words[$i] .= "$Confuse4BiGram{$ind}";
      }
      $words[$i] .= "\:";
      
      if ($words[$i] eq "\:Confuse4BiGram1000\:") {
	$words[$i-1] = "";
	$words[$i] = ":degree:";
	$degreeNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0100\:") {
	$words[$i-1] = "";
	$words[$i] = ":pubnum:";
	$pubNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0010\:") {
	$words[$i-1] = "";
	$words[$i] = ":note:";
	$noteNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0001\:") {
	$words[$i-1] = "";
	$words[$i] = ":affi:";
	$affiNum++;
      }
    }
  }#end with neutral bigram
  
  # single words match on Pubnum, notes and degree!
  for my $i(0 .. $#words) {
    if (($words[$i] !~ /\:\w+\:/) && ($words[$i] !~ /^\W+\s*$/)) {
      if ($neutral) {
	my %Confuse4Single = (
			      1 => 0,
			      2 => 0,
			      3 => 0,
			      4 => 0
			     );
	my $match = 0;
	my $degreeMatch;
	my $pubnumMatch;
	my $noteMatch;
	my $affiMatch;
	my $stemword;
	
	if ($stem) {
	  $stemword = &PSTEM::stem($stemword);
	  $degreeMatch = $degreeH{$stemword};
	  $pubnumMatch = $pubnumH{$stemword};
	  $noteMatch = $noteH{$stemword};
	  $affiMatch = ($words[$i] =~ /^[\p{IsUpper}]/ && $affiH{$stemword});
	}else {
	  $degreeMatch = $degreeH{lc($words[$i])};
	  $pubnumMatch = $pubnumH{lc($words[$i])};
	  $noteMatch = $noteH{lc($words[$i])};
	  $affiMatch = ($words[$i] =~ /^[\p{IsUpper}]/ && $affiH{lc($words[$i])});
	}
	
	#because hhan@cse.psu.edu will become hhan.psu.edu after stemming
	#and $stemword is lower case
	if ($degreeMatch) {
	  $Confuse4Single{1} = 1;
	  $match = 1;
	}
	if ($pubnumMatch) {
	  $Confuse4Single{2} = 1;
	  $match = 1;
	}
	if ($noteMatch) {
	  $Confuse4Single{3} = 1;
	  $match = 1;
	}
	if ($affiMatch) {
	  $Confuse4Single{4} = 1;
	  $match = 1;
	}
	
	if ($match) {
	  $words[$i] = "\:Confuse4Single";
	  foreach my $ind(sort {$a <=> $b} keys %Confuse4Single) {
	    $words[$i] .= "$Confuse4Single{$ind}";
	  }
	  $words[$i] .= "\:";
	  if ($words[$i] eq "\:Confuse4Single1000\:") {
	    $words[$i] = ":degree:";
	    $degreeNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0100\:") {
	    $words[$i] = ":pubnum:";
	    $pubNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0010\:") {
	    $words[$i] = ":note:";
	    $noteNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0001\:") {
	    $words[$i] = ":affi:";
	    $affiNum++;
	  }
	}
      }# end with neutral

      if ($words[$i] !~ /\:\w+\:/) {
	if (exists($conjH{$words[$i]})) {
	  $words[$i] = ":conj:";
	}elsif (exists($prepH{$words[$i]})) {
	  $words[$i] = ":prep:";
	}elsif ($words[$i] =~ /\@/) {
	  $words[$i] = "\:Email\:";
	}elsif ($words[$i] =~ /(http)|(ftp)\:\/\/(\w+\.){1,}/i) {
	  $words[$i] = "\:http\:";
	}elsif ($words[$i] =~ /^[\p{IsUpper}]/) { # Capitalize letter 1
	  if ((length($words[$i]) == 1) || ($words[$i] =~ /^[\p{IsUpper}]\.$/)) {
	    $words[$i] = ":SingleCap:"; #like M
	    $capNum ++; # actually only the number of single cap
	  }elsif (exists ($postcodeH{lc($words[$i])})) { # 2 caps
	    $words[$i] = ":postcode:";
	  }elsif (($i == 0) && ($abstractH{lc($words[$i])})) {
	    $words[$i] = ":abstract:";
	  }elsif (($i == 0) && ($keywordH{lc($words[$i])})) {
	    $words[$i] = ":keyword:";
	  }elsif ($introH{lc($words[$i])}) {
	    $words[$i] = ":intro:";
	    $introNum++;
	  }elsif ($phoneH{lc($words[$i])}) {
	    $words[$i] = ":phone:";
	    $phoneNum++;
	  }elsif ($monthH{lc($words[$i])}) {
	    $words[$i] = ":month:";
	    $dateNum++;
	  }else {
	    if ($neutral) {
	      if ($addrH{lc($words[$i])}) {
		$words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($cityH{lc($words[$i])}) { #If not neutral class
		$words[$i] = ":city:";
		$addrNum++;
	      }elsif ($stateH{lc($words[$i])}) {
		$words[$i] = ":state:";
		$addrNum++;
	      }elsif ($countryH{lc($words[$i])}) {
		$words[$i] = ":country:";
		$addrNum++;
	      }elsif ($nameH{lc($words[$i])}) { # end with not neutral class
		$words[$i] = ":MayName:";
		$Cap1NonDictWordNum ++;
	      }elsif ($dictH{lc($words[$i])}) {
		$words[$i] = ":Cap1DictWord:";
		$Cap1DictWordNum ++;		
	      }elsif ($words[$i] =~ /\W+|\-/) { #like BU-CS-93-015; maybe the length could be relaxed; I add \W+ here!!!
		my @Parts = split(/\W+|\-/, $words[$i]);
		for $i(0 .. $#Parts) {
		  if ($Parts[$i] =~ /^[\p{IsLower}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:LowerWord"."$len"."\:";
		    $Parts[$i] = "\:LowerWords\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:CapWord"."$len"."\:";
		    $Parts[$i] = "\:CapWords\:";
		  }elsif ($Parts[$i] =~ /^\d+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:Dig\[$len\]\:";
		    $Parts[$i] = "\:Digs\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}\p{IsLower}]+$/) {
		    $Parts[$i] = "\:MixCaseWords\:";
		  }else {
		    my $len = length($Parts[$i]);
		    $Parts[$i] = "\:Mix\[$len\]\:";
		  }
		}
		$words[$i] = join("\-", @Parts);
	      }elsif ($words[$i] =~ /^[\p{IsUpper}]+$/) {
		my $len = length($words[$i]);
		$words[$i] = "\:CapWord"."$len"."\:";
		#	      $words[$i] = "\:CapWords\:";
	      }else {
		$words[$i] = ":Cap1NonDictWord:";
		$Cap1NonDictWordNum ++;
	      }
	    }else {
	      if ($degreeH{lc($words[$i])}) {
		$words[$i] = ":degree:";
		$degreeNum++;
	      }elsif ($pubnumH{lc($words[$i])}) {
		$words[$i] = ":pubnum:";
		$pubNum++;
	      }elsif ($noteH{lc($words[$i])}) {
		$words[$i] = ":note:";
		$noteNum++;
	      }elsif ($monthH{lc($words[$i])}) {
		$words[$i] = ":month:";
		$dateNum++;
	      }elsif ($affiH{lc($words[$i])}) {
		$words[$i] = ":affi:";
		$affiNum++;
	      }elsif ($addrH{lc($words[$i])}) {
		$words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($cityH{lc($words[$i])}) { #If not neutral class
		$words[$i] = ":city:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($stateH{lc($words[$i])}) {
		$words[$i] = ":state:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($countryH{lc($words[$i])}) {
		$words[$i] = ":country:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($nameH{lc($words[$i])}) { # end with not neutral class
		$words[$i] = ":MayName:";
		$Cap1NonDictWordNum ++;
	      }elsif ( $dictH{lc($words[$i])}) {
		$words[$i] = ":Cap1DictWord:";
		$Cap1DictWordNum ++;		
	      }elsif ($words[$i] =~ /\W+|\-/) { #like BU-CS-93-015; maybe the length could be relaxed; I add \W+ here!!!
		my @Parts = split(/\W+|\-/, $words[$i]);
		for $i(0 .. $#Parts) {
		  if ($Parts[$i] =~ /^[\p{IsLower}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:LowerWord"."$len"."\:";
		    $Parts[$i] = "\:LowerWords\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:CapWord"."$len"."\:";
		    $Parts[$i] = "\:CapWords\:";
		  }elsif ($Parts[$i] =~ /^\d+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:Dig\[$len\]\:";
		    $Parts[$i] = "\:Digs\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}\p{IsLower}]+$/) {
		    $Parts[$i] = "\:MixCaseWords\:";
		  }else {
		    my $len = length($Parts[$i]);
		    $Parts[$i] = "\:Mix\[$len\]\:";
		  }
		}
		$words[$i] = join("\-", @Parts);
	      }elsif ($words[$i] =~ /^[\p{IsUpper}]+$/) {
		my $len = length($words[$i]);
		$words[$i] = "\:CapWord"."$len"."\:";
		#	      $words[$i] = "\:CapWords\:";
	      }else {
		$words[$i] = ":Cap1NonDictWord:";
		$Cap1NonDictWordNum ++;
	      }
	    }
	  }#end with neutral
	}elsif ($words[$i] =~ /^[\p{IsLower}]/) { # small case letter 1
	  if (exists ($phoneH{$words[$i]})) {
	    $words[$i] = ":phone:";
	    $phoneNum++;
          }elsif (exists ($monthH{lc($words[$i])})) {
	    $words[$i] = ":month:";
	    $dateNum++;
	  }elsif ($keywordH{lc($words[$i])}) {
	    $words[$i] = ":keyword:";
	  }elsif (exists $dictH{lc($words[$i])}) { 
	    $words[$i] = ":DictWord:";
	    $DictWordNum ++;		
	  }else {# should consider the mixure of digit and letters
	    $words[$i] = ":NonDictWord:";
	    $NonDictWordNum ++;
	  } 
	}elsif ($words[$i] =~ /^[\d\-]+$/) { #like 30332-0280 or 1111
	  my $newword = $words[$i];
	  while ($words[$i] =~ /(\d+)/g) {
	    my $dig = $1;
	    my $diglen = length($dig);
	    $newword =~ s/$dig/ \:Dig\[$diglen\]\: /;
	  }
	  $words[$i] = $newword;
	  $digitNum++;
	}elsif ($words[$i] =~ /^(\W+)(.*)$/) { #start from a non-word character
	  my $nonword = $1;
	  my $rest = $2;
	  $words[$i] = $nonword;
	  while (length($rest) > 0) {
	    if ($rest =~ /^([\p{IsUpper}]+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:CapWords\:".length($tmp); #length may be relaxed
	    }elsif ($rest =~ /^([\p{IsLower}]+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:LowerWords\:".length($tmp);
	    }elsif ($rest =~ /^(\d+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:Digs\:".length($tmp);
	    }else { #get the head character
	      my $restLen = length($rest);
	      $restLen--;
	      $words[$i] .= substr($rest, 0, 1);
	      $rest = substr($rest, 1, $restLen);
	    }
	  }
	}else {
	  $others++;
	}
      }
    }else {
      #      print " already token or punctuation\: $words[$i] \n";
    }
  }
  
  for my $i(0 .. $#words) {
      if (exists ($$FeatureDictH{$words[$i]}{ID})) {
	  $TestFeatureVecH{$words[$i]}++;
      }
  }

  # here we add in the bigrams
  if (length($line) > 1) {
    for my $i(1 .. $#words) { #not good for (0 .. $#words-1) soemtimes
      my $pre = $words[$i-1];
      my $now = $words[$i];
      # add bigram into dict and train or test vector
      if (exists ($$FeatureDictH{"$pre $now"}{ID})) {
	$TestFeatureVecH{"$pre $now"}++;
      }
    } # end with bigram features
  }
  
  # try to normalize using F1
  $TestFeatureVecH{CsenLen} = $senLen;
  if ($senLen > 0) {
    $TestFeatureVecH{CdateNumPer} =  sprintf("%.8f", $dateNum/$senLen);
    $TestFeatureVecH{CDictWordNumPer} = sprintf("%.8f", $DictWordNum/$senLen);
    $TestFeatureVecH{CNonDictWordNumPer} =  sprintf("%.8f", $NonDictWordNum/$senLen);
    $TestFeatureVecH{CCap1DictWordNumPer} = sprintf("%.8f", $Cap1DictWordNum/$senLen);
    $TestFeatureVecH{CCap1NonDictWordNumPer} = sprintf("%.8f", $Cap1NonDictWordNum/$senLen);
    $TestFeatureVecH{CdigitNumPer} = sprintf("%.8f", $digitNum/$senLen);
    $TestFeatureVecH{CaffiNumPer} = sprintf("%.8f", $affiNum/$senLen);
    $TestFeatureVecH{CaddrNumPer} = sprintf("%.8f", $addrNum/$senLen);
    $TestFeatureVecH{CintroNumPer} = sprintf("%.8f",$introNum/$senLen);
    $TestFeatureVecH{CphoneNumPer} = sprintf("%.8f",$phoneNum/$senLen);
    $TestFeatureVecH{CdegreeNumPer} = sprintf("%.8f",$degreeNum/$senLen);
    $TestFeatureVecH{CpubNumPer} = sprintf("%.8f",$pubNum/$senLen);
    $TestFeatureVecH{CnoteNumPer} = sprintf("%.8f",$noteNum/$senLen);
    $TestFeatureVecH{CpageNumPer} = sprintf("%.8f",$pageNum/$senLen);
    $TestFeatureVecH{CcapNumPer} = sprintf("%.8f",$capNum/$senLen);
    $TestFeatureVecH{CothersPer} =  sprintf("%.8f", $others/$senLen);
    #$TestFeatureVecH{ClinePos} = sprintf("%.8f", $linePos);
  }else {
    #print "null line\: $line \n";
  }
 
  if ($FiletoPrint ne "") { 
      open(PFH, ">$FiletoPrint") || die "SVMHeaderParse: here4...could not open $FiletoPrint to write\n";
      print PFH "$label ";
  }

  my $SVMFeaVec = "$label "; #this is a string 
  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %TestFeatureVecH) {
  if ($TestFeatureVecH{$feature} != 0){
     if ($norm) {
	if ($$FeatureDictH{$feature}{max} != 0) {
	# print "feature: $TestFeatureVecH{$feature} ; dict $$FeatureDictH{$feature}{max} => ";
	  my $tmpval =  sprintf("%.8f", $TestFeatureVecH{$feature}/$$FeatureDictH{$feature}{max});
	  $TestFeatureVecH{$feature} = $tmpval;
	  #print "  $TestFeatureVecH{$feature} \n";
	}else {
	  #print "zero max\: $feature \n";
	}
      }
      if ($FiletoPrint ne "") {
      	print PFH "$$FeatureDictH{$feature}{ID}\:$TestFeatureVecH{$feature} ";
      }
      $SVMFeaVec .= "$$FeatureDictH{$feature}{ID}\:$TestFeatureVecH{$feature} ";
   }else {
     #print "zero value\: $feature ($TestFeatureVecH{$feature}) \n";
   }
  }
  if ($FiletoPrint ne "") {
      print PFH "\n";
      close(PFH); 
  }

  my $convertedStr = join(" ", @words);
  #return(\%TestFeatureVecH);
   return($SVMFeaVec);
  #return($convertedStr);
}


sub LineFeatureRepre() {
  my $line = shift;
  my $neutral = 1;
  my $neutralAddName = 0;
  my $norm = 1;
  my %TestFeatureVecH = (); #very important
  
  #some of these features might not work for single word case such as 
  #senLen, so might just take this factor out for word case
  #########categorical features################
  my $senLen = 0;
  my $dateNum = 0;
  my $DictWordNum = 0;
  my $NonDictWordNum = 0;
  my $Cap1DictWordNum = 0;
  my $Cap1NonDictWordNum = 0;
  my $digitNum = 0;
  my $others = 0;
  my $affiNum = 0;	
  my $addrNum = 0; # let city, state, country all counted as the addr
                   # for word case, we might need more specific recognition
  my $capNum = 0;
  my $introNum = 0;
  my $phoneNum = 0;
  my $degreeNum = 0;
  my $pubNum = 0;
  my $noteNum = 0;
  my $pageNum = 0;
  ###

  my $TokenLine;			 				 
  if (length($line) > 1) {
    ($TokenLine, $senLen) = &AddrMatch($line); # this is to match the bi-grams in the address database; assume bi-gram is unique for address
    #transformed features				  
  }else {
    $TokenLine = $line;
  }
  my @words = split(/\s+/, $TokenLine);
  #now start the AddrNameConfu, shared among address and people's name 
  #normally do not use this representation

  for my $i(0 .. $#words) {
    if ($words[$i] =~ /\+PAGE\+/) {
      $words[$i] = ":page:";
      $pageNum++;
    }
  } # end with for each word
  
  #match bi-gram on Pubnum, Note and Degree and affiliation (might make it a separate func)					       
  if (($neutral) && (length($line) > 1)) {
    for my $i(1 .. $#words) {
      my $pre = lc($words[$i-1]);
      my $now = lc($words[$i]);
      my $prestem;
      my $nowstem;
      my $degreeMatch;
      my $pubnumMatch;
      my $noteMatch;
      my $affiMatch;

      if ($stem) {
	$prestem = &PSTEM::stem($pre);
	$nowstem = &PSTEM::stem($now);
	$degreeMatch = $degreeH{lc("$prestem $nowstem")};
	$pubnumMatch = $pubnumH{lc("$prestem $nowstem")};
	$noteMatch = $noteH{lc("$prestem $nowstem")};
	$affiMatch = $affiH{lc("$prestem $nowstem")};
      }else { # for bigram match, we do not request both to be capitalized
	$degreeMatch = $degreeH{lc("$pre $now")};
	$pubnumMatch = $pubnumH{lc("$pre $now")};
	$noteMatch = $noteH{lc("$pre $now")};
	$affiMatch = $affiH{lc("$pre $now")};
      }
      
      
      if (($pre =~ /^\s*$/) || ($pre =~ /\:\w+\:/)) {next; }
      
      my %Confuse4BiGram = (
			    1 => 0,
			    2 => 0,
			    3 => 0,
			    4 => 0
			   );
      my $match = 0;
      if ($degreeMatch) {
	$Confuse4BiGram{1} = 1;
	$match = 1;
      }
      if ($pubnumMatch) {
	$Confuse4BiGram{2} = 1;
	$match = 1;
      }
      if ($noteMatch) {
	$Confuse4BiGram{3} = 1;
	$match = 1;
      }
      
      if ($affiMatch) {
	$Confuse4BiGram{4} = 1;
	$match = 1;
      }
      
      if ($match == 0) { next; }
      
      $words[$i] = "\:Confuse4BiGram";
      foreach my $ind(sort {$a <=> $b} keys %Confuse4BiGram) {
	$words[$i] .= "$Confuse4BiGram{$ind}";
      }
      $words[$i] .= "\:";
      
      if ($words[$i] eq "\:Confuse4BiGram1000\:") {
	$words[$i-1] = "";
	$words[$i] = ":degree:";
	$degreeNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0100\:") {
	$words[$i-1] = "";
	$words[$i] = ":pubnum:";
	$pubNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0010\:") {
	$words[$i-1] = "";
	$words[$i] = ":note:";
	$noteNum++;
      }elsif ($words[$i] eq "\:Confuse4BiGram0001\:") {
	$words[$i-1] = "";
	$words[$i] = ":affi:";
	$affiNum++;
      }
    }
  }#end with neutral bigram
  
  # single words match on Pubnum, notes and degree!
  for my $i(0 .. $#words) {
    if (($words[$i] !~ /\:\w+\:/) && ($words[$i] !~ /^\W+\s*$/)) {
      if ($neutral) {
	my %Confuse4Single = (
			      1 => 0,
			      2 => 0,
			      3 => 0,
			      4 => 0
			     );
	my $match = 0;
	my $degreeMatch;
	my $pubnumMatch;
	my $noteMatch;
	my $affiMatch;
	my $stemword;
	
	if ($stem) {
	  $stemword = &PSTEM::stem($stemword);
	  $degreeMatch = $degreeH{$stemword};
	  $pubnumMatch = $pubnumH{$stemword};
	  $noteMatch = $noteH{$stemword};
	  $affiMatch = ($words[$i] =~ /^[\p{IsUpper}]/ && $affiH{$stemword});
	}else {
	  $degreeMatch = $degreeH{lc($words[$i])};
	  $pubnumMatch = $pubnumH{lc($words[$i])};
	  $noteMatch = $noteH{lc($words[$i])};
	  $affiMatch = ($words[$i] =~ /^[\p{IsUpper}]/ && $affiH{lc($words[$i])});
	}
	
	#because hhan@cse.psu.edu will become hhan.psu.edu after stemming
	#and $stemword is lower case
	if ($degreeMatch) {
	  $Confuse4Single{1} = 1;
	  $match = 1;
	}
	if ($pubnumMatch) {
	  $Confuse4Single{2} = 1;
	  $match = 1;
	}
	if ($noteMatch) {
	  $Confuse4Single{3} = 1;
	  $match = 1;
	}
	if ($affiMatch) {
	  $Confuse4Single{4} = 1;
	  $match = 1;
	}
	
	if ($match) {
	  $words[$i] = "\:Confuse4Single";
	  foreach my $ind(sort {$a <=> $b} keys %Confuse4Single) {
	    $words[$i] .= "$Confuse4Single{$ind}";
	  }
	  $words[$i] .= "\:";
	  if ($words[$i] eq "\:Confuse4Single1000\:") {
	    $words[$i] = ":degree:";
	    $degreeNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0100\:") {
	    $words[$i] = ":pubnum:";
	    $pubNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0010\:") {
	    $words[$i] = ":note:";
	    $noteNum++;
	  }elsif ($words[$i] eq "\:Confuse4Single0001\:") {
	    $words[$i] = ":affi:";
	    $affiNum++;
	  }
	}
      }# end with neutral

      if ($words[$i] !~ /\:\w+\:/) {
	if (exists($conjH{$words[$i]})) {
	  $words[$i] = ":conj:";
	}elsif (exists($prepH{$words[$i]})) {
	  $words[$i] = ":prep:";
	}elsif ($words[$i] =~ /\@/) {
	  $words[$i] = "\:Email\:";
	}elsif ($words[$i] =~ /(http)|(ftp)\:\/\/(\w+\.){1,}/i) {
	  $words[$i] = "\:http\:";
	}elsif ($words[$i] =~ /^[\p{IsUpper}]/) { # Capitalize letter 1
	  if ((length($words[$i]) == 1) || ($words[$i] =~ /^[\p{IsUpper}]\.$/)) {
	    $words[$i] = ":SingleCap:"; #like M
	    $capNum ++; # actually only the number of single cap
	  }elsif (exists ($postcodeH{lc($words[$i])})) { # 2 caps
	    $words[$i] = ":postcode:";
	  }elsif (($i == 0) && ($abstractH{lc($words[$i])})) {
	    $words[$i] = ":abstract:";
	  }elsif (($i == 0) && ($keywordH{lc($words[$i])})) {
	    $words[$i] = ":keyword:";
	  }elsif ($introH{lc($words[$i])}) {
	    $words[$i] = ":intro:";
	    $introNum++;
	  }elsif ($phoneH{lc($words[$i])}) {
	    $words[$i] = ":phone:";
	    $phoneNum++;
	  }elsif ($monthH{lc($words[$i])}) {
	    $words[$i] = ":month:";
	    $dateNum++;
	  }else {
	    if ($neutral) {
	      if ($addrH{lc($words[$i])}) {
		$words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($cityH{lc($words[$i])}) { #If not neutral class
		$words[$i] = ":city:";
		$addrNum++;
	      }elsif ($stateH{lc($words[$i])}) {
		$words[$i] = ":state:";
		$addrNum++;
	      }elsif ($countryH{lc($words[$i])}) {
		$words[$i] = ":country:";
		$addrNum++;
	      }elsif ($nameH{lc($words[$i])}) { # end with not neutral class
		$words[$i] = ":MayName:";
		$Cap1NonDictWordNum ++;
	      }elsif ($dictH{lc($words[$i])}) {
		$words[$i] = ":Cap1DictWord:";
		$Cap1DictWordNum ++;		
	      }elsif ($words[$i] =~ /\W+|\-/) { #like BU-CS-93-015; maybe the length could be relaxed; I add \W+ here!!!
		my @Parts = split(/\W+|\-/, $words[$i]);
		for $i(0 .. $#Parts) {
		  if ($Parts[$i] =~ /^[\p{IsLower}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:LowerWord"."$len"."\:";
		    $Parts[$i] = "\:LowerWords\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:CapWord"."$len"."\:";
		    $Parts[$i] = "\:CapWords\:";
		  }elsif ($Parts[$i] =~ /^\d+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:Dig\[$len\]\:";
		    $Parts[$i] = "\:Digs\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}\p{IsLower}]+$/) {
		    $Parts[$i] = "\:MixCaseWords\:";
		  }else {
		    my $len = length($Parts[$i]);
		    $Parts[$i] = "\:Mix\[$len\]\:";
		  }
		}
		$words[$i] = join("\-", @Parts);
	      }elsif ($words[$i] =~ /^[\p{IsUpper}]+$/) {
		my $len = length($words[$i]);
		$words[$i] = "\:CapWord"."$len"."\:";
		#	      $words[$i] = "\:CapWords\:";
	      }else {
		$words[$i] = ":Cap1NonDictWord:";
		$Cap1NonDictWordNum ++;
	      }
	    }else {#end with neutral

	      if ($degreeH{lc($words[$i])}) {
		$words[$i] = ":degree:";
		$degreeNum++;
	      }elsif ($pubnumH{lc($words[$i])}) {
		$words[$i] = ":pubnum:";
		$pubNum++;
	      }elsif ($noteH{lc($words[$i])}) {
		$words[$i] = ":note:";
		$noteNum++;
	      }elsif ($monthH{lc($words[$i])}) {
		$words[$i] = ":month:";
		$dateNum++;
	      }elsif ($affiH{lc($words[$i])}) {
		$words[$i] = ":affi:";
		$affiNum++;
	      }elsif ($addrH{lc($words[$i])}) {
		$words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($cityH{lc($words[$i])}) { #If not neutral class
		$words[$i] = ":city:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($stateH{lc($words[$i])}) {
		$words[$i] = ":state:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($countryH{lc($words[$i])}) {
		$words[$i] = ":country:";
		#	    $words[$i] = ":addr:";
		$addrNum++;
	      }elsif ($nameH{lc($words[$i])}) { # end with not neutral class
		$words[$i] = ":MayName:";
		$Cap1NonDictWordNum ++;
	      }elsif ( $dictH{lc($words[$i])}) {
		$words[$i] = ":Cap1DictWord:";
		$Cap1DictWordNum ++;		
	      }elsif ($words[$i] =~ /\W+|\-/) { #like BU-CS-93-015; maybe the length could be relaxed; I add \W+ here!!!
		my @Parts = split(/\W+|\-/, $words[$i]);
		for $i(0 .. $#Parts) {
		  if ($Parts[$i] =~ /^[\p{IsLower}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:LowerWord"."$len"."\:";
		    $Parts[$i] = "\:LowerWords\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}]+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:CapWord"."$len"."\:";
		    $Parts[$i] = "\:CapWords\:";
		  }elsif ($Parts[$i] =~ /^\d+$/) {
		    my $len = length($Parts[$i]);
		    #	      $Parts[$i] = "\:Dig\[$len\]\:";
		    $Parts[$i] = "\:Digs\:";
		  }elsif ($Parts[$i] =~ /^[\p{IsUpper}\p{IsLower}]+$/) {
		    $Parts[$i] = "\:MixCaseWords\:";
		  }else {
		    my $len = length($Parts[$i]);
		    $Parts[$i] = "\:Mix\[$len\]\:";
		  }
		}
		$words[$i] = join("\-", @Parts);
	      }elsif ($words[$i] =~ /^[\p{IsUpper}]+$/) {
		my $len = length($words[$i]);
		$words[$i] = "\:CapWord"."$len"."\:";
		#	      $words[$i] = "\:CapWords\:";
	      }else {
		$words[$i] = ":Cap1NonDictWord:";
		$Cap1NonDictWordNum ++;
	      }
	    }
	  }#end with else neutral
	}elsif ($words[$i] =~ /^[\p{IsLower}]/) { # small case letter 1
	  if (exists ($phoneH{$words[$i]})) {
	    $words[$i] = ":phone:";
	    $phoneNum++;
          }elsif (exists ($monthH{lc($words[$i])})) {
	    $words[$i] = ":month:";
	    $dateNum++;
	  }elsif ($keywordH{lc($words[$i])}) {
	    $words[$i] = ":keyword:";
	  }elsif (exists $dictH{lc($words[$i])}) { 
	    $words[$i] = ":DictWord:";
	    $DictWordNum ++;		
	  }else {# should consider the mixure of digit and letters
	    $words[$i] = ":NonDictWord:";
	    $NonDictWordNum ++;
	  } 
	}elsif ($words[$i] =~ /^[\d\-]+$/) { #like 30332-0280 or 1111
	  my $newword = $words[$i];
	  while ($words[$i] =~ /(\d+)/g) {
	    my $dig = $1;
	    my $diglen = length($dig);
	    $newword =~ s/$dig/ \:Dig\[$diglen\]\: /;
	  }
	  $words[$i] = $newword;
	  $digitNum++;
	}elsif ($words[$i] =~ /^(\W+)(.*)$/) { #start from a non-word character
	  my $nonword = $1;
	  my $rest = $2;
	  $words[$i] = $nonword;
	  while (length($rest) > 0) {
	    if ($rest =~ /^([\p{IsUpper}]+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:CapWords\:".length($tmp); #length may be relaxed
	    }elsif ($rest =~ /^([\p{IsLower}]+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:LowerWords\:".length($tmp);
	    }elsif ($rest =~ /^(\d+)(.*)$/) {
	      my $tmp = $1;
	      $rest = $2;
	      $words[$i] .= "\:Digs\:".length($tmp);
	    }else { #get the head character
	      my $restLen = length($rest);
	      $restLen--;
	      $words[$i] .= substr($rest, 0, 1);
	      $rest = substr($rest, 1, $restLen);
	    }
	  }
	}else {
	  $others++;
	}
      }
    }else {
      #      print " already token or punctuation\: $words[$i] \n";
    }
  }
  
  for my $i(0 .. $#words) {
#      if (exists ($$FeatureDictH{$words[$i]}{ID})) {
	  $TestFeatureVecH{$words[$i]}++;
#      }
  }

  # here we add in the bigrams
  if (length($line) > 1) {
    for my $i(1 .. $#words) { #not good for (0 .. $#words-1) soemtimes
      my $pre = $words[$i-1];
      my $now = $words[$i];
      # add bigram into dict and train or test vector
#      if (exists ($$FeatureDictH{"$pre $now"}{ID})) {
	$TestFeatureVecH{"$pre $now"}++;
#      }
    } # end with bigram features
  }
  
  # try to normalize using F1
  $TestFeatureVecH{CsenLen} = $senLen;
  if ($senLen > 0) {
    $TestFeatureVecH{CdateNumPer} =  sprintf("%.8f", $dateNum/$senLen);
    $TestFeatureVecH{CDictWordNumPer} = sprintf("%.8f", $DictWordNum/$senLen);
    $TestFeatureVecH{CNonDictWordNumPer} =  sprintf("%.8f", $NonDictWordNum/$senLen);
    $TestFeatureVecH{CCap1DictWordNumPer} = sprintf("%.8f", $Cap1DictWordNum/$senLen);
    $TestFeatureVecH{CCap1NonDictWordNumPer} = sprintf("%.8f", $Cap1NonDictWordNum/$senLen);
    $TestFeatureVecH{CdigitNumPer} = sprintf("%.8f", $digitNum/$senLen);
    $TestFeatureVecH{CaffiNumPer} = sprintf("%.8f", $affiNum/$senLen);
    $TestFeatureVecH{CaddrNumPer} = sprintf("%.8f", $addrNum/$senLen);
    $TestFeatureVecH{CintroNumPer} = sprintf("%.8f",$introNum/$senLen);
    $TestFeatureVecH{CphoneNumPer} = sprintf("%.8f",$phoneNum/$senLen);
    $TestFeatureVecH{CdegreeNumPer} = sprintf("%.8f",$degreeNum/$senLen);
    $TestFeatureVecH{CpubNumPer} = sprintf("%.8f",$pubNum/$senLen);
    $TestFeatureVecH{CnoteNumPer} = sprintf("%.8f",$noteNum/$senLen);
    $TestFeatureVecH{CpageNumPer} = sprintf("%.8f",$pageNum/$senLen);
    $TestFeatureVecH{CcapNumPer} = sprintf("%.8f",$capNum/$senLen);
    $TestFeatureVecH{CothersPer} =  sprintf("%.8f", $others/$senLen);
    #$TestFeatureVecH{ClinePos} = sprintf("%.8f", $linePos);
  }else {
    #print "null line\: $line \n";
  }
  
  if ($FiletoPrint ne "") { 
      open(PFH, ">$FiletoPrint") || die "SVMHeaderParse: could not open $FiletoPrint to write: $!";
      print PFH "$label ";
  }

  if (0) {
  my $SVMFeaVec = ""; #this is a string 
  foreach my $feature (sort {$$FeatureDictH{$a}{ID} <=> $$FeatureDictH{$b}{ID}} keys %TestFeatureVecH) {
  if ($TestFeatureVecH{$feature} != 0){
     if ($norm) {
	if ($$FeatureDictH{$feature}{max} != 0) {
	# print "feature: $TestFeatureVecH{$feature} ; dict $$FeatureDictH{$feature}{max} => ";
	  my $tmpval =  sprintf("%.8f", $TestFeatureVecH{$feature}/$$FeatureDictH{$feature}{max});
	  $TestFeatureVecH{$feature} = $tmpval;
	  #print "  $TestFeatureVecH{$feature} \n";
	}else {
	  #print "zero max\: $feature \n";
	}
      }
      if ($FiletoPrint ne "") {
      	print PFH "$$FeatureDictH{$feature}{ID}\:$TestFeatureVecH{$feature} ";
      }
      $SVMFeaVec .= "$$FeatureDictH{$feature}{ID}\:$TestFeatureVecH{$feature} ";
   }else {
     #print "zero value\: $feature ($TestFeatureVecH{$feature}) \n";
   }
  }
}

  
  if ($FiletoPrint ne "") {
      print PFH "\n";
      close(PFH); 
  }

  my $convertedStr = join(" ", @words);
  return(\%TestFeatureVecH);
  # return($SVMFeaVec);
  #return($convertedStr);
}


sub WordFeatureRepre() {
  my $line = shift;
  my $dict = shift; 
  my @FeatureLine;


  return(\@FeatureLine);
}

#Given a line, make the space explicit
sub FillSpace() { #recognize <<sep>>, instead of <sep>
  my $content = shift;
  my $lineNO = 0;

  $content =~ s/\s+<<sep>>/<<sep>>/g;
  $content =~ s/<<\/sep>>\s+/<<\/sep>>/g;

  my $punc = 0; # space is the only separator
  if ($content =~ /([^\p{IsLower}\p{IsUpper}\s+\-\.\d+(<<sep>>)(<<\/sep>>)])|(\W+and\W+)/ig)  {
      $punc = 1; #contains punctuation
  }

  my @Seq = split(/(<<sep>>[^\<\>]*<<\/sep>>)/, $content); #the () keeps the spliter in the array @Seq!
  for my $i (0 .. $#Seq) {
      if ($Seq[$i] =~ /<<sep>>/) {
	  #print "spliter\: $Seq[$i] \n";
      }else {
	  #this is the place to separate the punctuations and fill the space
#	  print "before removing the space $Seq[$i] \n";
	  $Seq[$i] =~ s/\s+<<sep>>/<<sep>>/g;
	  $Seq[$i] =~ s/<<\/sep>>\s+/<<\/sep>>/g;
	  # remove space arround punctuations 
	  $Seq[$i] =~ s/\s+/ \<space\> /g;
	  $Seq[$i] =~ s/<space>\s+(\W+)\s+<space>/ $1 /g;
#	  print "after  removing the space $Seq[$i]\n";
      }
  }
  
  $content = join(" ", @Seq);
  return($punc, $content);  
}


#Given a line, make the space explicit
sub OfflineFillSpace() { #recognize <sep>
  my $content = shift;
  my $lineNO = 0;

  $content =~ s/\s+<sep>/<sep>/g;
  $content =~ s/<\/sep>\s+/<\/sep>/g;

  my $punc = 0; # space is the only separator
  if ($content =~ /([^\p{IsLower}\p{IsUpper}\s+\-\.\d+(<sep>)(<\/sep>)])|(\W+and\W+)/ig)  {
      $punc = 1; #contains punctuation
  }

  my @Seq = split(/(<sep>[^\<\>]*<\/sep>)/, $content); #the () keeps the spliter in the array @Seq!
  for my $i (0 .. $#Seq) {
      if ($Seq[$i] =~ /<sep>/) {
	  #print "spliter\: $Seq[$i] \n";
      }else {
	  #this is the place to separate the punctuations and fill the space
#	  print "before removing the space $Seq[$i] \n";
	  $Seq[$i] =~ s/\s+<sep>/<sep>/g;
	  $Seq[$i] =~ s/<\/sep>\s+/<\/sep>/g;
	  # remove space arround punctuations 
	  $Seq[$i] =~ s/\s+/ \<space\> /g;
	  $Seq[$i] =~ s/<space>\s+(\W+)\s+<space>/ $1 /g;
#	  print "after  removing the space $Seq[$i]\n";
      }
  }
  
  $content = join(" ", @Seq);
  return($punc, $content);  
}


sub SeparatePunc0108bak() {
  my $line = shift;

  #added 12/16
  $line =~ s/^\s+//g;
  $line =~ s/\s+$//g;

  $line =~ s/([^\p{IsLower}\p{IsUpper}\s+\-\d+\<\>\.]\s+)/ $1 /g;
  $line =~ s/[\w+]{3,}(\.)\s+/ $1 /g;
  $line =~ s/\s+/ /;
 
  return($line);
}

sub SeparatePunc() {
  my $line = shift;

  $line =~ s/^\s+//g;
  $line =~ s/\s+$//g;

  $line =~ s/([^\p{IsLower}\p{IsUpper}\s+\-\d+\<\>\.]\s+)/ $1 /g;
  $line =~ s/([\w+]{3,})(\.)\s+/$1 $2 /g; #"Dept. of" becomes "Dept . of"
  #How about blah, blah, ... blah. And ....
  #Dr. Smith will be keep the small dot
  #Sep. will keep the small dot as well.
  #But how about removing every dot, including Dr. and Sep. ?

#  $line =~ s/\W+$//g; #remove last punctuation
  $line =~ s/\s+/ /;

  return($line);
}


sub weired_author(){
    my $str = shift;
    
    my $weired = 0;
    my %weired_words = (
			'Departamento' =>1,
			'IN PRESS'=>1,
			'PRESS'=>1,
			'Center'=>1,
			'Ltd' =>1,
			'Universidad'=>1,
			'chair' =>1,
			'Submitted'=>1,
			'pp'=>1,
			'Version'=>1,
			'Thesis' =>1,
			'Proposal' =>1,
			'University'=>1,
			'Universiteit'=>1,
			'Institut'=>1,
			'extended'=>1,
			'abstract'=>1,
			'Laboratoire'=>1,
			'COVER PAGE'=>1,
			'COVER'=>1,
			'Page' => 1,
			'Job Title'=>1,
			'Job'=>1,
			'Title'=>1,
			'Case Study'=>1,
			'Case Sludy'=>1,
			'Case'=>1,
			'Report'=>1,
			'Reply'=>1,
			'A Report'=>1,
			'A Reply'=>1,
			'Research'=>1,
			'Paper'=>1,
			'Research Paper'=>1,
			'Research Project'=>1,
			'Project'=>1,
			'Retrospective'=>1,
			'Roadmap'=>1,
			'Tutorial'=>1,
			'WORKING PAPER'=>1,
			'Working' =>1,
			'White Paper'=>1,
			'in honor of'=>1,
			'international' =>1,
			'Dataset' =>1,
			'Sample' =>1,
			'Network'=>1,
			'Networks'=>1,
			'Academiae'=>1,
			'company'=>1,
			'Submitted'=>1,
			);

    my %filter_words = (
			'honor'=>1,
			'ed'=>1,
			'eds'=>1,
			'jr'=>1,
			'jr\.'=>1,
			'authors'=>1,
			'author' =>1,
			'editor'=>1,
			'editors'=>1,
			'with'=>1,
			'by'=>1,
			);
    
    #if separate authors into individuals.
    ##    $str =~ s/^\s*[^\p{IsLower}\p{IsUpper}\d\-\.]//g;
#    $str =~ s/[^\p{IsLower}\p{IsUpper}\d\-\.]\s*$//g;

    my @weired_words_arr = keys %weired_words;   
    my $weired_words_str = join("|", @weired_words_arr);
    
    #print "\n\nbefore: $str\n";
    $str =~ s/\./\. /g;
    $str =~ s/\d+//g;
    $str =~ s/^\s*\W+//g;
    $str =~ s/\W+\s*$//g;
    $str =~ s/\s+/ /g;
    $str = &str_space_clean($str);
    #print "after: $str \n"; 
    
    my @words = split(/\s+/, $str);
    my $lcase_num = 0;
    my $weired_form = 0;
    my @new_name = ();
    my $pure_single_letter = 1;
    for my $i(0 .. $#words) {
	if ( (length($words[$i]) > 1) && ($words[$i] !~ /^\w\.$/)) {
	    $pure_single_letter = 0;
	}
	if ($filter_words{lc($words[$i])} || ($words[$i] !~ /\w/)) {
	    next;
	}else {
	    if ($words[$i] =~ /^[\p{IsLower}\-]+$/) {
		$lcase_num++;
	    }elsif ($words[$i] =~ /[^\p{IsLower}\p{IsUpper}\-\.]/) {
		$weired_form++;
	    }
	    #make the first letter capitalized
	    $words[$i] = ucfirst(lc($words[$i])); 
	    push @new_name, $words[$i];
	}
    }
    if (($pure_single_letter) || ($str =~ /$weired_words_str/) || ($#words > 4) || ($#new_name <1) || (($#words +1 - $weired_form) < 2) || ($lcase_num>2)) {
	$weired = 1;
    }
    #print "weired:? $weired \n";
    $str = join(' ', @new_name);
    #print "final str $str\n";
    
    return($weired, $str);
}



#turn array into hash map { $hash_name{$_} =$some_value } @array_name;
sub hash_stopwords {
    my $stopword = "$Database_Dir/stopwords";
    my %stopH = ();
    open(stopReader, "$stopword") || die "SVMHeaderParse: could not open $stopword to read \n";
    while (my $line = <stopReader>) {
	$line = &str_space_clean($line);
	$stopH{$line}++;
    }
    close(stopReader);
    return(\%stopH);
}

sub hash_affi_stopwords {
    #my $DB_dir = "/home/hhan/projects/public_library/DB";
    my $stopword = "$Database_Dir/affi.txt";
    my %stopH = ();
    open(stopReader, "$stopword") || die "SVMHeaderParse: could not open $stopword to read \n";
    while (my $line = <stopReader>) {
	$line = &str_space_clean($line);
	$line =~ s/^\d+\s+//g;
	$stopH{lc($line)}++;
    }
    close(stopReader);
    return(\%stopH);
}

sub hash_nickname{
    #my $DB_dir = "/home/hhan/projects/public_library/DB";
    my $stopword = "$Database_Dir/nickname.txt";
    my %stopH = ();
    open(stopReader, "$stopword") || die "SVMHeaderParse: could not open $stopword to read \n";
    while (my $line = <stopReader>) {
	$line = &str_space_clean($line);
	my @names = split(/<>|\s*\,\s*/, $line);
	for my $i(1 .. $#names) {
	    $stopH{lc($names[0])}{lc($names[$i])} = 1;
	}
    }
    close(stopReader);
    return(\%stopH);
}

sub hash_statewords {
    #my $DB_dir = "/home/hhan/projects/public_library/DB";
    my $stopword = "$Database_Dir/statename.txt";
    my %stopH = ();
    open(stopReader, "$stopword") || die "SVMHeaderParse: could not open $stopword to read \n";
    while (my $line = <stopReader>) {
	$line = &str_space_clean($line);
	my ($state, $abbr) = split(/\s*\,\s*/, $line);
	$stopH{$abbr} = $state;
    }
    close(stopReader);
    return(\%stopH);
}


sub hash_addrwords {
    #my $DB_dir = "/home/hhan/projects/public_library/DB";
    my $stopword = "$Database_Dir/addr.txt";
    my %stopH = ();
    open(stopReader, "$stopword") || die "SVMHeaderParse: could not open $stopword to read \n";
    while (my $line = <stopReader>) {
	$line = &str_space_clean($line);
	$line =~ s/^\d+\s+//g;
	$stopH{lc($line)}++;
    }
    close(stopReader);
    return(\%stopH);
}

sub str_space_clean() {
    my $str = shift;

    $str =~ s/\s+/ /g;
    $str =~ s/^\s+//g;
    $str =~ s/\s+$//g;
    return($str);
}

sub nfreeze_hash_to_file() {
    my $H = shift;
    my $F = shift;

    my $mystring = nfreeze($H);
    open(dumpFH,  ">$F") || die "SVMHeaderParse: could not open $F to write: $!";
    print dumpFH "$mystring";
    close(dumpFH);
}

sub dump_hash_to_file() {
    my $H = shift;
    my $F = shift;

    $d = Data::Dumper->new([$H]);
    $mystring = $d->Dump;
    open(dumpFH, ">$F") || die "SVMHeaderParse: could not open $F to write: $!";
    print dumpFH "$mystring";
    close(dumpFH);
}


sub read_hash_from_file() {
  my $file = shift;

  undef $/;
  open(dumpFH, "$file") || die "SVMHeaderParse: could not open $file to read. \n";
  my $string = <dumpFH>;
  close(dumpFH);
  $/ = "\n";

  eval($string);
  return($VAR1);
}

sub thaw_hash_from_file() {
  my $file = shift;

  undef $/;
  open(dumpFH, "$file") || die "SVMHeaderParse: could not open $file to read. \n";
  my $string = <dumpFH>;
  close(dumpFH);
  $/ = "\n";

  my $VAR1 = thaw($string);
  return($VAR1);
}

sub rand_split_samples_to2parts() {
    my $samples = shift; #array
    my $ratio = shift;

    my $total_num = $#$samples;
    my $num1 = int($total_num*$ratio);
    my $num2 = $total_num - $num1;
    my (@part1, @part2);
    print STDERR "rand_split_samples_to2parts\: $ratio of $total_num is $num1\n";
    $t=time;
    srand($t); #seed
    for($j=$total_num;$j>=0;$j--){
        $r=int(rand($j));
        if (($total_num - $j) < $num1) {
            push @part1, $$samples[$r];
            #adjust the samples after the selected one
            for my $k($r .. $#$samples-1) {
                $$samples[$k] = $$samples[$k+1];
            }
            pop @$samples;
        }else {
            push @part2, $$samples[$r];
        }
    }
    return(\@part1, \@part2);
}

sub rand_split_samples_to2parts_v2() {
    my $samples = shift; #array
    my $ratio = shift;

    my $total_num = $#$samples;
    my $num1 = int($total_num*$ratio);
    my $num2 = $total_num - $num1;
    my (@part1, @part2);
    print STDERR "rand_split_samples_to2parts\: $ratio of $total_num is $num1\n";
    $t=time;
    srand($t); #seed
    for($j=$total_num;$j>=0;$j--){
        $r=int(rand($j));
        if (($total_num - $j) < $num1) {
            push @part1, $$samples[$r];
            #adjust the samples after the selected one
            for my $k($r .. $#$samples-1) {
                $$samples[$k] = $$samples[$k+1];
            }
            pop @$samples;
        }
    }
    return(\@part1, $samples);
}

sub rand_split_samples_toNparts() {
    my $samples = shift; #array
    my $fold = shift;

    my $total_num = $#$samples;
    my $unit = int($total_num/$fold +1);
    my $last_fold = $total_num - $unit*($fold-1);

    my @data = ();
    $t=time;
    srand($t); #seed
    for($j=$total_num;$j>=1;$j--){
        $r=int(rand($j));
	my $subfold = int(($total_num - $j)/$unit) + 1;
	push @{$data[$subfold]}, $$samples[$r];
	#adjust the samples after the selected one
	for my $k($r .. $#$samples-1) {
	    $$samples[$k] = $$samples[$k+1];
	}
	pop @$samples;
    }
    return(@data);
}

sub rand_split_hash_index_toNparts() {
    my $sample_hash = shift; #hash
    my $fold = shift;

    my @sample_arr = keys %{$sample_hash};
    my $total_num = $#sample_arr;
    my $unit = int($total_num/$fold +1);
    my $last_fold = $total_num - $unit*($fold-1);

    my @data = ();
    $t=time;
    srand($t); #seed
    for($j=$total_num;$j>=1;$j--){
        $r=int(rand($j));
	my $subfold = int(($total_num - $j)/$unit) + 1;
	
	my $name = $sample_arr[$r];
	my %pos = ();
	if ($$sample_hash{$name}{label} > -1) {
	    my @tmp = split(/\<\>/, $$sample_hash{$name}{label});
	    map { $pos{$_} =1 } @tmp;
	}
	foreach my $file_name (keys %{$$sample_hash{$name}{name}}) {
	    my ($tmp, $num) = split(/\_\_/, $file_name);
	    my $label = "-1";
	    if ($pos{$num}) {
		$label = "+1";
	    }
	    push @{$data[$subfold]}, "$label<>$file_name<>$$sample_hash{$name}{name}{$file_name}{snippet}";
	}
	
	for my $k($r .. $#sample_arr-1) {
	    $sample_arr[$k] = $sample_arr[$k+1];
	}
	pop @sample_arr;
    }
    return(@data);
}

sub ExtractBinaryNfoldSVMResult() {
    my $in = shift;
    my %ResultH = ();

    open (inFH, "$in") || die "SVMHeaderParse: could not open $in to read \n";
    while (my $line = <inFH>) {
	if ($line =~ /Accuracy on test set: (\d+\.\d+)\%/) {
	    $ResultH{A}{count}++;
	    $ResultH{A}{sum} += $1;
	}
	if ($line =~ /Precision\/recall on test set\: (.*)\%\/(.*)\%/) {
	    my $P = $1;
	    my $R = $2;
	    if ($P =~ /\d+\.\d+/) {
		$ResultH{P}{count}++;
		$ResultH{P}{sum} += $P;
	    }
	    if ($R =~ /\d+\.\d+/) {
		$ResultH{R}{count}++;
		$ResultH{R}{sum} += $R;
	    }
	}
    }
    close(inFH);
    
    print STDERR "average result from cross validation \n";
    foreach my $eval(sort {$a <=> $b} keys %ResultH) {
        $ResultH{$eval}{avg} = sprintf("%.8f", $ResultH{$eval}{sum}/$ResultH{$eval}{count});
	print STDERR "evaluation($eval) -- $ResultH{$eval}{avg}\n";
    }
}    

## get alias file
sub GetNameVariations1() {
    my $personalName = shift; #like _Chris_S._Mellish__1.txt   
    
    my @QueryNameParts = split(/\s+|\-/, $personalName);
    my %NameVariations;
    my ($FirstName, $LastName, $FI, $LI, $MI1, $MI2, $FI_LI, $AllInitial, $AllName, $FILN, $FIMI1LastName, $FIMI1MI2LastName)
	= ('','','','','','','','','','','','','');
    
    $FirstName = $QueryNameParts[0];
    $LastName = $QueryNameParts[$#QueryNameParts];
    
    $NameVariations{$FirstName} = "FN";
    $NameVariations{$LastName} = "LN";
    
    $FI = substr($FirstName, 0, 1);
    $LI = substr($LastName, 0, 1);

    $FI_LI= "$FI"."$LI";
    $FILN = "$FI"."$LastName";
    $FNLI = "$FirstName"."$LI";
    $NameVariations{$FILN} = "FILN";
    $NameVariations{$FNLI} = "FNLI";
    
    for my $i(0 .. $#QueryNameParts) {
	$QueryNameParts[$i] =~ s/\W+//g;
	$AllName .= $QueryNameParts[$i];
    }
    # dependts on whether this name contains 3 parts or 4 parts
    if ($#QueryNameParts < 1) {next;}
    if ($#QueryNameParts eq 1) {
	$AllInitial = $FI_LI; 	
	$NameVariations{$AllInitial} = "all_initial";
    }else {
	$NameVariations{$FI_LI} = "FILI";
	$NameVariations{"$FN"."$QueryNameParts[1]"} = "FNMN";
	if ($#QueryNameParts eq 2) {
	    $MI1 = substr($QueryNameParts[1], 0, 1);
	    $AllInitial= "$FI"."$MI1"."$LI"; 
	    $FIMI1LastName = "$FI"."$MI1"."$LastName";
	    $NameVariations{$AllInitial} = "all_initial";
	    $NameVariations{$FIMI1LastName} = "FIMI1LN";
	}elsif ($#NameParts eq 3) {
	    $MI1 = substr($QueryNameParts[1], 0, 1);
	    $MI2 = substr($QueryNameParts[2], 0, 1); 
	    $AllInitial = "$FI"."$MI1"."$MI2"."$LI"; 
	    $FIMI1LastName = "$FI"."$MI1"."$LastName";
	    $FIMI1MI2LastName = "$FI"."$MI1"."$MI2"."$LastName";
	    $NameVariations{$AllInitial} = "all_initial";
	    $NameVariations{$FIMI1LastName} = "FIMI1LN";
	    $NameVariations{$FIMI1MI2LastName} = "FIMI1MI2LN";
	}
    }
    
    ## It will take chance for this exact match
    if (length ($QueryNameParts[$#QueryNameParts]) < 4) {
	$PartLastName =  substr($QueryNameParts[$#QueryNameParts], 0, 5);
	$NameVariations{$PartLastName} = "partial_LN";
    }
    return(\%NameVariations);	
}

sub GetNameVariations() {
    my $personalName = shift; #like _Chris_S._Mellish__1.txt   
    my $nickname = shift;
    
    my @QueryNameParts = split(/\s+|\-/, $personalName);
    my %NameVariations;
    my ($FirstName, $LastName, $FI, $LI, $MI1, $MI2, $FI_LI, $AllInitial, $AllName, $FILN, $FIMI1LastName, $FIMI1MI2LastName)
	= ('','','','','','','','','','','','','');
    
    $FirstName = $QueryNameParts[0];
    $LastName = $QueryNameParts[$#QueryNameParts];
# using first 5 letters decreases performance if not using substring matching
#    if (length($QueryNameParts[0]) > 4) {
#	$FirstName = substr($QueryNameParts[0],0,5);
#    }
#    if (length($QueryNameParts[$#QueryNameParts]) > 4) {
#	$LastName = substr($QueryNameParts[$#QueryNameParts],0,5);
#    }    
    $NameVariations{$FirstName} = "FN";
    $NameVariations{$LastName} = "LN";
    foreach my $alias(keys %{$$nickname{lc($FirstName)}}) {
	$NameVariations{$alias} = "FN";
    }
    $FI = substr($FirstName, 0, 1);
    $LI = substr($LastName, 0, 1);

    $FI_LI= "$FI"."$LI";
    $FILN = "$FI"."\\"."w*"."$LastName";
    $FNLI = "$FirstName"."$LI";
    $LNFI = "$LastName"."$FI";
    $NameVariations{$FILN} = "FILN";
    $NameVariations{"$FI"."\."."$LastName"} = "FILN";
    $NameVariations{$FNLI} = "FNLI";
    $NameVariations{$LNFI} = "LNFI";
    
    for my $i(0 .. $#QueryNameParts) {
	$QueryNameParts[$i] =~ s/\W+//g;
	$AllName .= $QueryNameParts[$i];
    }
    $NameVariations{$AllName} = "full_name";
    $NameVariations{"\\"."w*"."$FirstName"."\\"."w*"."$LastName"."\\"."w*"} = "FNLN";
    $NameVariations{"$FirstName"."\."."$LastName"} = "FNLN";
    $NameVariations{"$LastName"."$FirstName"} = "LNFN";

    # depends on whether this name contains 3 parts or 4 parts
    if ($#QueryNameParts < 1) {next;}
    elsif ($#QueryNameParts eq 1) {
	$AllInitial = $FI_LI; 	
	$NameVariations{$AllInitial} = "all_initial";
    }else {
	$NameVariations{$FI_LI} = "FILI";
	$NameVariations{"$FirstName"."$QueryNameParts[1]"} = "FNMN";
	if ($#QueryNameParts eq 2) {
	    $MI1 = substr($QueryNameParts[1], 0, 1);
	    $AllInitial= "$FI"."$MI1"."$LI"; 
	    $FIMI1LastName = "$FI"."$MI1"."$LastName";
	    $NameVariations{$AllInitial} = "all_initial";
	    $NameVariations{$FIMI1LastName} = "FIMI1LN";
	}elsif ($#QueryNameParts eq 3) {
	    $MI1 = substr($QueryNameParts[1], 0, 1);
	    $MI2 = substr($QueryNameParts[2], 0, 1); 
	    $AllInitial = "$FI"."$MI1"."$MI2"."$LI"; 
	    $FIMI1LastName = "$FI"."$MI1"."$LastName";
	    $FIMI2LI = "$FI"."$MI2"."$LI";
	    $FIMI1LI = "$FI"."$MI1"."$LI";
	    $FIMI1MI2LastName = "$FI"."$MI1"."$MI2"."$LastName";
	    $MN2LastName = "$QueryNameParts[2]"."$LastName";
	    $NameVariations{$AllInitial} = "all_initial";
	    $NameVariations{$FIMI1LastName} = "FIMI1LN";
	    $NameVariations{$FIMI1LI} = "FIMI1LI";
	    $NameVariations{$FIMI2LI} = "FIMI2LI";
	    $NameVariations{$MN2LastName} = "MI2LN";
	    $NameVariations{$FIMI1MI2LastName} = "FIMI1MI2LN";
	}
    }
    
    ## It will take chance for this exact match
    if (length ($QueryNameParts[$#QueryNameParts]) < 4) {
	$PartLastName =  substr($QueryNameParts[$#QueryNameParts], 0, 5);
	$NameVariations{$PartLastName} = "partial_LN";
    }
    return(\%NameVariations);	
}

sub get_university_emails() {
    my $univ = "$Database_Dir/university_list/univ-full.html";
    my $simple_format = "$Database_Dir/university_list.txt";

    my %H = ();
    open(UNIV, $univ) || die "SVMHeaderParse: could not open $univ to read. \n";
    my @content = <UNIV>;
    close(UNIV);
     
    open(simpleWriter, ">$simple_format") || die "SVMHeaderParse: could not open $simple_format to write: $!";
    for my $i(0 .. $#content) {
	if ($content[$i] =~ /\<LI\>\s+\<A\s+HREF\=\"([^\"]*)\"\>(.*)\<\/A\>/) {
	    my $url = $1;
	    my $college = $2;
	    print simpleWriter "$college<>$url\n";
	}
    }
    close(simpleWriter);
    return(\%H);
}

#input: an array of value
sub compute_std() {
    my $arr = shift;
    my $mean = 0;
    my $std = 0;
    
    #cal mean
    for my $i(0 .. $#$arr) {
	$mean += $$arr[$i];
    }
    $mean = sprintf("%.3f", $mean/($#$arr+1));

    for my $i(0 .. $#$arr) {
	$std += ($$arr[$i]-$mean)**2;
    }
    my $temp = sprintf("%.8f", $std/$#$arr);
    $std = sqrt($temp);
    
    return($mean, $std);
}




1;
