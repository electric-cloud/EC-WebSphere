
=head1 NAME

createMailSession.pl - a perl library to that creates a new JavaMail session.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library that creates  a mail session in the specified scope.

=head1 LICENSE

Copyright (c) 2014 Electric Cloud, Inc.
All rights reserved.

=head1 AUTHOR

    ---

=head2 METHODS

=cut

# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;

use warnings;
use strict;
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------
my $gScope                 = trim(q($[scope]));
my $gMailSessionName       = trim(q($[mailSessionName]));
my $gMailSessionDesc       = trim(q($[mailSessionDesc]));
my $gMailSessionJNDIName   = trim(q($[mailSessionJNDIName]));
my $gCategory              = trim(q($[category]));
my $gDebug                 = trim(q($[debug]));
my $gMailStoreHost         = trim(q($[mailStoreHost]));
my $gMailStorePort         = trim(q($[mailStorePort]));
my $gMailStoreProtocol     = trim(q($[mailStoreProtocol]));
my $gMailStoreUser         = trim(q($[mailStoreUser]));
my $gMailFrom              = trim(q($[mailFrom]));
my $gMailTransportHost     = trim(q($[mailTransportHost]));
my $gMailTransportPort     = trim(q($[mailTransportPort]));
my $gMailTransportProtocol = trim(q($[mailTransportProtocol]));
my $gMailTransportUser     = trim(q($[mailTransportUser]));
my $gStrict                = trim(q($[strict]));
my $gWSAdminAbsPath        = trim(q($[wsadminAbsPath]));
my $gConnectionType        = trim(q($[connectiontype]));
my $gConfigurationName     = "$[configname]";

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------

=over

=item B<main>

main - contains the whole process to be done by the plugin, it builds
       the command line, sets the properties and the working directory

B<Params:>

none

B<Returns:>

none

=back

=cut

sub main() {

    # create args array
    my @args = ();
    my %props;
    my %configuration;
    my $ScriptFile = "";
    my $xpath;
    my $username;
    my $mailStorePassword;
    my $mailTransportPassword;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    ## Convert format of scope variable from 'Cell=Cell01,Node=Node01,Server=server1' to required /Cell:Cell01/Node:Node01/Server:server1/
    $gScope =~ s/=/:/g;
    $gScope =~ s/,/\//g;
    $gScope = '/' . $gScope . '/';


    %configuration = getConfiguration( $ec, $gConfigurationName );

    $ScriptFile = "import re\n";

    $ScriptFile .= "scopeId = AdminConfig.getid('" . $gScope . "')\n";

    ## Retrieve configuration id for transport protocol provider
    if ($gMailTransportProtocol) {
        $ScriptFile .=
"for protocolProvider in AdminConfig.list('ProtocolProvider',scopeId).splitlines():\n"
          . "\tif(re.search('"
          . $gMailTransportProtocol
          . "',protocolProvider)):\n"
          . "\t\tprint 'The configuration id for "
          . $gMailTransportProtocol
          . " : ' + protocolProvider\n"
          . "\t\ttrasportProtocolProviderId = protocolProvider\n";
    }

    ## Retrieve configuration id for store protocol provider
    if ($gMailStoreProtocol) {
        $ScriptFile .=
"for protocolProvider in AdminConfig.list('ProtocolProvider',scopeId).splitlines():\n"
          . "\tif(re.search('"
          . $gMailStoreProtocol
          . "',protocolProvider)):\n"
          . "\t\tprint 'The configuration id for "
          . $gMailStoreProtocol
          . " : ' + protocolProvider\n"
          . "\t\tstoreProtocolProviderId = protocolProvider\n";

    }
    $ScriptFile .=
        'AdminResources.createMailSessionAtScope("'
      . $gScope
      . '","Built-in Mail Provider"';

    $ScriptFile .=
      ',"' . $gMailSessionName . '","' . $gMailSessionJNDIName . '"';

# a flag to check if the optional arguments for mail session are provided or not.
    my $mailSessionOptArgsProvided = 0;

    ## Optional arguments for mail provider starts here.
    $ScriptFile .= ",[";

    if ($gMailSessionDesc) {
        $ScriptFile .= "['description','" . $gMailSessionDesc . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gCategory) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['category','" . $gCategory . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gDebug) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['debug','true']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailStoreHost) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailStoreHost','" . $gMailStoreHost . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailStorePort) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailStorePort','" . $gMailStorePort . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailStoreProtocol) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailStoreProtocol',storeProtocolProviderId]";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailStoreUser) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }

        # Returns an xPath object containing the userName.
        $xpath =
          $ec->getFullCredential( $gMailStoreUser, { value => "userName" } );

        # Parse userName from response.
        $username = $xpath->find("//userName");

        if ( defined $username && "$username" eq "" ) {
            print "Empty username found in '"
              . $gMailStoreUser
              . "' credential object.\n";
        }
        $ScriptFile .= "['mailStoreUser','" . $username . "']";

        # Returns an xPath object containing the password.
        $xpath =
          $ec->getFullCredential( $gMailStoreUser, { value => "password" } );

        # Parse password from response.
        $mailStorePassword = $xpath->find("//password");

        if ( defined $mailStorePassword && "$mailStorePassword" eq "" ) {
            print "Empty password found in '"
              . $gMailStoreUser
              . "' credential object.\n";
        }
        $ScriptFile .= ",['mailStorePassword','" . $mailStorePassword . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailTransportHost) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailTransportHost','" . $gMailTransportHost . "']";
        $mailSessionOptArgsProvided = 1;
    }
    if ($gMailTransportPort) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailTransportPort','" . $gMailTransportPort . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailTransportProtocol) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailTransportProtocol',trasportProtocolProviderId]";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailTransportUser) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }

        # Returns an xPath object containing the userName.
        $xpath =
          $ec->getFullCredential( $gMailTransportUser,
            { value => "userName" } );

        # Parse userName from response.
        $username = $xpath->find("//userName");

        if ( defined $username && "$username" eq "" ) {
            print "Empty username found in '"
              . $gMailTransportUser
              . "' credential object.\n";
        }
        $ScriptFile .= "['mailTransportUser','" . $username . "']";

        # Returns an xPath object containing the password.
        $xpath =
          $ec->getFullCredential( $gMailTransportUser,
            { value => "password" } );

        # Parse password from response.
        $mailTransportPassword = $xpath->find("//password");
        print "Mail transport password:" . $mailTransportPassword;

        if ( defined $mailTransportPassword && "$mailTransportPassword" eq "" )
        {
            print "Empty password found in '"
              . $gMailTransportUser
              . "' credential object.\n";
        }
        $ScriptFile .=
          ",['mailTransportPassword','" . $mailTransportPassword . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gMailFrom) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailFrom','" . $gMailFrom . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($gStrict) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['strict','true']";
        $mailSessionOptArgsProvided = 1;
    }
    else {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['strict','false']";
        $mailSessionOptArgsProvided = 1;
    }

    $ScriptFile .= "])\n";
    $ScriptFile .= "AdminConfig.save()\n";

    # Obtain deployment manager MBean
    $ScriptFile .= "dm=AdminControl.queryNames('type=DeploymentManager,*')\n";

