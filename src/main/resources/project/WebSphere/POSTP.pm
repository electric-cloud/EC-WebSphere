# this module provides custom postp logic.
package EC::POSTP;
use strict;
use warnings;
use XML::Simple;
use ElectricCommander;
use Data::Dumper;

our $VERSION = 0.1;

our $PATTERNS = [
    {
        start => qr/^\[(INFO|ERROR|WARNING|DEBUG|TRACE)\]/,
        cut => qr/^\[(INFO|ERROR|WARNING|DEBUG|TRACE)\]\s+/,
        end => qr/^\s*$/,
    },
    {
        start => qr/^\[OUT\]\[(INFO|ERROR|WARNING|DEBUG|TRACE)\]:/,
        cut => qr/^\[OUT\]\[(INFO|ERROR|WARNING|DEBUG|TRACE)\]:\s+/,
        end => qr/\s+:\[(INFO|ERROR|WARNING|DEBUG|TRACE)\]\[OUT\]$/,
    }
];
# Postp module should have run method, which will be invoked.
sub run {
    my ($class, $argv) = @_;
    print Dumper $argv;
    my $file_name = get_file_name();
    my $ec = ElectricCommander->new();
    $ec->setProperty("diagFile", $file_name);
    print "File name: $file_name\n";
    my $current_line = 1;
    my $starting_line = 0;
    my $counter = 0;
    my $d = DiagReport->new();
    print "Entering log loop\n";
    my $buf = '';
    my $matcher = '';
    my $state = 'seek';
    my $terminator = undef;
    my $cutter = undef;
    while (<>) {
        my $line = $_;
        print "$current_line: '$line'\n";
        # we're looking for our patterns.
        # Once pattern is found, SM is swithching to accumulate mode.
        if ($state eq 'seek') {
            ($terminator, $cutter, $matcher) = try_to_switch_mode($line);
            if ($terminator && $cutter) {
                print "Switching to accumulate\n";
                $starting_line = $current_line;
                $state = 'accumulate';
                $line =~ s/$cutter//s;
                $buf = '';
                # $counter++;
            }
        }
        # Here we're accumulating logs until terminator.
        # If terminator sequence is detected, switching to flush mode.
        if ($state eq 'accumulate') {
            if ($line =~ m/$terminator/s) {
                $line =~ s/$terminator//s;
                print "Switching to flush\n";
                if ($line) {
                    $buf .= $line;
                }
                $state = 'flush';
            }
            else {
                $buf .= $line;
            }
            $counter++;
        }
        # This mode appends event and switches to seek.
        if ($state eq 'flush') {
            $d->add_row({
                type      => $matcher,
                matcher   => $matcher,
                firstLine => $starting_line,
                numLines  => $counter,
                message   => $buf,
            });

            open (my $fh, '>', $file_name);
            print $fh $d->render();
            close $fh;
            $counter = 0;
            $state = 'seek';
        }
        $current_line++;
    }
    print Dumper $d;
    print "Done with loop, rendering\n";
    open (my $fh, '>', $file_name);
    print $fh $d->render();
    close $fh;
}


sub try_to_switch_mode {
    my ($line) = @_;

    print "Analyzing line: $line\n";
    my $terminator = undef;
    my $cutter = undef;
    my $matcher = undef;
    for my $pat (@$PATTERNS) {
        if ($line =~ m/$pat->{start}/s) {
            print "match\n";
            if ($1) {
                print "Matcher: $1\n";
                $matcher = lc($1);
            }
            $terminator = $pat->{end};
            $cutter = $pat->{cut};
            last;
        }
    }
    return ($terminator, $cutter, $matcher);
}


sub get_file_name {
    my $step_id = $ENV{COMMANDER_JOBSTEPID};
    return 'diag-' . $step_id . '.xml';
}


package DiagReport;
use strict;
use warnings;
use XML::Simple;
use Carp;

sub new {
    my ($class) = @_;

    my $self = {
        _data => {
            diagnostics => {
                diagnostic => []
            }
        }
    };
    bless $self, $class;
    return $self;
}

sub add_row {
    my ($self, $opts) = @_;

    my $diag_row = {
        type      => '',
        matcher   => '',
        name      => '',
        firstLine => '',
        numLines  => '',
        message   => ''
    };

    for my $attr (qw/type message firstLine/) {
        if (!exists $opts->{$attr}) {
            croak "$attr attribute is mandatory for postp\n";
        }
    }
    $diag_row->{type} = $opts->{type};
    $diag_row->{message} = $opts->{message};
    $diag_row->{firstLine} = $opts->{firstLine};

    $diag_row->{numLines} = $opts->{numLines};
    $diag_row->{matcher} = $opts->{matcher};
    if (!$opts->{matcher}) {
        $diag_row->{matcher} = $diag_row->{type};
    }

    if (!$opts->{numLines}) {
        $diag_row->{numLines} = 1;
    }
    if ($opts->{name}) {
        $diag_row->{name} = $opts->{name}
    }
    else {
        delete $diag_row->{name};
    }
    push @{$self->{_data}->{diagnostics}->{diagnostic}}, $diag_row;
    print Dumper $self;
    return $self;
}

sub render {
    my ($self) = @_;

    my $header = qq|<?xml version="1.0" encoding="UTF-8"?>\n|;

    my $xml = $header . XMLout($self->{_data}, KeepRoot => 1, NoAttr => 1, SuppressEmpty => 1);
    return $xml;
}

1;
