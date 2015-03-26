
=head1 NAME

createJMSProvider.pl - a perl library to that creates a new JMS provider.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to that creates a new JMS provider.

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

$::gWSAdminAbsPath      = trim(q($[wsadminAbsPath]));
$::gScope               = trim(q($[scope]));
$::gJmsProvider         = trim(q($[jmsProvider]));
$::gExtContextFactory   = trim(q($[extContextFactory]));
$::gExtProviderURL      = trim(q($[extProviderURL]));
$::gClasspath           = trim(q($[classpath]));
$::gDescription         = trim(q($[description]));
$::gIsolatedClassLoader = trim(q($[isolatedClassLoader]));
$::gNativepath          = trim(q($[nativepath]));
$::gProviderType        = trim(q($[providerType]));
$::gPropertySet         = trim(q($[propertySet]));
$::gSupportsASF         = trim(q($[supportsASF]));
$::gConnectionType      = trim(q($[connectiontype]));
$::gConfigurationName   = "$[configname]";

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
    my $ScriptFile;
    my $optionalArgsSupplied;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    %configuration = getConfiguration( $ec, $::gConfigurationName );

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    $ScriptFile =
        "AdminJMS. createJMSProviderAtScope (\""
      . $::gScope . "\",\""
      . $::gJmsProvider . "\",\""
      . $::gExtContextFactory . "\",\""
      . $::gExtProviderURL . "\"";

    if (   $::gClasspath
        || $::gDescription
        || $::gIsolatedClassLoader
        || $::gNativepath
        || $::gProviderType
        || $::gPropertySet
        || $::gSupportsASF )
    {
        ## if any of the optional arguments are supplied.
        $optionalArgsSupplied = 'true';
    }

    if ($optionalArgsSupplied) {
        $ScriptFile .= ",[";
    }
    if ($::gClasspath) {
        $ScriptFile .= "['classpath','" . $::gClasspath . "']";
    }

    if ($::gDescription) {
        $ScriptFile .= ",['description','" . $::gDescription . "']";
    }

    if ($::gIsolatedClassLoader) {
        $ScriptFile .=
          ",['isolatedClassLoader','" . $::gIsolatedClassLoader . "']";
    }

    if ($::gNativepath) {
        $ScriptFile .= ",['nativepath','" . $::gNativepath . "']";
    }

    if ($::gPropertySet) {
        $ScriptFile .= ",['propertySet',[['resourceProperties',"
          . $::gPropertySet . "]]]";
    }

    if ($::gProviderType) {
        $ScriptFile .= ",['providerType','" . $::gProviderType . "']";
    }

    if ($::gSupportsASF) {
        $ScriptFile .= ",['supportsASF','" . $::gSupportsASF . "']";
    }

    if ($optionalArgsSupplied) {
        $ScriptFile .= "]";
    }
    $ScriptFile .= ")\n";

    open( MYFILE, '>createJMSProvider_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f createJMSProvider_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

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

    $props{'configureEJBContainerLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    my $content = `$cmdLine`;

    #print log
    print "$content\n";

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

main();

1;
