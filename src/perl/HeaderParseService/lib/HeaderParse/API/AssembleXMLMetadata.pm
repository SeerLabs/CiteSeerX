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
package HeaderParse::API::AssembleXMLMetadata;

# example input:
#<DID> 5 </DID>
#<note>Third ILOG International Users Meeting ,  9  10 July 1997 ,  Paris ,  France</note>
#<title>Daily management of an earth observation satellite :</title>
#<title>comparison of ILOG Solver with dedicated algorithms</title>
#<title>for Valued Constraint Satisfaction Problems</title>
#<author>Michel Lemaitre</author>
#<author>G'erard Verfaillie</author>
#<abstract>ONERA/CERT</abstract>
#<abstract>2 ,  avenue</abstract>
#<address>Edouard Belin -- BP 4025 --</address>
#<address>31055 Toulouse cedex 4 -- France</address>
#<email>fMichel.Lemaitre,Gerard.Verfaillieg@cert.fr</email>

################Function Description########################
# Find the name and its affiliation/address mapping
#Assumption: 
#(1) It is valid to split the header by authors into chunks; each resulting chunk has complete informaiton of the authors in that chunk
#(2) Use edit-distance to find the mapping between authors and emails.

#For each author chunk with N authors.
#case 1: N = 1;
#	(a) if (exists following affi. and addr.) {
#		combine the following affi. and addrs.
#		}
#	(b) else 
#		warning

#case 2: N > 1;
#	(a) if the following affi. and addrs == 1
#		these N people share the affi. and addr.
#	(b) if the following affi. and addrs == N
#		map 1-1
#	(c) otherwise
#		warning

#package finalize_metata_extraction_v4;
use utf8;
use HeaderParse::Config::API_Config;
use Data::Dumper;
use String::Approx 'adist';
use HeaderParse::API::Function qw(&weired_author);
use CSXUtil::SafeText qw(&cleanXML &cleanAll);


sub assemble(){
    my $rstr = shift;

    $$rstr =~ s/^\s+//g;
    $$rstr =~ s/\s+$//g;

    my @xml_arr = split(/<DID>\s*(\d+)\s*<\/DID>/, $$rstr);

    $did=1;
    $xml_hash{$did} = $$rstr;

    #turn arr into hash;
    splice(@xml_arr,0,1);

    my %xml_hash_parsed;

    #start parsing authors and all their attributes
    ($xml_hash_parsed{$did}, $uncertain) = &parse_xml($did, $xml_hash{$did},$uncertain_addr);

    $xml_hash_parsed{$did}{raw} = $xml_hash{$did};
    delete($xml_hash{$did});

    if ($uncertain) {
	print STDERR "\n\n$did has mismatched address parsing \n";
	$uncertain_addr++;
    }

    my $handle = Data::Dumper->new([\%xml_hash_parsed]);
    $$rstr = $handle->Dump;

    my $rFinalStr = &output_xml(\%xml_hash_parsed);
    return $rFinalStr;
}



