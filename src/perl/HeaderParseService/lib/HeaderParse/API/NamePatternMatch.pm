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
package HeaderParse::API::NamePatternMatch;

use utf8;

#this is wrapper version, with <<>> as the separator

#11/26-11/29 
#Function 1:
#use model match method for the multi-author lines separated only by space to get all the legal patterns for the line;

#input:  a line from file [MultiAuthorLines.space.processed]
#for example:
#Chungki <space> Lee <<sep>><</sep>> James <space> E. <space> Burns


#Function 2: 
#FeatureRepresentation, given the predicted name sequence
#like\:  Chungki Lee<>James E. Burns<>


#Idea: used recursive function, which might be improved by dynamic programming later
sub NamePatternMatch() {
    my $line = shift;

#    print "NAME LINE: $line\n";

    #preprocess the line
    $line =~ s/(<<sep>>)|(<<\/sep>>)|(<space>)/ /g;
    $line =~ s/\s+[^\p{IsUpper}]+(\s+|$)/ /g;

#remove isolated punctuations or digits;   ignore the the small case letter, because the output would be the extracted names; but index needs to be kept to compute the performance finally -- problem to be solved

    $line =~ s/\+L/ /g;
    $line =~ s/^\s+//g;
    $line =~ s/\s+$//g;

    my @authors = split(/\s+/, $line);

    $AuthSeqMat = &FillSequenceMatrix(\@authors);

    my $SeparatorMat = &InitializeSepMat($#authors); # separators

    #step 2: Recursive function to find the separator
    my ($separator, $sequenceArr, $separatorArr) = &SeekSep(0, $#authors, $AuthSeqMat);

    my $PredictedNames = ();
    $PredictedNames = &printNameArray($sequenceArr, $separatorArr, \@authors, 0);
    return ($PredictedNames);
}

  #this is the recursive function of getting the separators.
  # $SeparatorMat might not be useful
  sub SeekSep() {
    my ($i, $j, $SequenceMat) = @_;
    my $separator= 0;
    my @FinalseparatorArr=();
    my @FinalsequenceArr=();

    if ($i+2 >= $j) { #non-separable unit with 3- words
      if (&Duplicate([$$SequenceMat[$i][$j]], \@FinalsequenceArr) eq "-1") {
	push @FinalsequenceArr, [$$SequenceMat[$i][$j]];      
      }
      # no separator
    }else {
      #what is the basic case 
      #consider the case of 4 words here\: Paulo A. R. Lorenzo 
      # We just add this case into candidate sequence.
	if ($i+3 == $j) {
	  if (&Duplicate([$$SequenceMat[$i][$j]], \@FinalsequenceArr) eq "-1") {
	    push @FinalsequenceArr, [$$SequenceMat[$i][$j]];      
          }
	}	
      for my $k($i+2 .. $j-1) {
	my ($leftSeparator, $leftSequenceArr, $leftSeparatorArr) = &SeekSep($i, $k-1, $SequenceMat);
	my ($rightSeparator, $rightSequenceArr, $rightSeparatorArr) = &SeekSep($k, $j, $SequenceMat);
	#left
	if (! $leftSeparator) {
	  if (&valid($$leftSequenceArr[0][0])) {
	    #got left name string
	  }else {
	    next;
	  }
	}else {#separable -- then get the sequence array and separator array   
	 
	}

	#right
	if (! $rightSeparator) {
	  if (&valid($$rightSequenceArr[0][0])) {
	    #got right name string
	  }else {
	    next;
	  }
	}else {#separable -- then get the sequence array and separator array   
	  
	}

	$separator = 1; #passed at this point
	#combine two good sub parts and the current k 
	if (! $leftSeparator) {
	  if (! $rightSeparator) {
	    @separatorArr = ("$k");
	    @sequenceArr = ($$leftSequenceArr[0][0], $$rightSequenceArr[0][0]);
	    if (&Duplicate(\@sequenceArr, \@FinalsequenceArr) eq "-1") {
	      push @FinalsequenceArr, [@sequenceArr];
	      push @FinalseparatorArr, [@separatorArr];
	    }
	  }else {
	    for my $R(0 .. $#$rightSequenceArr) {
	      @sequenceArr = ($$leftSequenceArr[0][0]);
	      my @separatorArr = ("$k");
	      for my $ri(0 .. $#{$$rightSequenceArr[$R]}) {
		@sequenceArr = (@sequenceArr, $$rightSequenceArr[$R][$ri]);
	      }
	      for my $ri(0 .. $#{$$rightSeparatorArr[$R]}) {
		@separatorArr  = (@separatorArr , $$rightSeparatorArr[$R][$ri]);
	      }
	      if (&Duplicate(\@sequenceArr, \@FinalsequenceArr) eq "-1") {
		push @FinalsequenceArr, [@sequenceArr];
		push @FinalseparatorArr, [@separatorArr];
	      }
	    }
	  }
	}else {
	  if (! $rightSeparator) {
	    for my $L(0 .. $#$leftSequenceArr) {
	      my @sequenceArr = ();
	      my @separatorArr = (); 
	      for my $li(0 .. $#{$$leftSequenceArr[$L]}) {
		@sequenceArr = (@sequenceArr, $$leftSequenceArr[$L][$li]);
	      }
	      @sequenceArr = (@sequenceArr, $$rightSequenceArr[0][0]);
	      for my $li(0 .. $#{$$leftSeparatorArr[$L]}) {
		@SeparatorArr = (@SeparatorArr, $$leftSeparatorArr[$L][$li]);
	      }
	      @SeparatorArr = (@SeparatorArr,"$k"); 
	      
	      if (&Duplicate(\@sequenceArr, \@FinalsequenceArr) eq "-1") {
		push @FinalsequenceArr, [@sequenceArr];
		push @FinalseparatorArr, [@separatorArr];
	      }
	    }
	  }else {
	    for my $L(0 .. $#$leftSequenceArr) {
	      my @WholeSequence = ();
	      my @separatorArr = (); 
	      my @leftSequence = ();
	      for my $li(0 .. $#{$$leftSequenceArr[$L]}) {
		@leftSequence = (@leftSequence, $$leftSequenceArr[$L][$li]);
	      }
	      for my $li(0 .. $#{$$leftSeparatorArr[$L]}) {
		@SeparatorArr = (@SeparatorArr, $$leftSeparatorArr[$L][$li]);
	      }
	      @SeparatorArr = (@SeparatorArr,"$k"); 

	      for my $R(0 .. $#$rightSequenceArr) {
		my @rightSequence = ();
		my @rightSeparator = (); 
		@WholeSequence = @leftSequence;
		for my $ri(0 .. $#{$$rightSequenceArr[$R]}) {
		  @rightSequence = (@rightSequence, $$rightSequenceArr[$R][$ri]);
		}
		@WholeSequence = (@WholeSequence, @rightSequence);
		for my $ri(0 .. $#{$$rightSeparatorArr[$R]}) {
		  @rightSeparator = (@rightSeparator, $$rightSeparatorArr[$R][$ri]);
		}
		@SeparatorArr = (@SeparatorArr, @rightSeparator);
		if (&Duplicate(\@WholeSequence, \@FinalsequenceArr) eq "-1") {
		  push @FinalsequenceArr, [@WholeSequence];
		  push @FinalseparatorArr, [@separatorArr];
		}
	      }
	    }
	  }
      }
      
	if (! $separator) {# no combination is valid
	  #	push @FinalsequenceArr, [$$SequenceMat[$i][$j]];  
	}
      }
    }  
    return($separator, \@FinalsequenceArr, \@FinalseparatorArr);
    
  }
  
  # this is to initialize the separator matrix
  sub InitializeSepMat() {
    my $num = shift;
    my @SeparatorMat = ();

    for my $i(0 .. $num) {
      for my $j(0 .. $i-1) {
	$SeparatorMat[$i][$j] = "-2"; #err
      }
      $SeparatorMat[$i][$i] = "-1"; #no separator
    }
    $SeparatorMat[0][1] = "-1";
    if (($num-1) > 0) {
        $SeparatorMat[$num-1][$num] = "-1";
    }
    return(\@SeparatorMat);
  }
  
  sub FillSequenceMatrix() {
    my $authors = shift;
    #step 1: Fill the sequence matrix
    my @SequenceMat = ();
    
    for my $i(0 .. $#$authors) {    
      $SequenceMat[$i][$i] = &NameType($$authors[$i]);
    }
    
    # needs adjust
    my $maxStep;
    if ($#authors > 4) {
      $maxStep = 3;
    }else {
      $maxStep = $#$authors; #remember the range starts from 0 and the maxStep = total number of words - 2
    }
    
    for my $step(1 .. $maxStep) {
      for my $i(0 .. $#$authors-$step) {
	my $tmpStr = "$SequenceMat[$i][$i+$step-1]"."$SequenceMat[$i+$step][$i+$step]";
	if (length($tmpStr) < 6) {
	  $SequenceMat[$i][$i+$step] = $tmpStr;
	  
	}
      }
    }

    return(\@SequenceMat);
  }

sub NameType() {
    my $name = shift;
    my $reval = "";
    
    if ($name =~ /^[\p{IsUpper}](\.)*$/) {
	$reval = "I";
    }elsif (($name =~ /^[\p{IsUpper}]+/) && (length($name) > 1)) {
	$reval = "F";
    }elsif (($name =~ /^[\p{IsLower}]+/) && (length($name) > 1)) {
	$reval = "s";
    }else {
	print STDERR "odd $name \n";
	$reval = "o";
    }
    return ($reval);
}


sub RichNameType() {
    my $name = shift;
    my $reval = "";
    
    $name =~ s/^\s+//g;
    $name =~ s/\s+$//g;
    
  if ($name =~ /^[\p{IsUpper}]+(\.)*$/) {
    $reval = "I";
  }elsif (($name =~ /^[\p{IsUpper}]+/) && (length($name) > 1)) {
    if ($name =~ /\w\-\w/) {
      $reval = "F-";
    }else{
      $reval = "F";
    }
  }elsif (($name =~ /^[\p{IsLower}]+/) && (length($name) > 1)) {
    $reval = "s";
  }else {
    print STDERR "odd $name \n";
    $reval = "o";
  }
  return ($reval);
}


sub printMatrix() { # obsolete
  my $TrueIndex;
  my $arr = shift; 
  my $OutFH = shift;

  for my $i(0 .. $#$arr) {
    if ($i eq $TrueIndex) {
      print OutFH "+1 ";
    }else {
      print OutFH "-1";
    }
    for my $j(0 .. $#{$$arr[$i]}) {
      if ($$arr[$i][$j]) {
	print OutFH "$$arr[$i][$j]<>";
      }else {
	print OutFH "0";
      }
    }
    print OutFH "\n";
  }
}


sub RemoveDuplicates(){ # remove duplicates(obsolete)
  my $arr = shift; 
  my %uniqueH;
  my @index = ();

  for my $i(0 .. $#$arr) {
    my $str = "";
    for my $j(0 .. $#{$$arr[$i]}) {
      if ($$arr[$i][$j]) {
	$str .= "$$arr[$i][$j]<>";
      }else {
	$str .= "<>";
      }
    }
    if (! $uniqueH{"$str"}) {
      $uniqueH{"$str"} = 1; #otherwise index $i = 0 would be considered as negative
      push @index, $i;
    }
  }
  return(\@index);
} 


sub Duplicate() { # see if a name array is already in a 2D name matrix
  my $matchArr = shift;
  my $arr = shift;
  my $duplicate = "-1";

  my $matchStr = "";
  for my $j(0 .. $#{$matchArr}) {
    $matchStr .= "$$matchArr[$j]<>";
  }
  
  for my $i(0 .. $#$arr) {
    my $tmpstr = "";
    for my $j(0 .. $#{$$arr[$i]}) {
      if ($$arr[$i][$j]) {
	$tmpstr .= "$$arr[$i][$j]<>";
      }
    }

    if ($tmpstr eq $matchStr) {
      $duplicate = $i; #rememver $i could be 0(the starting position of array)
      last;
    }
  }

  return($duplicate);
}


#Prints the pattern (FFF, FI..), separator and corresponding names
# if $PrintPattern = 1; and $print = 1;
#if $separator is 0. we should return the whole sequence as the name???
#did not consider about this case

sub printNameArray() { 
  my $NameArr = shift; 
  my $SeparatorArr = shift;
  my $OriginalName = shift;
  my $PrintPattern = shift;
  my @nameToReturn = ();

  my $print = 0;

  for my $i(0 .. $#$NameArr) {
    if ($PrintPattern) {
      print STDERR "$i\-\- ";
      for my $j(0 .. $#{$$NameArr[$i]}) {
	if ($$NameArr[$i][$j]) {
	  print STDERR "$$NameArr[$i][$j] ";
	}else {
	  print STDERR "0 ";
	}
      }
      print STDERR "Separator \[";
      for my $j(0 .. $#{$$SeparatorArr[$i]}) {
	print STDERR "$$SeparatorArr[$i][$j] ";
      }
      print STDERR "\]\n";
    }

    #print original names
    my $start = 0;
    my $end = $#$OriginalName;
#    print "Original name\:";
    my $NameNum = 0;
    for my $j(0 .. $#{$$SeparatorArr[$i]}) {
      my $end = $$SeparatorArr[$i][$j]-1;
      my $tmpName = "";
      for my $k($start .. $end) {
	if ($print) {
	  print STDERR "$$OriginalName[$k] ";
	}
	$tmpName .= "$$OriginalName[$k] ";
      }
      
      $tmpName =~ s/\s+$//g;
      $nameToReturn[$i][$NameNum] = $tmpName;
      $NameNum++;
      if ($print) {
	print STDERR "<>";
      }
      $start = $end+1;
    }
    
    $tmpName = "";
    for my $k($start .. $end) {
      if ($print) {
	print STDERR "$$OriginalName[$k] ";
      }
      $tmpName .= "$$OriginalName[$k] ";
    }
    $tmpName =~ s/\s+$//g;
    $nameToReturn[$i][$NameNum] = $tmpName;
    
    if ($print) {
      print STDERR "\n";
    }
  }

  return(\@nameToReturn);

}

sub printNameArray2() { #obsolete; which remove the duplicates before printing
  my $NameArr = shift; 
  my $SeparatorArr = shift;
  my $OriginalName = shift;
  my $Uncertainty = 0; #if it is of multiple cases
  
  my $UniqueIndex = &RemoveDuplicates($NameArr);
  if ($#$UniqueIndex > 0) {
    $Uncertainty = 1;
  }

  for my $i(0 .. $#$UniqueIndex) {
    print "$$UniqueIndex[$i]\-\- ";
    for my $j(0 .. $#{$$NameArr[$$UniqueIndex[$i]]}) {
      if ($$NameArr[$$UniqueIndex[$i]][$j]) {
	print STDERR "$$NameArr[$$UniqueIndex[$i]][$j] ";
      }else {
	print STDERR "0 ";
      }
    }
    print STDERR "Separator \[";
    for my $j(0 .. $#{$$SeparatorArr[$$UniqueIndex[$i]]}) {
      print STDERR "$$SeparatorArr[$$UniqueIndex[$i]][$j] ";
    }
    print STDERR "\]\n";

    #print original names according to the sorted separators
    my $start = 0;
    my $end = $#$OriginalName;
    print STDERR "Original name\:";
    for my $j(0 .. $#{$$SeparatorArr[$$UniqueIndex[$i]]}) {
      my $end = $$SeparatorArr[$$UniqueIndex[$i]][$j]-1;
      for my $k ($start .. $end) {
	print STDERR "$$OriginalName[$k] ";
      }
      print STDERR "<>";
      $start = $end+1;
    }
    for my $k ($start .. $end) {
      print STDERR "$$OriginalName[$k] ";
    }
    print STDERR "\n";
  }

 # return($Uncertainty);
}

sub valid() {
  my $str= shift;
  my %ValidPattern = (
		   "FF" => 1, #[FullName] [FullName]
		   "FFF" => 1,#[FullName] [FullName] [FullName]
		   "FIF" => 1,#FIF and derivatives: [FullName] [NameInitial]{1,3}/ [FullName]
		   "FIIF" => 1, #Paulo A. R. Lorenzo
		   "FIIIF" => 1,
		   "IF" => 1, ##IF and derivatives:[NameInitial]{1,2} [FullName]
		   "IIF" => 1, 
		   "IFF" => 1,#E. Christopher Lewis
		   "FssF" => 1 #Th.P. van der Weide   
		     );

  if ($ValidPattern{$str}) {
    return (1);
  }else {
    return (0);
  }
}

# this is to present the macro features of the predicted names see
# if this is correct prediction; 
# make it ready for training/testing or not?

sub NamePatternFeatureRepresent() {

return (1);

}

#Input format: Chungki <space> Lee <<sep>><</sep>> James <space> E. <space> Burns
sub GetTrueName() {
  my $in = shift;
  my @authors = split(/<<sep>>[^(<<)(>>)]*<<\/sep>>/, $in);
  
  for my $i(0 .. $#authors) {
    $authors[$i] =~ s/(<space>)/ /g;
    $authors[$i] =~ s/\s+[^\p{IsUpper}]+(\s+|$)/ /g; 
    #remove isolated punctuations or digits; 
    #the small case letter like 'n', 'x'
    #Disadvantage is: Th.P. van der Weide n would become "Th.P. Weide"
    #Index to the original word position should be kept
  
    $authors[$i] =~ s/\+L/ /g;
    $authors[$i] =~ s/^\s+//g;
    $authors[$i] =~ s/\s+$//g;
  }
				 
  return(\@authors);
}


1;
