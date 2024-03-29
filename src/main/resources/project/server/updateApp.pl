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


# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;
use WebSphere::WebSphere;

use warnings;
use strict;
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $gAppName           = trim(q($[appName]));
my $gClusterName       = trim(q($[clusterName]));
my $gServerName        = trim(q($[serverName]));
my $gWSAdminAbsPath    = trim(q($[wsadminAbsPath]));
my $gContentType       = trim(q($[contentType]));
my $gOperation         = trim(q($[operation]));
my $gContent           = trim(q($[content]));
my $gContentURI        = trim(q($[contentURI]));
my $gAdditionalParams  = trim(q($[additionalParams]));
my $gConfigurationName = q{$[configname]};
my $gRestart           = trim(q($[restart]));

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------
sub main {
    my @args = ();

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    if( $gOperation ne 'delete' && $gContent eq ''){
        print "Error : Content field can not be empty for operation " . $gOperation . ".";
        exit 1;
    }

    if( ($gContentType eq 'file' || $gContentType eq 'modulefile') && $gContentURI eq ''){
        print "Error : Content URI field can not be empty for content type " . $gContentType . ".";
        exit 1;
    }

    if( $gContentType eq 'app' && $gOperation ne 'update') {
        print "Error : Invalid operation " . $gOperation . " for content type APPLICATION.";
        exit 1;
    }

    if ( !$gServerName && !$gClusterName ) {
        print "Error: neither server name nor cluster name was specified";
        exit 1;
    }
    push(@args, '-appName ' . $gAppName);
    if($gClusterName ne ''){
        push(@args, '-clusterName ' . $gClusterName);
    }
    if($gServerName ne '') {
        push(@args, '-serverName ' . $gServerName);
    }
    push(@args, '-contentType ' . $gContentType);
    push(@args, '-contentURI ' . $gContentURI);
    push(@args, '-content ' . $gContent);
    push(@args, '-operation ' . $gOperation);
    push(@args, '-restart ' . $gRestart);
    if($gAdditionalParams ne '') {
        push(@args, '-additionalParams ' . $gAdditionalParams);
    }


    my $config_name = q{$[configname]};
    my $websphere = WebSphere::WebSphere->new($ec, $config_name, $gWSAdminAbsPath);
    die "No configuration found for config name $config_name" unless $websphere;

    my $python_filename = 'update_app.py';
    $python_filename = $websphere->write_jython_script(
        $python_filename, {},
        augment_filename_with_random_numbers => 1,
    );

    my $shellcmd = $websphere->_create_runfile( $python_filename, @args );
    my $escapedCmdLine = $websphere->_mask_password($shellcmd);

    print "WSAdmin command line:  $escapedCmdLine\n";
    my $props = {};
    $props->{'updateAppLine'} = $escapedCmdLine;
    setProperties( $ec, $props );    #execute command
    print `$shellcmd 2>&1`;

    #evaluates if exit was successful to mark it as a success or fail the step
    if ( $? == SUCCESS ) {
        $ec->setProperty( "/myJobStep/outcome", 'success' );
    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }
}



main();

1;
