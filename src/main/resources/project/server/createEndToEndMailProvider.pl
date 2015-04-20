
=head1 NAME

createEndToEndMailProvider.pl - a perl library to that creates a complete mail provider.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library that creates mail provider with a protocol provider, a mail session and a
custom property in the specified scope.

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
$::gScope                = trim(q($[scope]));
$::gMailProviderName     = trim(q($[mailProviderName]));
$::gMailProviderDesc     = trim(q($[mailProviderDesc]));
$::gIsolatedClassLoader  = trim(q($[isolatedClassLoader]));
$::gClasspath            = trim(q($[classpath]));
$::gCustPropName         = trim(q($[custPropName]));
$::gCustPropValue        = trim(q($[custPropValue]));
$::gProtocolProviderName = trim(q($[protocolProviderName]));
$::gClassName            = trim(q($[className]));
$::gProtocolProviderType = trim(q($[protocolProviderType]));
$::gMailSessionName      = trim(q($[mailSessionName]));
$::gMailSessionJNDIName  = trim(q($[mailSessionJNDIName]));
$::gCategory             = trim(q($[category]));
$::gDebug                = trim(q($[debug]));
$::gMailSessionDesc      = trim(q($[mailSessionDesc]));
$::gMailStoreHost        = trim(q($[mailStoreHost]));
$::gMailStorePort        = trim(q($[mailStorePort]));
$::gMailStoreUser        = trim(q($[mailStoreUser]));
$::gMailTransportHost    = trim(q($[mailTransportHost]));
$::gMailTransportPort    = trim(q($[mailTransportPort]));
$::gMailTransportUser    = trim(q($[mailTransportUser]));
$::gStrict               = trim(q($[strict]));
$::gWSAdminAbsPath       = trim(q($[wsadminAbsPath]));
$::gConnectionType       = trim(q($[connectiontype]));
$::gConfigurationName    = "$[configname]";

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

    %configuration = getConfiguration( $ec, $::gConfigurationName );
    $ScriptFile =
        'AdminResources.createCompleteMailProviderAtScope("'
      . $::gScope . '","'
      . $::gMailProviderName . '"';

    ## Custom property
    if ( $::gCustPropName && $::gCustPropValue ) {
        ## User has provided both custom property name and value :: Valid case
        $ScriptFile .=
          ',"' . $::gCustPropName . '","' . $::gCustPropValue . '"';

    }
    elsif ( $::gCustPropName eq '' && $::gCustPropValue eq '' ) {
        ## User don't want to specify any specific custom property :: Valid case
        $ScriptFile .= ',"dummyProp","dummyVal"';

    }
    elsif ( $::gCustPropName eq '' || $::gCustPropValue eq '' ) {
        ## User have provided either property name or value :: Invalid case
        die
"Error : Either provide both property name and value or do not specify anything.";
    }

    $ScriptFile .= ',"'
      . $::gProtocolProviderName . '","'
      . $::gClassName . '","'
      . $::gProtocolProviderType . '","'
      . $::gMailSessionName . '","'
      . $::gMailSessionJNDIName . '"';

# a flag to check if the optional arguments for mail session are provided or not.
    my $mailProviderOptArgsProvided = 0;

    ## Optional arguments for mail provider starts here.
    $ScriptFile .= ",[";

    if ($::gMailProviderDesc) {
        $ScriptFile .= "['description','" . $::gMailProviderDesc . "']";
        $mailProviderOptArgsProvided = 1;
    }

    if ($::gIsolatedClassLoader) {
        ## If user wants to specify separate class loader for mail provider implementation, only then specify its implementation classpath.

        if ($::gClasspath) {
            if ( $mailProviderOptArgsProvided != 0 ) {
                $ScriptFile .=
                  ",";    ## isolatedClassLoader is second optional argument.
            }
            $ScriptFile .= "['isolatedClassLoader','true'],['classpath','"
              . $::gClasspath . "']";
            $mailProviderOptArgsProvided = 1;
        }
        else {
            die
"Error :  Specify class path to JAR file that contains the implementation classes for this mail provider.";
        }
    }

    ## Optional arguments for mail provider ends here.
    $ScriptFile .= "]";

    ## Optional arguments for mail session starts here.
    $ScriptFile .= ",[";

