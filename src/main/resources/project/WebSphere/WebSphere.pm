package WebSphere::WebSphere;

=head1 NAME

C<WebSphere::WebSphere> - WebSphere configuration routines

=head1 DESCRIPTION

Wrapper for wsadmin utility.

=head1 COPYRIGHT

Copyright (c) 2016 Electric Cloud, Inc.
All rights reserved

=cut

use strict;
no strict "subs";
use ElectricCommander::PropDB;
use JSON;
use Data::Dumper;

=head1 METHODS

=head2 C<new>

Constructs a new L<WebSphere::WebSphere> object.

=over

2>&1

=item C<$ec>

Reference to L<ElectricCommander> object.

=item C<$configuration>

String with name of plugin configuration

=item C<$wsadminPath>

Absolute path to wsadmin utility

=back

Returns new instance of L<WebSphere::WebSphere> object.

=cut

sub new {
    my ( $class, $ec, $configurationName, $wsadminPath) = @_;

    my $self = {
        ec => $ec,
        # wsadminPath => $wsadminPath
    };
    bless $self, $class;
    my $configuration = $self->_getConfiguration($configurationName);

    $wsadminPath ||= $configuration->{wsadminabspath};
    unless ($wsadminPath) {
        $self->bail_out("Missing wsadmin absolute path");
    }
    if (!-e $wsadminPath || -d $wsadminPath) {
        $self->bail_out("wsadmin script does not exist or it is a directory");
    }
    $self->{wsadminPath} = $wsadminPath;
    if ( not $configuration ) {
        exit 1;
    }
    my $debug = $configuration->{debug};
    if (!defined $debug) {
        $debug = 0;
    }

    my $log = EC::Plugin::Logger->new($debug);
    $self->{_log} = $log;
    $self->{configuration} = $configuration;
    return $configuration ? $self : undef;
}

sub ec {
    my ($self) = @_;

    return $self->{ec};
}


sub log {
    my ($self) = @_;

    unless ($self->{_log}) {
        $self->bail_out("Logger is uninitialized");
    }
    return $self->{_log};
}


sub getRunContext {
    my ($self) = @_;

    my $ec = $self->ec();
    my $context = 'pipeline';
    my $flowRuntimeId = '';

    eval {
        $flowRuntimeId = $ec->getProperty('/myFlowRuntimeState/id')->findvalue('//value')->string_value;
    };
    return $context if $flowRuntimeId;

    eval {
        $flowRuntimeId = $ec->getProperty('/myFlowRuntime/id')->findvalue('/value')->string_value();
    };
    return $context if $flowRuntimeId;

    eval {
        $flowRuntimeId = $ec->getProperty('/myPipelineStageRuntime/id')->findvalue('/value')->string_value();
    };
    return $context if $flowRuntimeId;

    $context = 'schedule';
    my $scheduleName = '';
    eval {
        $scheduleName = $self->getScheduleName();
        1;
    } or do {
        print "error occured: $@\n";
    };

    if ($scheduleName) {
        return $context;
    }
    $context = 'procedure';
    return $context;
}

sub getProjectName {
    my ($self, $jobId) = @_;

    $jobId ||= $ENV{COMMANDER_JOBID};

    my $projectName = '';
    eval {
        my $result = $self->{ec}->getJobDetails($jobId);
        $projectName = $result->findvalue('//job/projectName')->string_value();
        # $projectName = $result->findvalue('//projectName')->string_value();
        1;
    } or do {
        print "error occured: $@\n";
    };

    return $projectName;
}

sub getScheduleName {
    my ($self, $jobId) = @_;

    $jobId ||= $ENV{COMMANDER_JOBID};

    my $scheduleName = '';
    eval {
        my $result = $self->{ec}->getJobDetails($jobId);
        $scheduleName = $result->findvalue('//scheduleName')->string_value();
        if ($scheduleName) {
            $self->{logger}->info('Schedule found: ', $scheduleName);
        };
        1;
    } or do {
        $self->{logger}->error($@);
    };

    return $scheduleName;
}

