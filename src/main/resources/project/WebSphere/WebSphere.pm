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

sub wsadmin {
	my ( $self, $file ) = @_;
	my $shellcmd = $self->_create_runfile($file);

	print "EXECUTE: $shellcmd\n";
	return $self->_parse_wsadmin_output(`$shellcmd`);
}

sub _parse_wsadmin_output {
	my ( $self, @output ) = @_;

	my @messages = ();
	my $json     = undef;

	print "OUTPUT: " . join( "\n", @output ) . "\n";

	for my $line (@output) {
		if ( $line =~ /^(WAS.*?): (.*)$/ ) {
			push( @messages, { $1 => $2 } );
		}
		else {
			$json = decode_json($line);
		}
	}

	return {
		messages => \@messages,
		json     => $json
	};
}

sub _create_runfile {
	my ( $self, $runfile ) = @_;
	my $configuration = $self->{configuration};

	my $options = {
		lang     => 'jython',
		conntype => 'SOAP',
		host     => $configuration->{websphere_url},
		port     => $configuration->{websphere_port},
		user     => $configuration->{user},
		password => $configuration->{password}
	};

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
		print "ERROR: Configuration '$configurationName' doesn't exist\n";
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