#cluster needs initialization --the first cluster!
sub parse_xml () {
    my $did = shift;
    my $str = shift;
    my $uncertain_addr = shift;
    my %xml_hash = ();

    my @lines = split(/\s*\n\s*/, $str);
    my $pre_stat = "";
    my %pre_email = ();
    #pre address/affiliation info.
    my $pre_cluster_id = "";
    my $pre_add_cluster_id = "";

    my $cluster_affi_exist = 0; # may not be useful
    my $cluster_addr_exist = 0; #may not be useful

    my $abstractComplete = 0;

    my $line_count = 0;
    for my $i(0 .. $#lines) {
	my $line = $lines[$i];
	$line = &string_clean($line);
	if ($line =~ /^\s*$/) {next;}
	if ($line =~ /\<(\w+)\>(.*)\<\/\w+\>/) {
	    my $tag = $1;
	    my $content = $2;
	    #$content = &lclean($content);
	    $line_count++;
	    $content = &string_clean($content);
	    if ($pre_stat ne $tag) { #different tags
		if ($tag =~ /abstract/) {
		    if (!defined $xml_hash{$tag}) {
			$xml_hash{$tag} .= "$content";
		    } else {
			$abstractComplete = 1;
		    }
		} elsif ($tag =~ /author/) {
		    $content =~ s/\s*(\,|\;)\s*/ /g;
		    $content = &string_clean($content);
		    $xml_hash{cluster_num}++;
		    my $cluster_id = $xml_hash{cluster_num};
		    $xml_hash{cluster}{$cluster_id}{start} = $line_count;
		    $xml_hash{cluster}{$cluster_id}{end} = $line_count;

                    #heuristically judge the correctness of name parsing, and clean names
		    my @multi_names = split(/\s+and\s+/i, $content);
		    for my $i(0 .. $#multi_names) {
			my $name = $multi_names[$i];
			my ($weireness, $clean_name) = &weired_author($name);
			if ($weireness) {next;}
			$xml_hash{cluster}{$cluster_id}{author_num}++;
			my $author_id = $xml_hash{cluster}{$cluster_id}{author_num};
			$xml_hash{cluster}{$cluster_id}{author}{$author_id}{name} = $clean_name;
		    }

		    #within cluster parameters update
		    $cluster_affi_exist = 0;
		    $cluster_addr_exist = 0;
		}elsif ($tag =~ /affiliation/) {
		    my $cluster_id = $xml_hash{cluster_num};
		    if ($cluster_id <0) {
			print STDERR "warning $did has affiliations ahead of authors \n";
		    }else{
			#start a new add_cluster regardless what different tags the previous line has
			$xml_hash{cluster}{$cluster_id}{add_cluster_num}++;
			my $add_cluster_id = $xml_hash{cluster}{$cluster_id}{add_cluster_num};
			$xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi_num}++;
			my $affi_id = $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi_num};
			$xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi}{$affi_id} = $content;
			#not good
			if ($pre_stat eq "email") {
			    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{pre_email} = %pre_email;
			}
		    }
		    $cluster_affi_exist =1;
		}elsif ($tag =~ /address/) {
		    my $cluster_id = $xml_hash{cluster_num};
		    if ($cluster_id <0) {
			#print "warning $did has affiliations ahead of authors \n";
		    }else{
			if ($pre_stat !~ /affiliation/) {
			    $xml_hash{cluster}{$cluster_id}{add_cluster_num}++;
			}
			my $add_cluster_id = $xml_hash{cluster}{$cluster_id}{add_cluster_num};
			$xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr_num}++;
			my $addr_id = $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr_num};
			$xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr}{$addr_id} = $content;
			#not good
			if ($pre_stat eq "email") {
			    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{pre_email} = %pre_email;
			}
		    }
		    $cluster_addr_exist =1;
		}elsif ($tag =~ /email/) { # other tags
		    if ($content =~ /\@/) {
			$parsed_emails = &parse_email($content);
			#concatenate these emails to the new one
			for my $i(0 .. $#$parsed_emails) {
			    $$parsed_emails[$i] = &string_clean($$parsed_emails[$i]);
			    $xml_hash{email_num}++;
			    my $email_id = $xml_hash{email_num};
			    $xml_hash{email}{$email_id} = $$parsed_emails[$i];
			    $pre_email{$$parsed_emails[$i]}++;
			    #if previous taf is affi/addr. point the pre-affi/addr down to email
			    if (($pre_stat eq "affiliation") || ($pre_stat eq "address")) {
				$xml_hash{cluster}{$pre_cluster_id}{add_cluster}{$pre_add_cluster_id}{next_email}{$$parsed_emails[$i]} = 1;
			    }
			}
		    }
		}else {
		    $xml_hash{$tag}= $content;
		}
	    }else { #same tags with the previous line
		my $cluster_id = $xml_hash{cluster_num};
		if ($tag =~ /author/) {
		    $content =~ s/\s*(\,|\;)\s*/ /g;
		    $content = &string_clean($content);
		    if  ($xml_hash{cluster}{$cluster_id}{end}  != ($line_count-1)) {
			die "SVMHeaderParse: $did cluster assignment inappropriate";
		    }else {
			$xml_hash{cluster}{$cluster_id}{end} = $line_count;
			#heuristically judge the correctness of name parsing, and clean names
			my @multi_names = split(/\s+and\s+/i, $content);
			for my $i(0 .. $#multi_names) {
			    my $name = $multi_names[$i];
			    my ($weireness, $clean_name) = &weired_author($name);
			    if ($weireness) {next;}
			    $xml_hash{cluster}{$cluster_id}{author_num}++;
			    my $author_id = $xml_hash{cluster}{$cluster_id}{author_num};
			    $xml_hash{cluster}{$cluster_id}{author}{$author_id}{name} = $clean_name;
			}
		    }
		}elsif ($tag =~ /affiliation/) {
		    my $add_cluster_id = $xml_hash{cluster}{$cluster_id}{add_cluster_num};
		    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi_num}++;
		    my $affi_id = $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi_num};
		    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{affi}{$affi_id} = $content;
		    $cluster_affi_exist =1;
		}elsif ($tag =~ /address/) {
		    my $add_cluster_id = $xml_hash{cluster}{$cluster_id}{add_cluster_num};
		    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr_num}++;
		    my $addr_id = $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr_num};
		    $xml_hash{cluster}{$cluster_id}{add_cluster}{$add_cluster_id}{addr}{$addr_id} = $content;
		    $cluster_addr_exist =1;
		}elsif ($tag =~ /email/) {# other tags
		    if ($content =~ /\@/) {
			$parsed_emails = &parse_email($content);
			#concatenate these emails to the new one
			for my $i(0 .. $#$parsed_emails) {
			    $$parsed_emails[$i] = &string_clean($$parsed_emails[$i]);
			    $xml_hash{email_num}++;
			    my $email_id = $xml_hash{email_num};
			    $xml_hash{email}{$email_id} = $$parsed_emails[$i];
			    $pre_email{$$parsed_emails[$i]}++;
			}
		    }
		} elsif ($tag =~ /abstract/) {
		    if ($abstractComplete <= 0) {
			$xml_hash{$tag} .= "\n$content";
		    }
		}else {
		    $xml_hash{$tag} .= " $content";
		}
	    }
	    #parameters update
	    if (($tag eq "affiliation") || ($tag eq "address")) {
		$pre_cluster_id = $xml_hash{cluster_num};
		$pre_add_cluster_id = $xml_hash{cluster}{$pre_cluster_id}{add_cluster_num};
		%pre_email = ();
	    }elsif ($tag ne "email") {
		%pre_email = ();
		$pre_cluster_id = "";
		$pre_add_cluster_id = "";
	    }
	    $pre_stat = $tag;
	}
    }

    $xml_hash{'abstractEnded'} = $abstractComplete;

    #the order of adjusting email and address is non changable
    $xml_hash =  &adjust_email(\%xml_hash);
    ($xml_hash, $uncertain) =  &adjust_addr($xml_hash, $uncertain_addr);

    return($xml_hash,$uncertain);
}


