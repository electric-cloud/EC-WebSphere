package WebSphere::Discovery;

=head1 NAME

C<WebSphere::Discovery> - Discovery routines for WebSphere
 
=head1 DESCRIPTION

WebSphere discovery routines, used to discover various installation properties
for WebSphere instance, located at ElectricFlow resource.

=head1 COPYRIGHT

Copyright (c) 2016 Electric Cloud, Inc.
All rights reserved

=cut

use strict;
no strict "subs";
use Data::Dumper;
use File::Which qw(which);
use File::Find;
use if $^O eq "MSWin32", Win32::DriveInfo;
use if $^O eq "MSWin32", Win32API::File => qw( :DRIVE_ GetDriveType );
use WebSphere::WebSphere;

# Common locations where we expect wsadmin script installed, platform dependent
my @ws_install_locations = (
    '/Program Files (x86)/IBM/WebSphere/AppServer',
    '/Program Files/IBM/WebSphere/AppServer'
);
my @unix_install_locations =
  ( '/opt/IBM/WebSphere/AppServer', '/usr/local/IBM/WebSphere/AppServer' );

my $DISCOVERY_PROPERTIES_ROOT = "/plugins/@PLUGIN_NAME@/project/ec_discovery/discovered_data";

=head1 METHODS

=head2 C<new>

Constructs a new L<WebSphere::Discovery> object.

=over

=item C<$ec>
        
Reference to L<ElectricCommander> object.
        
=item C<$resourceName>

String with resource name, on which discovery should run.

=back

Returns new instance of L<WebSphere::Discovery> object.
=cut

sub new {
    my ( $class, $ec, $resourceName ) = @_;

    $ec->deleteProperty("/resources[$resourceName]/ec_discovery");
    return bless { ec => $ec, resourceName => $resourceName }, $class;
}

=head2 C<discover>

Starts WebSphere discovery procedures.
Upon completion, sets discovered values on EF resource.

Returns 0 in case of error, 1 otherwise.
=cut

sub discover {
    my ($self, $configurationName) = @_;
    $self->{configurationName} = $configurationName;
    
    my $ec = $self->{ec};

    $ec->deleteProperty("$DISCOVERY_PROPERTIES_ROOT/discovered_data[$configurationName]");

    $self->_setStatus('inprogress');
    
    # Find wsadmin executable path
    my $wsadmin = $self->_discoverWsadminPath();

    if ( not $wsadmin ) {
        my $summary = 'Warning: Cannot find wsadmin executable';
        $self->_setStatus('incomplete', $summary);
        $self->_error("$summary\n");

        return 0;
    }

    my $websphere = new WebSphere::WebSphere($ec, $configurationName, $wsadmin);

    if(not defined $websphere) {
    	my $summary = "Error: Incorrect configuration name '$configurationName'";
        $self->_setStatus('incomplete', $summary);
        $self->_error("$summary\n");

        return 1;
    }

    my $script = $ec->getProperty("/myProject/wsadmin_scripts/discover.py")->getNodeText('//value');

    open(my $fh, '>', 'discover.py') or die "Cannot write to 'discover.py' $!";
    print $fh $script;
    close $fh;

    # TODO Check for errors
    my $ret = $websphere->wsadmin('discover.py');
    my $json = $ret->{json};
    
    if(not $json) {
        my $summary = 'Did not get correct output from wsadmin command, see discovery job log';
        $self->_setStatus('incomplete', $summary);

        return 1;
    }
    
    my $servers = $json->{servers};
    for my $server (keys %{$servers}) {
        my ($node, $short_server) = split("=", $server);
        $self->_createProperty("$configurationName/servers/$short_server", {description => $server, propertyType => 'sheet' });

    	for my $application (@{$servers->{$server}}) {
            $self->_setProperty("$configurationName/nodes/$node/$short_server", "");
            $self->_setProperty("$configurationName/servers/$short_server/$application", "");
            $self->_setProperty("$configurationName/applications/$application/servers/$short_server", "", {description => $server });
    	}
    }

    my $clusters = $json->{clusters};
    for my $cluster (keys %{$clusters}) {
        $self->_createProperty("$configurationName/clusters/$cluster", {description => $cluster, propertyType => 'sheet' });

        for my $application (@{$clusters->{$cluster}}) {
            $self->_setProperty("$configurationName/clusters/$cluster/$application", "");
            $self->_setProperty("$configurationName/applications/$application/clusters/$cluster", "");
        }
    }

    $ec->setProperty( "/resources[$self->{resourceName}]/ec_discovery/@PLUGIN_KEY@/$configurationName/wsadminPath", $wsadmin );
    $self->_setProperty( "$configurationName/wsadminPath", "\$[/myResource/ec_discovery/@PLUGIN_KEY@/$configurationName/wsadminPath]", {expandable => 0});
    
    $self->_setStatus('completed');

    return 0;
}