# Synchronization of configuration changes is only required in network deployment.not in standalone server environment.
    $ScriptFile .=
        "if dm:\n"
      . "\tprint 'Synchronizing configuration repository with nodes. Please wait...'\n"
      . "\t"
      . 'nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")' . "\n" . "\t"
      . "print 'The following nodes have been synchronized:'+str(nodes)\n"
      . "else:\n" . "\t"
      . "print 'Standalone server, no nodes to sync'\n";

    $ScriptFile .=
      "print 'Mail session " . $gMailSessionName . " created successfully.'\n";

    push( @args, '"' . $gWSAdminAbsPath . '"' );

    open( MYFILE, '>createMailSession_script.jython' );
    print MYFILE "$ScriptFile ";
    close(MYFILE);

    push( @args, '-f createMailSession_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    if ( $gConnectionType && $gConnectionType ne '' ) {
        push( @args, '-conntype ' . $gConnectionType );
    }

    my $hostParamName;

    if ( $gConnectionType eq IPC_CONNECTION_TYPE ) {
        $hostParamName = '-ipchost';
    }
    else {
        $hostParamName = '-host';
    }

    if ( $configuration{'websphere_url'} ne '' ) {
        push( @args, $hostParamName . ' ' . $configuration{'websphere_url'} );
    }

    if ( $configuration{'websphere_port'} ne '' ) {
        push( @args, '-port ' . $configuration{'websphere_port'} );
    }

    if ( $configuration{'user'} ne '' ) {
        push( @args, '-user ' . $configuration{'user'} );
    }

    if ( $configuration{'password'} ne '' ) {
        push( @args, '-password ' . $configuration{'password'} );
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'createCompleteMailProviderLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print " WSAdmin command line : $escapedCmdLine \n ";

    #execute command
    my $content = `$cmdLine`;
    $escapedCmdLine =
      maskPasswordsForMailProviders( $content, $mailStorePassword );
    $escapedCmdLine =
      maskPasswordsForMailProviders( $escapedCmdLine, $mailTransportPassword );

    #print log
    print "$escapedCmdLine \n ";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? >> 8 != 0 ) {

        $ec->setProperty( "/myJobStep/outcome", 'error' );

    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'success' );

        #set any additional error or warning conditions here
        #there may be cases that an error occurs and the exit code is 0.
        #we want to set to correct outcome for the running step
        if ( $content =~ m/WSVR0028I:/ ) {

            #license expired warning
            $ec->setProperty( "/myJobStep/outcome", 'warning' );
        }
    }

}

sub maskPasswordsForMailProviders {
    my ( $line, $password ) = @_;
    return $line unless defined $password && length($password);

    $line =~ s/Password',\s'$password'/Password', '****']/g;
    return $line;
}

main();

1;