sub parse_email(){
    my $content = shift;

    #parse email; email could be separated by only author names, or the whole email addresses
    my @all_emails = ();
    $content =~ s/(email|e-mail|e mail)(s)*(\s*\:\s*)*//gi;
    my @email_parts = split(/\@/, $content);
    if ($#email_parts < 2) { #only one @
	if ($content =~ /\,|;|\{|\}|\[|\]/) { #multiple people with the same email root
	    $content =~ s/\{|\}|\[|\]//g;
	    my ($pre, $last) = split(/\@/, $content);
	    my @authors = split(/\,/, $pre);
	    for my $k(0 .. $#authors) {
		my $tmp_email = "$authors[$k]"."\@"."$last";
		$tmp_email =~ s/^\s+//g;
		$tmp_email =~ s/\s+$//g;
		push @all_emails, $tmp_email;
	    }
	}else {
	    push @all_emails, $content;
	}
    }else { # do not consider the case that some @ string has 1+ people (if exists; extend the function)
	my @emails = ();
	if ($content =~ /\,|\;/) {
	    @emails = split(/\,|\;/, $content);
	}else {
	    @emails = split(/\s+/, $content);
	}

	for my $k(0 .. $#emails) {
	    push @all_emails, $emails[$k];
	}
    }
    for (my $i=0; $i<=$#all_emails; $i++) {
	my $email = $all_emails[$i];
	$email =~ s/^[\(\[\s\<]+//;
	$email =~ s/[\]\)\s\>]+$//;
	$all_emails[$i] = $email;
    }
    return(\@all_emails);
}