sub _setStatus {
    my ( $self, $status, $summary ) = @_;
    my $configurationName = $self->{configurationName};
    
    $self->_setProperty( "$configurationName/status", $status );

    if ( defined $summary ) {
        $self->_setProperty( "$configurationName/summary", $summary );
    }
}

sub _createProperty {
    my ( $self, $name, $params) = @_;
    my $path = "/plugins/@PLUGIN_NAME@/project/ec_discovery/discovered_data";

    $params = {} unless defined $params;

    $self->{ec}->createProperty( "$path/$name", $params);
}

sub _setProperty {
    my ( $self, $name, $value, $params) = @_;

 	$params = {} unless defined $params;
    $params->{value} = $value;

    $self->{ec}->setProperty( "$DISCOVERY_PROPERTIES_ROOT/$name", $params);
}

sub _getProperty {
    my ( $self, $name ) = @_;

    return $self->{ec}->getProperty("$DISCOVERY_PROPERTIES_ROOT/$name")->getNodeText('//value');
}

sub _debug {
    my ( $self, $message ) = @_;
    $self->_message( $message, 2 );
}

sub _info {
    my ( $self, $message ) = @_;
    $self->_message( $message, 1 );
}

sub _error {
    my ( $self, $message ) = @_;
    $self->_message( $message, 0 );
}

sub _message {
    my ( $self, $message, $lvl ) = @_;
    $lvl = defined $lvl ? $lvl : 1;
    print($message);
}

sub _discoverWsadminPath {
    my ($self) = @_;
    $self->_info('Checking wsadmin location in PATH environment variable... ');

    my $wsadmin = which('wsadmin');

    if ($wsadmin) {
        $self->_info("$wsadmin\n");

        return $wsadmin;
    } else {
        $self->_info("NOT FOUND\n");
    }

    $self->_info("\nSearching wsadmin in common installation locations:\n");
    my @directories = ();

    # Unix discovery
    if ( $^O ne "MSWin32" ) {
        return $self->_searchFile( \@unix_install_locations, 'wsadmin.sh' );
    }

    # On Windows, search all fixed drives for WebSphere installation
    my @drives =
      grep { DRIVE_FIXED == GetDriveType("$_:") }
      Win32::DriveInfo::DrivesInUse();
    for my $directory (@ws_install_locations) {
        for my $drive (@drives) {
            push( @directories, "$drive:$directory" );
        }
    }

    $self->_info("\n");
    return $self->_searchFile( \@directories, 'wsadmin.bat' );
}

sub _searchFile {
    my ( $self, $directories, $file_name ) = @_;
    my $path = undef;

    my $wanted = sub {
        $path = $File::Find::name
          if $_ eq $file_name
              and -x $_
              and ( not defined $path
                  or length($path) > length($File::Find::name) );
    };

    for my $directory (@$directories) {
        $self->_info("\t$directory... ");

        find( $wanted, ($directory) );

        if ( not $path ) {
            $self->_info("NOT FOUND\n");
            return;
        }

        $self->_info("$path\n");
        return $path;
    }
}

1;
