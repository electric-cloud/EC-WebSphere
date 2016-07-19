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
	my ( $class, $ec, $configurationName, $wsadminPath ) = @_;

	my $self = bless { ec => $ec, wsadminPath => $wsadminPath }, $class;
	my $configuration = $self->_getConfiguration($configurationName);

	if ( not $configuration ) {
		return undef;
	}

	$self->{configuration} = $configuration;
	return $configuration ? $self : undef;
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

	$command =~
	  s/-password (.)$self->{configuration}->{password}(.)/-password $1*****$2/g;
	return $command;
}

sub _create_runfile {
	my ( $self, $runfile ) = @_;
	my $configuration = $self->{configuration};

 	my $options = {
		lang     => 'jython'
	};
	
	if($configuration->{conntype}) {
		$options->{conntype} = $configuration->{conntype};
	}

    if($configuration->{websphere_url} && $options->{conntype} eq 'IPC') {
    	$options->{ipchost} = $configuration->{websphere_url};
    } else {
        $options->{host} = $configuration->{websphere_url};
    }

    if($configuration->{websphere_port}) {
    	$options->{port} = $configuration->{websphere_port};
    }
    
    if($configuration->{user}) {
    	$options->{user} = $configuration->{user};
    }

    if($configuration->{password}) {
        if ($^O eq 'MSWin32') {
         $options->{password} = "\"$configuration->{password}\"";
        } else {
         $options->{password} = "'$configuration->{password}'";
        }
    }
 
	my $options_string = "";
	for my $optionName ( keys %$options ) {
		$options_string .= " -$optionName $options->{$optionName}";
	}

	return qq{"$self->{wsadminPath}"$options_string -f "$runfile"};
}

sub _getConfiguration {
	my ( $self, $configurationName ) = @_;
	my $ec = $self->{ec};

	my $configurations =
	  new ElectricCommander::PropDB( $ec, '/myProject/websphere_cfgs' );
	my %configuration = $configurations->getRow($configurationName);

	# Check if configuration exists
	unless ( keys(%configuration) ) {
		print "Error: Configuration '$configurationName' doesn't exist\n";
		return undef;
	}

	# Get user/password out of credential
	my $xpath = $ec->getFullCredential( $configuration{credential} );
	$configuration{user}              = $xpath->findvalue("//userName");
	$configuration{password}          = $xpath->findvalue("//password");
	$configuration{configurationName} = $configurationName;

	return \%configuration;
}

1;