sub adjust_email () {
    my $adjust_hash= shift;

    my %author_email = ();
    my %email_author = ();
    foreach my $cluster_id (sort {$a<=>$b} keys %{$$adjust_hash{cluster}}) {
	foreach my $author_id (sort {$a<=>$b} keys %{$$adjust_hash{cluster}{$cluster_id}{author}}) {
	    my $name = $$adjust_hash{cluster}{$cluster_id}{author}{$author_id}{name};
	    my $name_id = "$cluster_id"."_"."$author_id";
	    if ($name !~ /\w/) {next;}
	    foreach my $email_id(sort {$a<=>$b} keys %{$$adjust_hash{email}}) {
		my $email = $$adjust_hash{email}{$email_id};
		my ($email_name, $email_root) = split(/\@/,$email);
		if ($email_name !~ /\w/) {next;}
		$dist = adist(lc($email_name), lc($name));
		$author_email{$name_id}{$email} = abs($dist);
		$email_author{$email}{$name_id} = abs($dist);
	    }
	}
    }

    foreach my $author_id (keys %author_email) {
	my $counter = keys %{$author_email{$author_id}};
	foreach my $email_id (sort {$author_email{$author_id}{$b}<=>$author_email{$author_id}{$a}} keys %{$author_email{$author_id}}) {
	    $author_email{$author_id}{$email_id}{rank} = $counter;
	    $author_email{$author_id}{$email_id}{score} =  $author_email{$author_id}{$email_id};
	    $counter--;
	}
    }

    foreach my $email_id (keys %email_author) {
	my $counter = keys %{$email_author{$email_id}};
	foreach my $author_id (sort {$email_author{$email_id}{$b}<=>$email_author{$email_id}{$a}} keys %{$email_author{$email_id}}) {
	    $email_author{$email_id}{$author_id}{rank} = $counter;
	    $email_author{$email_id}{$author_id}{score} = $email_author{$email_id}{$author_id};
	    $counter--;
	}
    }

    my %picked_author;
    my %picked_email;
    my %final_map = ();
    my %sum_hash = (); #rank

    #add the ranks from two sides -> all combinations
    foreach my $author_id (sort {$a<=>$b} keys %author_email) {
	foreach my $email_id (keys %{$author_email{$author_id}}) {
	    my $macro = "$author_id"."<>"."$email_id";
	    my ($cluster_id, $author_id2) = split(/\_/, $author_id);
	    #my $macro = "$$adjust_hash{cluster}{$cluster_id}{author}{$author_id2}"."<>"."$email_id";;
	    $sum_hash{$macro}{score} = $author_email{$author_id}{$email_id}{score} + $email_author{$email_id}{$author_id}{score};
	}
    }

    foreach my $macro (sort {$sum_hash{$a}{score} <=> $sum_hash{$b}{score}} keys %sum_hash) {
	my ($author_id1, $email_id) = split(/<>/,$macro);
	if ($picked_author{$author_id1} || $picked_email{$email_id}) {next;}
	$picked_author{$author_id1} = 1;
	$picked_email{$email_id} = 1;
	$final_map{$author_id1} = $email_id;
	my ($cluster_id, $author_id2) = split(/\_/, $author_id1);
	$$adjust_hash{cluster}{$cluster_id}{author}{$author_id2}{email}=$email_id;
    }

    return($adjust_hash);
}

sub adjust_addr() {
    my $H= shift;

    #address/affiliation assignment is unreasonable
    my $uncertain = 0;

    foreach my $cluster_id (sort {$a<=>$b} keys %{$$H{cluster}}) {
	if ($$H{cluster}{$cluster_id}{author_num} eq 1) {
	    if ($$H{cluster}{$cluster_id}{add_cluster_num} > 1) {
		#for one author - multiple address case; combine all
		my ($affi, $addr) = &combine_all_addr_set($$H{cluster}{$cluster_id});
		if ($affi ne "") {
		    $$H{cluster}{$cluster_id}{author}{1}{affi}=$affi;
		}
		if ($addr ne "") {
		    $$H{cluster}{$cluster_id}{author}{1}{addr}=$addr;
		}
	    }elsif ($$H{cluster}{$cluster_id}{add_cluster_num} eq 1) {
		my ($affi, $addr) = &combine_first_addr_set($$H{cluster}{$cluster_id});
		#print "affi is $affi and addr is $addr \n";
		if ($affi ne "") {
		    $$H{cluster}{$cluster_id}{author}{1}{affi}=$affi;
		}
		if ($addr ne "") {
		    $$H{cluster}{$cluster_id}{author}{1}{addr}=$addr;
		}
	    }else {
		#print "warning: No address and affiliations\n";
	    }
	}elsif ($$H{cluster}{$cluster_id}{author_num} > 1) {
	    if ($$H{cluster}{$cluster_id}{add_cluster_num} > 1) {
		if ($$H{cluster}{$cluster_id}{add_cluster_num} eq $$H{cluster}{$cluster_id}{author_num}) {
		    my $addr_cluster = &combine_addr_set($$H{cluster}{$cluster_id});
		    #equally assign
		    for my $author_id (1 .. $$H{cluster}{$cluster_id}{author_num}) {
			$$H{cluster}{$cluster_id}{author}{$author_id}{affi}=$$addr_cluster{$cluster_id}{affi};
			$$H{cluster}{$cluster_id}{author}{$author_id}{addr}=$$addr_cluster{$cluster_id}{addr};
		    }
		}else {
		    #check if existing emails as separators.
		    my $consonence = &check_email_as_address_separator($$H{cluster}{$cluster_id}{add_cluster});
		    #emails ahead of each address cluster is the separator for author's address
		    if (($consonence eq "pre") || ($consonence eq "next")) {
			$$H{cluster}{$cluster_id} =  &adjust_cluster_by_email_separator($consonence, $$H{cluster}{$cluster_id});
		    }else {
			$uncertain =1;
		    }
		    #another strategies:
		    #first name has first address; last name has last address
		    $$H{cluster}{$cluster_id} = &assign_edge_address($$H{cluster}{$cluster_id});
		    #other stategies???

		}
	    }elsif ($$H{cluster}{$cluster_id}{add_cluster_num} eq 1) {
		my ($affi, $addr) = &combine_first_addr_set($$H{cluster}{$cluster_id});
		#print "affi is $affi and addr is $addr \n";
		for my $author_id (1 .. $$H{cluster}{$cluster_id}{author_num}) {
		    $$H{cluster}{$cluster_id}{author}{$author_id}{affi}=$affi;
		    $$H{cluster}{$cluster_id}{author}{$author_id}{addr}=$addr;
		}
	    }else {
		#print "warning: No address and affiliations\n";
	    }
	}
    }

    return($H, $uncertain);
}

