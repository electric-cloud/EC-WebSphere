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

publishWSDL.pl - a perl library to that publishes WSDL files.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to that publishes WSDL files in each web services-enabled module to the file system location.

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
use WebSphere::WebSphere;
use warnings;
use strict;
$|=1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

# SOAP prefix processing is remaining.
$::gCommands = q($[commands]);
$::gAppName = trim(q($[appname]));
$::gpublish_location = trim(q($[publish_location]));
$::gpublish_location =~ s$\\$/$g;
$::gScriptFile = 'AdminApp.publishWSDL(\'' . $::gAppName . '\',\'' . $::gpublish_location . '\')' . "\n"
                . "print 'WSDL files for " . $::gAppName . " published successfully'";
$::gWSAdminAbsPath = trim(q($[wsadminabspath]));
$::gClasspath = trim(q($[classpath]));
$::gJavaParams = trim(q($[javaparams]));
$::gConfigurationName = "$[configname]";
$::gAdditionalOptions = "$[additionalcommands]";

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

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    my $websphere = WebSphere::WebSphere->new_simple($ec);

    %configuration = getConfiguration($ec, $::gConfigurationName);


    push(@args, '"'.$::gWSAdminAbsPath.'"');

    if($::gAdditionalOptions && $::gAdditionalOptions ne '') {
        push(@args, $::gAdditionalOptions);
    }

    my $file = 'publishWSDL_script.jython';
    $file = $websphere->write_jython_script(
        $file, {},
        augment_filename_with_random_numbers => 1,
        script => $ScriptFile
    );
    push(@args, '-f ' . $file);
    push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);

    if($::gClasspath && $::gClasspath ne '') {
        push(@args, '-wsadmin_classpath "' . $::gClasspath . '"');
    }

    my $connectionType = $configuration{conntype};
    if($connectionType && $connectionType ne '') {
        push(@args, '-conntype ' . $connectionType);
    }

    my $hostParamName;
    if ($connectionType eq IPC_CONNECTION_TYPE) {
        $hostParamName = '-ipchost';
    }
    else {
        $hostParamName = '-host';
    }
    if ($configuration{'websphere_url'} ne '') {
        push(@args, $hostParamName . ' ' . $configuration{'websphere_url'});
    }

    if ($configuration{'websphere_port'} ne '') {
        push(@args, '-port ' . $configuration{'websphere_port'});
    }

    if ($configuration{'user'} ne '') {
        push(@args, '-user ' . $configuration{'user'});
    }

    if ($configuration{'password'} ne '') {
        push(@args, '-password ' . $configuration{'password'});
    }

    if ($::gJavaParams && $::gJavaParams ne '') {
        foreach my $param (split(SEPARATOR_CHAR, $::gJavaParams)) {
            push(@args, "-javaoption $param");
        }
    }

    if ($::gCommands && $::gCommands ne '') {
        foreach my $command (split("\n", $::gCommands)) {
            push(@args, "-command $command");
        }
    }

    my $cmdLine = createCommandLine(\@args);
    my $escapedCmdLine = maskPassword($cmdLine, $configuration{'password'});

    $props{'publishWSDLLine'} = $escapedCmdLine;
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
            $ec ->setProperty("/myJobStep/outcome", 'warning');
        }
    }
    else {
        $ec->setProperty("/myJobStep/outcome", 'error');
    }
}

main();
 
1;
