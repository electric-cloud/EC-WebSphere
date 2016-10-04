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
my $gAdditionalParams    = trim(q($[additionalParams]));
my $gConfigurationName = q{$[configname]};

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
        return;
    }

    if( ($gContentType eq 'file' || $gContentType eq 'modulefile') && $gContentURI eq ''){
        print "Error : Content URI field can not be empty for content type " . $gContentType . ".";
        return;
    }

    if( $gContentType eq 'app' && $gOperation ne 'update') {
        print "Error : Invalid operation " . $gOperation . " for content type APPLICATION.";
        return;
    }

    my $config_name = q{$[configname]};
    my $websphere = WebSphere::WebSphere->new($ec, $config_name, $gWSAdminAbsPath);
    die "No configuration found for config name $config_name" unless $websphere;

    my $python_filename = 'update_app.py';
    $websphere->write_jython_script($python_filename);

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