sub combine_first_addr_set() {
    my $H = shift;

    my $affi = "";
    my $addr = "";

    foreach my $affi_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{1}{affi}}) {
	$affi .= "\; $$H{'add_cluster'}{1}{affi}{$affi_id}";
    }
    $affi =~ s/^\s*\;\s*//g;
    foreach my $addr_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{1}{addr}}) {
	$addr .= "\; $$H{'add_cluster'}{1}{addr}{$addr_id}";
    }
    $addr =~ s/^\s*\;\s*//g;
    delete($$H{'add_cluster'}{1}{affi});
    delete($$H{'add_cluster'}{1}{addr});

    return($affi,$addr);
}

sub combine_addr_set() {
    my $H = shift;

    my %add_cluster = ();

    foreach my $cluster_id (sort {$a <=> $b} keys %{$$H{'add_cluster'}}) {
	my $affi = "";
	my $addr = "";
	foreach my $affi_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{$cluster_id}{affi}}) {
	    $affi .= "\; $$H{'add_cluster'}{$cluster_id}{affi}{$affi_id}";
	}
	$affi =~ s/^\s*\;\s*//g;
	foreach my $addr_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{$cluster_id}{addr}}) {
	    $addr .= "\; $$H{'add_cluster'}{$cluster_id}{addr}{$addr_id}";
	}
	$addr =~ s/^\s*\;\s*//g;
	if ($affi ne "") {
	    $add_cluster{$cluster_id}{affi} = $affi;
	}

	if ($addr ne "") {
	    $add_cluster{$cluster_id}{addr} = $addr;
	}

	delete($$H{'add_cluster'}{$cluster_id}{affi});
	delete($$H{'add_cluster'}{$cluster_id}{addr});
    }

    return(\%add_cluster);
}


sub combine_all_addr_set() {
    my $H = shift;

    my $affi = "";
    my $addr = "";
    my %add_cluster = ();

    foreach my $cluster_id (sort {$a <=> $b} keys %{$$H{'add_cluster'}}) {
	foreach my $affi_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{$cluster_id}{affi}}) {
	    $affi .= "\; $$H{'add_cluster'}{$cluster_id}{affi}{$affi_id}";
	}
	foreach my $addr_id(sort {$a <=> $b} keys %{$$H{'add_cluster'}{$cluster_id}{addr}}) {
	    $addr .= "\; $$H{'add_cluster'}{$cluster_id}{addr}{$addr_id}";
	}
	delete($$H{'add_cluster'}{$cluster_id}{affi});
	delete($$H{'add_cluster'}{$cluster_id}{addr});
    }

    return($affi,$addr);
}

sub combine_affi() {
    my $H = shift;

    my $affi = "";
    foreach my $affi_id (sort {$a <=> $b} keys %{$$H{affi}}) {
	$affi .= "\; $$H{affi}{$affi_id}";
    }
    $affi =~ s/^\s*\;\s*//g;
    return($affi);
}

sub combine_addr() {
    my $H = shift;

    my $addr = "";
    foreach my $addr_id (sort {$a <=> $b} keys %{$$H{addr}}) {
	$addr .= "\; $$H{addr}{$addr_id}";
    }
    $addr =~ s/^\s*\;\s*//g;

    return($addr);
}

sub check_email_as_address_separator(){
    my $H = shift;

    my $consonence = 0; #0 is null state; -1 is conflict
    foreach my $add_cluster_id (sort {$a<=>$b} keys %{$H}) {
	if ($consonence eq "-1") {last;}
	my $pre_email = $$H{$add_cluster_id}{'pre_email'};
	my $next_email = $$H{$add_cluster_id}{'next_email'};
	###################here
	foreach my $email_key (sort %{$pre_email}) {
	    if ($email_key =~ /\w/) {
		if (($consonence eq "0") || ($consonence eq "pre")) {
		    $consonence = "pre";
		}elsif ($consonence ne "next") {
		    $consonence = -1;
		}
		last;
	    }
	}

	foreach my $email_key (sort %{$next_email}) {
	    if ($email_key =~ /\w/) {
		if (($consonence eq "0") || ($consonence eq "next")) {
		    $consonence = "next";
		}elsif ($consonence ne "pre") {
		    $consonence = -1;
		}
		last;
	    }
	}
    } #end of checking emails as the address separators

    return($consonence);
}