# a flag to check if the optional arguments for mail session are provided or not.
    my $mailSessionOptArgsProvided = 0;

    if ($::gCategory) {
        $ScriptFile .= "['category','" . $::gCategory . "']";
        $mailSessionOptArgsProvided = 1;
    }

    ## test value for checked and unchecked values. default is false.
    if ($::gDebug) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['debug','true']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailSessionDesc) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['description','" . $::gMailSessionDesc . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailStoreHost) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailStoreHost','" . $::gMailStoreHost . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailStorePort) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailStorePort','" . $::gMailStorePort . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailStoreUser) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }

        # Returns an xPath object containing the userName.
        $xpath =
          $ec->getFullCredential( $::gMailStoreUser, { value => "userName" } );

        # Parse userName from response.
        $username = $xpath->find("//userName");

        if ( defined $username && "$username" eq "" ) {
            print "Empty username found in '"
              . $::gMailStoreUser
              . "' credential object.\n";
        }
        $ScriptFile .= "['mailStoreUser','" . $username . "']";

        # Returns an xPath object containing the password.
        $xpath =
          $ec->getFullCredential( $::gMailStoreUser, { value => "password" } );

        # Parse password from response.
        $mailStorePassword = $xpath->find("//password");

        if ( defined $mailStorePassword && "$mailStorePassword" eq "" ) {
            print "Empty password found in '"
              . $::gMailStoreUser
              . "' credential object.\n";
        }
        $ScriptFile .= ",['mailStorePassword','" . $mailStorePassword . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailTransportHost) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailTransportHost','" . $::gMailTransportHost . "']";
        $mailSessionOptArgsProvided = 1;
    }
    if ($::gMailTransportPort) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }
        $ScriptFile .= "['mailTransportPort','" . $::gMailTransportPort . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gMailTransportUser) {
        if ( $mailSessionOptArgsProvided != 0 ) {
            ## This is not a first optional argument. Append , here .
            $ScriptFile .= ",";
        }

        # Returns an xPath object containing the userName.
        $xpath =
          $ec->getFullCredential( $::gMailTransportUser,
            { value => "userName" } );

        # Parse userName from response.
        $username = $xpath->find("//userName");

        if ( defined $username && "$username" eq "" ) {
            print "Empty username found in '"
              . $::gMailTransportUser
              . "' credential object.\n";
        }
        $ScriptFile .= "['mailTransportUser','" . $username . "']";

        # Returns an xPath object containing the password.
        $xpath =
          $ec->getFullCredential( $::gMailTransportUser,
            { value => "password" } );

        print "XPATH for transport user password:" . $xpath;

        # Parse password from response.
        $mailTransportPassword = $xpath->find("//password");

        if ( defined $mailTransportPassword && "$mailTransportPassword" eq "" ) {
            print "Empty password found in '"
              . $::gMailTransportUser
              . "' credential object.\n";
        }
        $ScriptFile .= ",['mailTransportPassword','" . $mailTransportPassword . "']";
        $mailSessionOptArgsProvided = 1;
    }

    if ($::gStrict) {
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
     $ScriptFile .= "if dm:\n"
                   . "\tprint 'Synchronizing configuration repository with nodes. Please wait...'\n"
                   . "\t" . 'nodes=AdminControl.invoke(dm, "syncActiveNodes", "true")' . "\n"
                   . "\t" . "print 'The following nodes have been synchronized:'+str(nodes)\n"
                   . "else:\n"
                   . "\t" . "print 'Standalone server, no nodes to sync'\n";

    $ScriptFile .=
        "print 'Mail provider "
      . $::gMailProviderName
      . " created successfully.'\n";

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    open( MYFILE, '>createCompleteMailProvider_script.jython' );
    print MYFILE "$ScriptFile ";


    close(MYFILE);

    push( @args, '-f createCompleteMailProvider_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    if ( $::gConnectionType && $::gConnectionType ne '' ) {
        push( @args, '-conntype ' . $::gConnectionType );
    }

    my $hostParamName;

    if ( $::gConnectionType eq IPC_CONNECTION_TYPE ) {
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
    $escapedCmdLine = maskPasswordsForMailProviders( $content, $mailStorePassword );
    $escapedCmdLine = maskPasswordsForMailProviders( $escapedCmdLine, $mailTransportPassword );

    #print log
    print "$escapedCmdLine \n ";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {

        $ec->setProperty( "/myJobStep/outcome", 'success' );

        #set any additional error or warning conditions here
        #there may be cases that an error occurs and the exit code is 0.
        #we want to set to correct outcome for the running step
        if ( $content =~ m/WSVR0028I:/ ) {

            #license expired warning
            $ec->setProperty( "/myJobStep/outcome", 'warning' );
        }

    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }

}

sub maskPasswordsForMailProviders {
    my ($line, $password) = @_;
    return $line unless defined $password && length($password);

    $line =~ s/Password\', \'$password\']/Password', '****']/g;
    return $line;
}

main();


1;
