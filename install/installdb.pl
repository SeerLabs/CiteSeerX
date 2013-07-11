#!/usr/bin/perl
# CiteSeerX mysql database installation script.  This script requires
# that the mysql command line utility is on the user's path.  Please
# make sure that it is before trying to use this.
#
# IGC
use strict;

my $SQL_DIR = "../src/sql";
my %FILE_LOOKUP = (
    'CSX' => 'CSX_DATABASE.SQL',
    'CITEGRAPH' => 'CITEGRAPH_DATABASE.SQL',
    'MYCSX' => 'MYCSX_DATABASE.SQL',
    'DOI' => 'DOI_DATABASE.SQL',
    'EXTMETADATA' => 'CSX_EXTERNAL_METADATA.SQL'
);
my @DB_DEFS = qw(CSX CITEGRAPH MYCSX DOI EXTMETADATA);

my $user = "root";
my $pass;
my $host;
my $csxuser;
my $csxpass;
my $csxdomain;
my $siteid;
my $depid;
my @dbpref;

collectInformation();
while(!verify()) { collectInformation(); }
collectScripts();
runScripts();
cleanup();
print "Finished.\n";

sub collectInformation {

    while(1) {
        print "Which database definitions would you like to install?\n".
            "You can specify multiple options in a space-separated list.\n".
            "The options are ";
        print join ", ", @DB_DEFS;
        print ", or ALL: ";
        my $dbpref = <>;
        chomp($dbpref);
        $dbpref = uc($dbpref);
        @dbpref = split " ", $dbpref;

        my $okpref;
        foreach my $pref (@dbpref) {
            if (!defined $FILE_LOOKUP{$pref} && $pref ne 'ALL') {
                print "Invalid option: $pref\n\n";
                $okpref = undef;
                last;
            } else {
                if ($pref eq 'ALL') {
                    @dbpref = @DB_DEFS;
                }
                $okpref = 1;
            }
        }
        my %used;
        my @newpref;
        foreach my $pref (@dbpref) {
            if (!defined $used{$pref}) {
                push @newpref, $pref;
                $used{$pref}++;
            }
        }
        @dbpref = @newpref;
        if ($okpref) {
            last;
        }
    }
    
    print "\nPlease specify the host address and root password\n".
        "for the mysql database server you would like to use.\n\n";
    
    print "Host [default localhost]: ";
    $host = <>;
    chomp($host);
    if (!$host) { $host = 'localhost'; }
    
    while (!readPassword()) {
        print "Passwords did not match.  Please try again\n";
    }
    print "\n";

    print "Now, please enter the details of the citeseerx user that\n".
        "will access the databases you create.\n\n";

    print "CSX User: ";
    $csxuser = <>;
    chomp($csxuser);
    
    print "CSX Password: ";
    $csxpass = <>;
    chomp($csxpass);

    print "\nEnter the IP domain from which the user\n".
        "will access the database in the format expected by your\n".
        "database system (e.g., %.ist.psu.edu).\n\n";
    print "CSX Domain: ";
    $csxdomain = <>;
    chomp($csxdomain);

    foreach my $pref (@dbpref) {
        if ($pref eq 'DOI') {
            print "\nEnter configuration for your DOI server deployment.\n".
                "The Site ID and Deployment ID should both be integers.\n".
                "All DOI servers on your site should specify the same Site\n".
                "ID but no two servers within your site should have the\n".
                "same Deployment ID, or else duplicate IDs may be granted.\n".
                "Please note that the main CiteSeerX installation uses 10\n".
                "as its Site ID, so this value should not be used unless\n".
                "you are configuring the main site.\n\n";
            while(1) {
                print "Site ID: ";
                $siteid = <>;
                chomp($siteid);
                if ($siteid !~ m/^\d+$/) {
                    print "Invalid site id.\n\n";
                    next;
                }
                print "Deployment ID: ";
                $depid = <>;
                chomp($depid);
                if ($depid !~ m/^\d+$/) {
                    print "Invalid deployment id.\n\n";
                    next;
                }
                last;
            }
        }
    }

}


sub readPassword {
    print "Root password: ";
    system("stty -echo");
    my $pass1 = <>;
    chomp($pass1);
    system("stty echo");
    
    print "\n";

    print "Reenter password: ";
    system("stty -echo");
    my $pass2 = <>;
    chomp($pass2);
    system("stty echo");

    print "\n";

    if ($pass1 eq $pass2) {
        $pass = $pass1;
        return 1;
    } else {
        return undef;
    }
}


sub verify {
    print "\nYou have entered these values:\n";
    print "Databases: ", (join ", ", @dbpref), "\n";
    print "Database Host: $host\n";
    print "Root password: ";
    for (1 .. length($pass)) { print "*"; };
    print "\n";
    print "CSX User: $csxuser\n";
    print "CSX Pass: $csxpass\n";
    print "CSX Domain: $csxdomain\n";
    if ($siteid) {
        print "DOI Site ID: $siteid\n";
    }
    if ($depid) {
        print "DOI Deployment ID: $depid\n";
    }
    while(1) {
        print "\nDoes this look ok? [yes/no] ";
        my $resp = <>;
        chomp($resp);
        if (lc($resp) eq "yes") {
            return 1;
        } elsif (lc($resp) eq "no") {
            return undef;
        } else {
            print "Please enter \"yes\" or \"no\".\n";
        }
    }

}


sub collectScripts {
    foreach my $pref (@dbpref) {
        my $file = $FILE_LOOKUP{$pref};
        my $path = "$SQL_DIR/$file";
        open(IN, "<$path") or die $!;
        my $script;
        {
            local $/ = undef;
            $script = <IN>;
        }
        close IN;
        $script =~ s/\$USERNAME\$/$csxuser/g;
        $script =~ s/\$DOMAIN\$/$csxdomain/g;
        $script =~ s/\$PASSWORD\$/$csxpass/g;
        $script =~ s/\$SITEID\$/$siteid/g;
        $script =~ s/\$DEPID\$/$depid/g;
        open(OUT, ">$file") or die $!;
        print OUT $script;
        close OUT;
    }
}


sub runScripts {
    foreach my $pref (@dbpref) {
        my $sql = $FILE_LOOKUP{$pref};
        my $command = "mysql -h $host -u root -p$pass < $sql";
        my @command = split " ", $command;
        print "$command\n";
        system($command);
        if ($? == -1) {
            print "Failed to execute command ($command): $!\n";
            exit;
        } elsif ($? & 127) {
            print "Failed to execute command ($command): ".
                "mysql died with signal ".($? & 127)."\n";
            exit;
        }
    }
}


sub cleanup {
    foreach my $pref (@dbpref) {
        unlink($FILE_LOOKUP{$pref}) or die $!;
    }
}
