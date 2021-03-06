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

modifyApplicationClassLoader.pl - a perl library to modify application's ClassLoader behaviour.

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that modifies application's ClassLoader behaviour.

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

my $configName        = trim(q($[configurationName]));
my $wsadminAbsPath    = trim(q($[wsadminAbsPath]));
my $applicationName   = trim(q($[applicationName]));
my $loadOrder         = trim(q($[loadOrder]));
my $classLoaderPolicy = trim(q($[classLoaderPolicy]));

# create args array
my @args  = ();
my %props = ();


#get an EC object
my $ec = new ElectricCommander();
$ec->abortOnError(0);

print "Will modify ClassLoader for " . $applicationName . " application. Load Order: $loadOrder, ClassLoader policy: $classLoaderPolicy\n";
my $websphere = new WebSphere::WebSphere( $ec, $configName, $wsadminAbsPath );

my $file = 'modify_application_classloader.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');

$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);

my $shellcmd = $websphere->_create_runfile( $file, @args );
my $escapedCmdLine = $websphere->_mask_password($shellcmd);

print "WSAdmin command line:  $escapedCmdLine\n";
$props{'modifyApplicationClassLoaderLine'} = $escapedCmdLine;
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
