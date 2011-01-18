#!/vol/gnu/bin/env perl -w
use Bio::SeqIO;
use File::Basename;

use strict;

my $reference;
my $contigs;

if (@ARGV != 2) {
die "usage: $0 <contigs.fasta> <references.fasta> \n" .
	"\tMatches the contigs on the references using BLAST.\n" .
	"\t(BLAST as well as the perl module Bio::SeqIO have to be installed\n" .
	"\tand the command \'blastall\' has to be available.)\n" .
	"\tThe BLAST result is afterwards reformated such that it can be loaded within r2cat.\n" .
	"\thttp://bibiserv.techfak.uni-bielefeld.de/r2cat\n";
} else {
	$contigs=$ARGV[0];	# FASTA filename of the contigs file
	$reference=$ARGV[1];	# FASTA filename of the reference genome(s)
}

#Create an artificial filename to put the results in. consists of both filenames.
my $contigBasename=(fileparse($contigs,qr{\..*}))[0];
my $referenceBasename=(fileparse($reference,qr{\..*}))[0];
my $outputfilename = $contigBasename . "-" . $referenceBasename;


#Start the matching. First create a database for blast..
if (! -e "$reference.nsq") {
	print "Creating db for the reference in order to use BLAST:\n";
	print "  formatdb -p F -i $reference\n";
	system "formatdb -p F -i $reference";
} else {
	print "A database for the reference was already created.\nRemove \n$reference.nsq\n to create it in the next run.\n";
}
print "done\n";

# ..then run BLAST to find matches.
my $blastmatchfile="$outputfilename.blastmatches";
if (! -e "$blastmatchfile") {
	print "BLASTing contigs on reference db:\n";
	print "  blastall -p blastn -d $reference -i $contigs -o $blastmatchfile -e 1e-10 -m 8\n";
	system "blastall -p blastn -d $reference -i $contigs -o $blastmatchfile -e 1e-10 -m 8";
	print "done\n";
} else {
	print "The Matching was already performed.\nRemove \n$blastmatchfile\n to redo the matching in the next run.\n";
}


# Here, the blast matches are reformated such that they can be imported into r2cat
print "Reformating the blast results.\n";
# Store the lines to be written in the matches section.
my @outputMatches;
# Remember which contigs and reference sequences occur.
my %queries;
my %targets;
push @outputMatches, "BEGIN_HITS\n";
push @outputMatches, "#query_id	query_start	query_end	target_id	target_start	target_end\n";
open(BLAST,$blastmatchfile) or die $!;
while(my $line = <BLAST>) {
	chomp($line);
	my @tokens = split(/\t/,$line);
	my $query_id = $tokens[0];
	my $query_start = $tokens[6];
	my $query_end = $tokens[7];
	my $target_id = $tokens[1];
	my $target_start = $tokens[8];
	my $target_end = $tokens[9];
	$queries{$query_id} = 1; # mark that this id occurs
	$targets{$target_id} = 1;# mark that this id occurs
	
# Swap start and end for reverse complement matches.
# (We indicate a reverse complement match on the query side instead of the target side.)
	if($target_start>$target_end) {
		my $tmp = $query_start;
		$query_start = $query_end;
		$query_end = $tmp;
		
		$tmp = $target_start;
		$target_start = $target_end;
		$target_end = $tmp;
	}
	
	push @outputMatches, join("\t", $query_id,$query_start,$query_end,$target_id,$target_start,$target_end ) . "\n";
}
push @outputMatches, "END_HITS\n\n";
close(BLAST);



#Getting the sizes of contigs and references. This is necessary for the visualisation later in r2cat.
my @outputTargets;
my @outputQueries;
print "Collecting sequence sizes.\n";

my $contigseq=Bio::SeqIO->new(-file=>"$contigs",-format=>'fasta');
my $referenceseq=Bio::SeqIO->new(-file=>"$reference",-format=>'fasta');

#Collect reference sequences sizes
my $totalRefLen=0;
while (my $entry=$referenceseq->next_seq()){
	my $id = $entry->id;
	if(defined($targets{$id}) && $targets{$id}==1) {
		push @outputTargets, "BEGIN_TARGET $id\n";
		my $description=$entry->desc;
		if ( defined($description) && $description ne "") {
			push @outputTargets, " description=\"$description\"\n";
		}
		my $len=length($entry->seq);
		push @outputTargets, " size=$len\n";
		push @outputTargets, " offset=$totalRefLen\n";
		$totalRefLen+=$len;
		push @outputTargets, " file=$ARGV[0]\n";
		push @outputTargets, "END_TARGET\n\n";
 }
}

# Collect contigs sizes
my $totalConLen=0;
while (my $entry=$contigseq->next_seq()){
	my $id = $entry->id;
	if(defined($queries{$id}) && $queries{$id}==1) {
		push @outputQueries, "BEGIN_QUERY $id\n";
		my $description=$entry->desc;
		if ( defined($description) && $description ne "") {
			push @outputQueries, " description=\"$description\"\n";
		}
		my $len=length($entry->seq);
		push @outputQueries, " size=$len\n";
		push @outputQueries, " offset=$totalConLen\n";
		$totalConLen+=$len;
		push @outputQueries, " file=$ARGV[0]\n";
		push @outputQueries, "END_QUERY\n\n";
	}
}

print "Writing results to file.\n";
open(OUT, ">$outputfilename.r2c")or die $!;
print OUT "# r2cat save file\n#  (generated from a BLAST matches file)\n\n";
print OUT @outputTargets;
print OUT @outputQueries;
print OUT @outputMatches;
close(OUT);
print "You can now open the matches in r2cat by loading the file:\n";
print $outputfilename.".r2c\n";
