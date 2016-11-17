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

use warnings;
use strict;
$|=1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gInstanceName = trim(q($[instancename]));
$::gScriptLocation = trim(q($[scriptlocation]));
$::gAdditionalCommands = "$[additionalcommands]";
$::gConfigName = "$[configname]";

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------


########################################################################
# main - contains the whole process to be done by the plugin, it builds 
#        the command line, sets the properties and the working directory
#
# Arguments:
#   none
#
# Returns:
#   none
#
########################################################################
sub main() {
    # create args array
    my @args = ();
    my %props;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    my %configuration;

    if ($::gConfigName ne '') {
        %configuration = getConfiguration($ec, $::gConfigName);
    }

    my $extension = '';
    my $actualOperativeSystem = $^O;

    my $fixedLocation = $::gScriptLocation;

    push(@args, '"'.$fixedLocation.'"');
    # if target: add to command string
    if ($::gInstanceName && $::gInstanceName ne '') {
        push(@args, $::gInstanceName);
    }

    if (%configuration) {
        if ($configuration{'user'} ne '') {
            push(@args, '-username ' . $configuration{'user'});
        }
        if ($configuration{'password'} ne '') {
            push(@args, '-password ' . $configuration{'password'});
        }
        if ($configuration{'websphere_port'} ne '') {
            push(@args, '-port ' . $configuration{'websphere_port'});
        }
    }
    if ($::gAdditionalCommands && $::gAdditionalCommands ne '') {
        push(@args, $::gAdditionalCommands);
    }

    my $cmdLine = createCommandLine(\@args);
    my $escapedCmdLine = maskPassword($cmdLine, $configuration{'password'});
    $props{'stopServerLine'} = $escapedCmdLine;
    setProperties($ec, \%props);
    print "WSAdmin command line: $escapedCmdLine\n";

    # execute command
    my $content = `$cmdLine`;
    # print log
    print "$content\n";

    # evaluates if exit was successful to mark it as a success or fail the step
    if ($? == SUCCESS) {
        $ec->setProperty("/myJobStep/outcome", 'success');
        # set any additional error or warning conditions here
        # there may be cases that an error occurs and the exit code is 0.
        # we want to set to correct outcome for the running step
        if ($content =~ m/WSVR0028I:/) {
            #license expired warning
            $ec->setProperty("/myJobStep/outcome", 'warning');
        }
    }
    else {
        $ec->setProperty("/myJobStep/outcome", 'error');
    }
}

main();

1;