sub adjust_cluster_by_email_separator() {
    my $consonence =shift;
    #$$H{cluster}{$cluster_id})
    my $H = shift;

    if ($consonence eq "pre") {
	foreach my $add_cluster_id (sort {$a<=>$b} keys %{$$H{add_cluster}}) {
	    my $pre_email_hash = $$H{add_cluster}{$add_cluster_id}{'pre_email'};
	    #check each email and assign to respective author
	    foreach my $pre_email (keys %{$pre_email_hash}) {
		foreach my $author_id (keys %{$$H{author}}) {
		    if ($pre_email eq $$H{author}{$author_id}{email}) {
			#need to combine them first.
			my $combine_affi = &combine_affi($$H{add_cluster}{$add_cluster_id});
			my $combine_addr = &combine_addr($$H{add_cluster}{$add_cluster_id});
			$$H{author}{$author_id}{affi} = $combine_affi;
			$$H{author}{$author_id}{addr} = $combine_addr;
		    }
		}
	    }
	}
    }elsif ($consonence eq "next") {
	foreach my $add_cluster_id (sort {$a<=>$b} keys %{$$H{add_cluster}}) {
	    my $next_email_hash = $$H{add_cluster}{$add_cluster_id}{'next_email'};
	    #check each email and assign to respective author
	    foreach my $next_email (keys %{$next_email_hash}) {
		foreach my $author_id (keys %{$$H{author}}) {
		    if ($next_email eq $$H{author}{$author_id}{email}) {
			#need to combine them first.
			my $combine_affi = &combine_affi($$H{add_cluster}{$add_cluster_id});
			my $combine_addr = &combine_addr($$H{add_cluster}{$add_cluster_id});
			$$H{author}{$author_id}{affi} = $combine_affi;
			$$H{author}{$author_id}{addr} = $combine_addr;
		    }
		}
	    }
	}
    }

    return($H);
}


#assign first name first address and last name last address
sub assign_edge_address () {
    my $H = shift;

    my $first_name_addr = $$H{author}{1}{addr};
    my $first_name_affi = $$H{author}{1}{affi};
    my $last_name_addr = $$H{author}{$$H{author_num}}{addr};
    my $last_name_affi = $$H{author}{$$H{author_num}}{affi};

    #needs to combine add and affi
    if (($first_name_addr eq "" ) && ($first_name_affi eq "")) {
	my $combine_affi = &combine_affi($$H{add_cluster}{1});
	my $combine_addr = &combine_addr($$H{add_cluster}{1});

	$$H{author}{1}{affi} = $combine_affi;
	$$H{author}{1}{addr} = $combine_addr;
    }

    if (($last_name_addr eq "" ) && ($last_name_affi eq "")) {
	my $combine_affi = &combine_affi($$H{add_cluster}{$$H{add_cluster_num}});
	my $combine_addr = &combine_addr($$H{add_cluster}{$$H{add_cluster_num}});
	$$H{author}{$$H{author_num}}{affi} = $combine_affi;
	$$H{author}{$$H{author_num}}{addr} = $combine_addr;
    }

    return($H);
}