# this function is works like setSummary from other plugins, but it also sets props for pipelines.
# pipeline and procedure should be a hash reference or reference to array with hash references.
sub setResult {
    my ($self, %params) = @_;

    my ($outcome, $procedure, $pipeline) = ($params{outcome}, $params{procedure}, $params{pipeline});
    if (!ref $outcome) {
        $self->bail_out("HASH or ARRAY reference is expected as outcome parameter");
    }
    $outcome = [$outcome] if ref $outcome eq 'HASH';
    if (!ref $procedure) {
        $self->bail_out("HASH or ARRAY reference expected as procedure parameter");
    }
    $procedure = [$procedure] if ref $procedure eq 'HASH';
    if (!ref $pipeline) {
        $self->bail_out("pipeline parameter should be a HASH or ARRAY reference");
    }

    # if pipeline has been passed as hash reference, we will create array reference to iterate through it.
    $pipeline = [$pipeline] if ref $pipeline eq 'HASH';

    my $ec = $self->ec();

    # time to get context.
    my $context = $self->getRunContext();
    # setting outcome
    for my $o (@$outcome) {
        if ($o->{result} !~ m/^(?:error|success|warning)$/s) {
            $self->bail_out("Expected error, success or warning, got: $o->{result}");
        }
        if ($o->{target} !~ m/^(?:myJobStep|myJob|myCall)$/s) {
            $self->bail_out("target should one of: myJobStep, myJob, myCall. Got: $o->{target}");
        }
        $ec->setProperty('/' . $o->{target} . '/outcome' => $o->{result});
    }
    # pipeline
    if ($context eq 'pipeline') {
        for my $p (@$pipeline) {
            if (!$p->{target} || !exists $p->{msg}) {
                $self->bail_out("target and msg are mandatory");
            }
            $ec->setProperty('/myPipelineStageRuntime/ec_summary/' . $p->{target} =>  $p->{msg});
        }
    }
    # procedure, schedule and application process are also procedure context.
    else {
        for my $p (@$procedure) {
            if (!$p->{target} || !exists $p->{msg}) {
                $self->bail_out("target and msg are mandatory");
            }
            if ($p->{target} !~ m/^(?:myJobStep|myJob|myCall)$/s) {
                $self->bail_out("target should one of: myJobStep, myJob, myCall. Got: $p->{target}");
            }
            $ec->setProperty('/' . $p->{target} . '/summary' => $p->{msg});
        }
    }
    return 1;
}


=head2 C<new>

Execute python script in wsadmin, and parse output from command.
Python script must output data in json.

=over

=item C<$file>

String with path to python script

=back

Returns hash containing two hashes:
 messages - hash of wsadmin messages, { messageid, description }
 json - json output of script

=cut

sub wsadmin {
    my ( $self, $file ) = @_;
    my $shellcmd = $self->_create_runfile($file);

    print 'Run command: ' . $self->_mask_password($shellcmd) . "\n";
    return $self->_parse_wsadmin_output(`$shellcmd 2>&1`);
}

sub _parse_wsadmin_output {
    my ( $self, @output ) = @_;

    my @messages = ();
    my $json     = undef;

    print "Command output:\n" . join( "", @output ) . "\n";

    for my $line (@output) {
        if ( $line =~ /^(WAS.*?): (.*)$/ ) {
            push( @messages, { $1 => $2 } );
        }
        elsif ( $line =~ /^\{.*\}$/ ) {
            $json = decode_json($line);
        }
    }

    return {
        messages => \@messages,
        json     => $json
    };
}

sub _mask_password {
    my ( $self, $command ) = @_;

    $command =~ s/-password (.)$self->{configuration}->{password}(.)/-password $1*****$2/g;
    return $command;
}

sub _create_runfile {
    my ( $self, $runfile, @args ) = @_;
    my $configuration = $self->{configuration};

    my $options = { lang => 'jython' };

    if ( $configuration->{conntype} ) {
        $options->{conntype} = $configuration->{conntype};
    }

    if ( $configuration->{websphere_url} && $options->{conntype} eq 'IPC' ) {
        $options->{ipchost} = $configuration->{websphere_url};
    }
    else {
        $options->{host} = $configuration->{websphere_url};
    }

    if ( $configuration->{websphere_port} ) {
        $options->{port} = $configuration->{websphere_port};
    }

    if ( $configuration->{user} ) {
        $options->{user} = $configuration->{user};
    }

    if ( $configuration->{password} ) {
        if ( $^O eq 'MSWin32' ) {
            $options->{password} = "\"$configuration->{password}\"";
        }
        else {
            $options->{password} = "'$configuration->{password}'";
        }
    }

    my $options_string = "";
    for my $optionName ( keys %$options ) {
        $options_string .= " -$optionName $options->{$optionName}";
    }

    if(length(@args) > 0) {
        $options_string .= " ".join(" ", @args);
    }

    return qq{"$self->{wsadminPath}"$options_string-f "$runfile"};
}

