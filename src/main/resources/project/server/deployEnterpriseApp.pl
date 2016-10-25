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

deployEnterpriseApp.pl - a perl library to deploy an enterprise application

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that deploys an enterprise application on a cluster or standalone server

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
use WebSphere::WebSphere;
use WebSphere::Util;

use warnings;
use strict;
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gCluster    = trim(q($[cluster]));
$::gServerList = trim(q($[serverList]));

$::gConfigName        = trim(q($[configName]));
$::gWsadminAbsPath    = trim(q($[wsadminAbsPath]));
$::gCommands          = trim(q($[commands]));
$::gClasspath         = trim(q($[classpath]));
$::gJavaParams        = trim(q($[javaparams]));
$::gAdditionalOptions = trim(q($[additionalcommands]));

# create args array
my @args  = ();
my %props = ();

if ( $::gCluster && $::gServerList ) {
    print "Error : Enter either Target Cluster or Target Server(s).\n";
    return;
}

if ( $::gAdditionalOptions && $::gAdditionalOptions ne '' ) {
    push( @args, $::gAdditionalOptions );
}

if ( $::gClasspath && $::gClasspath ne '' ) {
    push( @args, '-wsadmin_classpath "' . $::gClasspath . '"' );
}

if ( $::gJavaParams && $::gJavaParams ne '' ) {
    foreach my $param ( split( SEPARATOR_CHAR, $::gJavaParams ) ) {
        push( @args, "-javaoption $param" );
    }
}

if ( $::gCommands && $::gCommands ne '' ) {
    foreach my $command ( split( "\n", $::gCommands ) ) {
        push( @args, "-command $command" );
    }
}

#get an EC object
my $ec = new ElectricCommander();
$ec->abortOnError(0);

my $websphere = new WebSphere::WebSphere( $ec, $::gConfigName, $::gWsadminAbsPath );

my $file = 'deploy_enterprise_application.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');

$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);

# open( my $fh, '>', $file ) or die "Cannot write to $file: $!";
# print $fh $script;
# close $fh;

my $debug = $websphere->{configuration}->{debug};

my $shellcmd = $websphere->_create_runfile( $file, @args );
my $escapedCmdLine = $websphere->_mask_password($shellcmd);

print "WSAdmin command line:  $escapedCmdLine\n";

if($debug eq "1") {
    print "WSAdmin script:\n";
    print $script;
    print "== End of WSadmin script.\n";
}

$props{'deployEnterpriseAppLine'} = $escapedCmdLine;
setProperties( $ec, \%props );

#execute command
print `$shellcmd 2>&1`;

#evaluates if exit was successful to mark it as a success or fail the step
if ( $? == SUCCESS ) {
    $ec->setProperty( "/myJobStep/outcome", 'success' );
}
else {
    $ec->setProperty( "/myJobStep/outcome", 'error' );
}

1;