sub output_xml(){
    my $parsed_hash = shift;
    my $author_found = 0;

    my $l_algName = $algName;
    my $l_algVersion = $algVersion;
    cleanXML(\$l_algName);
    cleanXML(\$l_algVersion);

    my $str = "<algorithm name=\"$l_algName\" version=\"$l_algVersion\">\n";

    foreach my $did (sort {$a <=> $b} keys %{$parsed_hash}) {
	my $title = $$parsed_hash{$did}{title};
	$title = repairPunctuation($title);
	cleanAll(\$title);

	$str.="<title>$title</title>\n";
	$str.="<authors>\n";
	foreach my $cluster_id (sort {$a <=> $b} keys %{$$parsed_hash{$did}{cluster}}) {
	    if ($cluster_id =~ /\d+/) {
		foreach my $author_id ( sort {$a <=> $b} keys %{$$parsed_hash{$did}{cluster}{$cluster_id}{author}}) {
		    $author_found = 1;

		    my $name = $$parsed_hash{$did}{cluster}{$cluster_id}{author}{$author_id}{name};
		    cleanAll(\$name);
		    $name = normalizeName($name);
		    my $affi = $$parsed_hash{$did}{cluster}{$cluster_id}{author}{$author_id}{affi};
		    $affi = repairPunctuation($affi);
		    cleanAll(\$affi);

		    my $addr = $$parsed_hash{$did}{cluster}{$cluster_id}{author}{$author_id}{addr};
		    $addr = repairPunctuation($addr);
		    cleanAll(\$addr);

		    my $email = $$parsed_hash{$did}{cluster}{$cluster_id}{author}{$author_id}{email};
		    cleanAll(\$email);

		    if ($name =~ /\w/) {
			$str.="<author>\n";
			$str.="<name>$name</name>\n";
			if ($affi =~ /\w/) {
			    $str.="<affiliation>$affi</affiliation>\n";
			}
			if ($addr =~ /\w/) {
			    $str.="<address>$addr</address>\n";
			}
			if ($email =~ /\w/) {
			    $str.="<email>$email</email>\n";
			}
			$str.="</author>\n";
		    }
		}
	    }
	}
	$str.="</authors>\n";

	my $keywords = $$parsed_hash{$did}{keyword};
	if ($keywords =~ /\w/) {
	    $keywords = repairPunctuation($keywords);
	    my @keywords = normalizeKeywords($keywords);
	    $str .= "<keywords>\n";
	    foreach my $keyword (@keywords) {
		cleanAll(\$keyword);
		$str .= "<keyword>$keyword</keyword>\n";
	    }
	    $str .= "</keywords>\n";
	}

	my $abstract = $$parsed_hash{$did}{abstract};
	if ($abstract =~ /\w/) {
	    $abstract = repairPunctuation($abstract);
	    $abstract = normalizeAbstract($abstract, $$parsed_hash{$did}{abstractEnded});
	    cleanAll(\$abstract);
	    $str .= "<abstract>$abstract</abstract>\n";
	}

	my $date = $$parsed_hash{$did}{date};
	if ($date =~ /\d/) {
	    $date = repairPunctuation($date);
	    cleanAll(\$date);
	    $date = normalizeDate($date);
	    if (defined $date) {
		$str .= "<date>$date</date>\n";
	    }
	}
    }

    my $titlelength = length($$parsed_hash{$did}{title});
    my $authorcount = scalar keys %{$$parsed_hash{$did}{cluster}{$cluster_id}{author}};

    my $validHeader;

    if(length($$parsed_hash{$did}{title}) > 0 && $author_found) {
	$validHeader = "<validHeader>1</validHeader>";
    }
    else {
	$validHeader = "<validHeader>0</validHeader>";
    }

    $str.="$validHeader\n";
    $str.="</algorithm>\n";

    return \$str;
}


sub normalizeName {
    my $name = shift;
    my @tokens = split " ", $name;
    my @newTokens = ();
    foreach my $token (@tokens) {
	if ($token =~ m/^and$/i) {
	    next;
	}
	push @newTokens, $token;
    }
    return join " ", @newTokens;
}