sub _getConfiguration {
    my ( $self, $configurationName ) = @_;
    my $ec = $self->{ec};

    my $configurations =
      new ElectricCommander::PropDB( $ec, '/myProject/websphere_cfgs' );
    my %configuration = $configurations->getRow($configurationName);

    # Check if configuration exists
    unless ( keys(%configuration) ) {
        my $error_string = "Configuration '$configurationName' doesn't exists";
        print "Error: $error_string\n";
        $self->setSummary("$error_string");
        return undef;
    }

    # Get user/password out of credential
    my $xpath = $ec->getFullCredential( $configuration{credential} );
    $configuration{user}              = $xpath->findvalue("//userName");
    $configuration{password}          = $xpath->findvalue("//password");
    $configuration{configurationName} = $configurationName;

    return \%configuration;
}

=head2 write_jython_script

Retrieves python script from properties and writes it into the specified $filename.

    $websphere->write_jython_script(
        'path_to_file.py', {},
        augment_filename_with_random_numbers => 1
    );

If augment_filename_with_random_numbers was provided, method will augment filename by random numbers sequence.
For example:

test.py => test_524808719411718.py

=cut

sub write_jython_script {
    my ( $self, $filename, $tmpl_params, %opts ) = @_;

    my $ec = $self->{ec};
    my $script;
    if ($opts{script}) {
        $script = $opts{script};
    }
    else {
        $script = $ec->getProperty("/myProject/wsadmin_scripts/$filename")->getNodeText('//value');
    }

    unless ($script) {
        die "No script content found in /myProject/wsadmin_scripts/$filename property and was not provided in parameters.";
    }

    if ($opts{augment_filename_with_random_numbers}) {
        my $rnd = gen_random_numbers( 42 );
        $rnd = '_' . $rnd;
        $filename =~ s/(\.[\w]+?)$/$rnd$1/s;
    }
    open ( my $fh, '>', $filename ) or die "Cannot open file $filename: $!";
    print $fh $script;
    close $fh;

    return $filename;
}

sub new_simple {
    my ( $class, $ec ) = @_;

    if ( !$ec ) {
        $ec = ElectricCommander->new();
    }
    my $self = {
        ec => $ec
    };
    bless $self, $class;
    return $self;
}

=head2

STATIC METHOD

Returns random numbers sequence by modifier.

=cut

sub gen_random_numbers {
    my ($mod) = @_;

    my $rand = rand($mod);
    $rand =~ s/\.//s;
    return $rand;
}

=head2

Sets summary property to provided one.

    $ws->setSummary("Error occured");

=cut

sub setSummary {
    my ($self, @msg) = @_;

    my $msg = join '', @msg;
    return unless $msg;
    $self->{ec}->setProperty('/myCall/summary', $msg);
    return 1;
}


sub bail_out {
    my ($self, @msg) = @_;

    print join('', @msg);
    exit 1;
}

## internal package, required for logging
package EC::Plugin::Logger;

use strict;
use warnings;
use Data::Dumper;

use constant {
    ERROR => -1,
    INFO => 0,
    DEBUG => 1,
    TRACE => 2,
};

sub new {
    my ($class, $level) = @_;
    $level ||= 0;
    my $self = {level => $level};
    return bless $self,$class;
}

sub warning {
    my ($self, @messages) = @_;

    $self->log(INFO, 'WARNING: ', @messages);
}

sub info {
    my ($self, @messages) = @_;
    $self->log(INFO, @messages);
}

sub debug {
    my ($self, @messages) = @_;
    $self->log(DEBUG, '[DEBUG]', @messages);
}

sub error {
    my ($self, @messages) = @_;
    $self->log(ERROR, '[ERROR]', @messages);
}

sub trace {
    my ($self, @messages) = @_;
    $self->log(TRACE, '[TRACE]', @messages);
}

sub log {
    my ($self, $level, @messages) = @_;

    binmode STDOUT, ':encoding(UTF-8)';

    return if $level > $self->{level};
    my @lines = ();
    for my $message (@messages) {
        unless(defined $message) {
            $message = 'undef';
        }
        if (ref $message) {
            print Dumper($message);
        }
        else {
            print "$message";
        }
    }
    print "\n";
    return 1;
}
1;
