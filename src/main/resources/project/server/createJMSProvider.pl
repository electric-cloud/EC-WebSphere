#
#  Copyright 2015 Electric Cloud, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#


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
    my $firstOptionalArg;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    %configuration = getConfiguration( $ec, $::gConfigurationName );

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    $ScriptFile =
        qq(AdminJMS.createJMSProviderAtScope\(")
      . $::gScope . qq(",")
      . $::gJmsProvider . qq(",")
      . $::gExtContextFactory . qq(",")
      . $::gExtProviderURL . qq(");


    ## This flag, if true, indicates that the next argument is the first optional argument.
    $firstOptionalArg = 'true';


    $ScriptFile .= ",[";

    if ($::gClasspath) {
        $ScriptFile .= "['classpath','" . $::gClasspath . "']";
        $firstOptionalArg = 'false';
    }

    if ($::gDescription) {
        if($firstOptionalArg eq 'false'){
            $ScriptFile .= qq(,);
        }
        $ScriptFile .= "['description','" . $::gDescription . "']";
        $firstOptionalArg = 'false';
    }


    if($::gIsolatedClassLoader eq 1){
        if($firstOptionalArg eq 'false'){
                    $ScriptFile .= qq(,);
        }
        $ScriptFile .=
          "['isolatedClassLoader','true']";
          $firstOptionalArg = 'false';
    }else{
        if($firstOptionalArg eq 'false'){
                  $ScriptFile .= qq(,);
         }
    $ScriptFile .=
              "['isolatedClassLoader','false']";
        $firstOptionalArg = 'false';
    }

    if ($::gNativepath) {
        if($firstOptionalArg eq 'false'){
            $ScriptFile .= qq(,);
        }
        $ScriptFile .= "['nativepath','" . $::gNativepath . "']";
        $firstOptionalArg = 'false';
    }

    if ($::gPropertySet) {
        if($firstOptionalArg eq 'false'){
           $ScriptFile .= qq(,);
        }
        $ScriptFile .= "['propertySet',[['resourceProperties',"
          . $::gPropertySet . "]]]";
        $firstOptionalArg = 'false';
    }

    if ($::gProviderType) {
        if($firstOptionalArg eq 'false'){
                   $ScriptFile .= qq(,);
        }
        $ScriptFile .= "['providerType','" . $::gProviderType . "']";
         $firstOptionalArg = 'false';
    }

    if($::gSupportsASF eq 1){
         if($firstOptionalArg eq 'false'){
                $ScriptFile .= qq(,);
          }

         $ScriptFile .= "['supportsASF','true']";
         $firstOptionalArg = 'false';
    }else{
         if($firstOptionalArg eq 'false'){
                        $ScriptFile .= qq(,);
         }
         $ScriptFile .= "['supportsASF','false']";
         $firstOptionalArg = 'false';
    }

    $ScriptFile .= "]";

    $ScriptFile .= "\)\n";

    open( MYFILE, '>createJMSProvider_script.jython' );

    print MYFILE "$ScriptFile";
    close(MYFILE);

    push( @args, '-f createJMSProvider_script.jython' );
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $connectionType      = $configuration{conntype};
    if ( $connectionType ne '' ) {
        push( @args, '-conntype ' . $connectionType );
    }

    my $hostParamName;

    if ( $connectionType eq IPC_CONNECTION_TYPE ) {
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