sub normalizeKeywords {
    my $text = shift;
    my @tokens = split '\s*[\:\;\,]\s*', $text;
    for (my $i=0; $i<=$#tokens; $i++) {
	$tokens[$i] = trimPunctuation($tokens[$i]);
    }
    if ($tokens[0] =~ m/keyword|keyphrase/i) {
	return @tokens[1..$#tokens];
    }
    return @tokens;
}

sub normalizeAbstract {
    my ($text, $abstractEnded) = @_;

    my @lines = split '\n', $text;
    if ($#lines < 0) {
	return "";
    }

    if ($abstractEnded<=0) {
	my $minLines = 5;
	my $maxLines = 15;
	my $lineCount = 0;
	for (my $i=0; $i<$#lines; $i++) {
	    $lineCount++;
	    if (($lineCount >= $minLines) && $line =~ m/\.\s*$/) {
		last;
	    }
	    if ($lineCount >= $maxLines) {
		last;
	    }
	}
	@lines = @lines[0..($lineCount-1)];
    }

    my $abstract = "";
    foreach my $line (@lines) {
	if ($line =~ m/\b(?:Abstract|ABSTRACT|abstract|Introduction|INTRODUCTION)\:?\s*$/ ||
	    $line =~ m/^\s*$/) {
	    next;
	}
	if ($abstract =~ m/\-$/ || $abstract =~ m/^\s*$/s) {
	    $abstract .= $line;
	} else {
	    $abstract .= " $line";
	}
    }
    return $abstract;
}

sub normalizeDate {
    my $date = shift;
    if ($date =~ m/(\b\d{4}\b)/) {
	my $year = $1;
	my @timeData = localtime(time);
	my $currentYear = $timeData[5]+1900;
	if ($year <= $currentYear+3) {
	    return $year;
	}
    }
    return undef;
}

sub trimPunctuation {
    my $text = shift;
    $text =~ s/[\.\,\<\>\?\/\:\;\"\'\{\[\}\]\+\=\_\-\(\)\*\&\^\%\$\#\@\!\~\`\\\|]+\s*$//;
    $text =~ s/^\s*[\.\,\<\>\?\/\:\;\"\'\{\[\}\]\+\=\_\-\(\)\*\&\^\%\$\#\@\!\~\`\\\|]+//;
    return $text;
}


sub string_clean() {
    my $str = shift;
    $str =~ s/^\s+//g;
    $str =~ s/\s+$//g;

    return($str);
}


sub new
{
    my $classname = shift;

    my $self = { XMLindent => '   ' };

    my @upperentities = qw (nbsp iexcl cent pound curren yen brvbar sect
			    uml copy ordf laquo not 173 reg macr deg plusmn
			    sup2 sup3 acute micro para middot cedil supl
			    ordm raquo frac14 half frac34 iquest Agrave
			    Aacute Acirc Atilde Auml Aring AElig Ccedil
			    Egrave Eacute Ecirc Euml Igrave Iacute Icirc
			    Iuml ETH Ntilde Ograve Oacute Ocirc Otilde Ouml
			    times Oslash Ugrave Uacute Ucirc Uuml Yacute
			    THORN szlig agrave aacute acirc atilde auml
			    aring aelig ccedil egrave eacute ecirc euml
			    igrave iacute icirc iuml eth ntilde ograve
			    oacute ocirc otilde ouml divide oslash ugrave
			    uacute ucirc uuml yacute thorn yuml);
    $upperentities[12] = '#173';

    $self->{'hashentity'} = {};
    for ( my $i=0; $i<=$#upperentities; $i++ )
    {
	my $key = '&'.$upperentities[$i].';';
	$self->{'hashentity'}->{$key}=$i+160;
    }

    $self->{'hashstr'} = (join (';|', @upperentities)).';';

    bless $self, $classname;
    return $self;
}

sub char_converter()
{

    my $H = { XMLindent => '   ' };

    my @upperentities = qw (nbsp iexcl cent pound curren yen brvbar sect
			    uml copy ordf laquo not 173 reg macr deg plusmn
			    sup2 sup3 acute micro para middot cedil supl
			    ordm raquo frac14 half frac34 iquest Agrave
			    Aacute Acirc Atilde Auml Aring AElig Ccedil
			    Egrave Eacute Ecirc Euml Igrave Iacute Icirc
			    Iuml ETH Ntilde Ograve Oacute Ocirc Otilde Ouml
			    times Oslash Ugrave Uacute Ucirc Uuml Yacute
			    THORN szlig agrave aacute acirc atilde auml
			    aring aelig ccedil egrave eacute ecirc euml
			    igrave iacute icirc iuml eth ntilde ograve
			    oacute ocirc otilde ouml divide oslash ugrave
			    uacute ucirc uuml yacute thorn yuml);
    $upperentities[12] = '#173';

    $H->{'hashentity'} = {};
    for ( my $i=0; $i<=$#upperentities; $i++ )
    {
	my $key = '&'.$upperentities[$i].';';
	$H->{'hashentity'}->{$key}=$i+160;
    }

    $H->{'hashstr'} = (join (';|', @upperentities)).';';

    return $H;
}

sub repairPunctuation {
    my $text = shift;
    $text =~ s/  / /gs;
    $text =~ s/\s([\.\,\;\]\)\:\}\!\?\>\-])/$1/gs;
    $text =~ s/^\s+//;
    $text =~ s/\s+$//;
    return $text;
}

# clean XML version two - for single-line streams
sub lclean
{
    my $t = shift;
    return undef if (! defined $t );

    $H = &char_converter;
    # make ISOlat1 entities into Unicode character entities
    $t =~ s/&($H->{'hashstr'})/sprintf ("&#x%04X;", $H->{'hashentity'}->{$&})/geo;
    # escape non-XML-encoded ampersands (including from other characters sets)
    $t =~ s/&(?!((\#[0-9]*)|(\#x[0-9]*)|(amp)|(lt)|(gt)|(apos)|(quot));)/&amp;/go;
    # convert extended ascii into Unicode character entities
    $t =~ s/[\xa0-\xff]/'&#'.ord ($&).';'/geo;
    # remove extended ascii that doesnt translate into ISO8859/1
    $t =~ s/[\x00-\x08\x0B\x0C\x0E-\x1f\x80-\x9f]//go;
    # make tags delimiters into entities
    $t =~ s/</&lt;/go;
    $t =~ s/>/&gt;/go;
    # flatten whitespace
    $t =~ s/[\s\t\r\n]+/ /go;
    # kill leading and terminating spaces
    $t =~ s/^[ ]+(.+)[ ]+$/$1/;
    return $t;
}

1;
