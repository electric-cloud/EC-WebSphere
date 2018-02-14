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
$|=1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gCommands = q($[commands]);
$::gScriptFileSource = trim(q($[scriptfilesource]));
$::gScriptFile = trim(q($[scriptfile]));
$::gScriptFileAbsPath = trim(q($[scriptfileabspath]));
$::gWSAdminAbsPath = trim(q($[wsadminabspath]));
$::gClasspath = trim(q($[classpath]));
$::gJavaParams = trim(q($[javaparams]));
$::gConfigurationName = "$[configname]";
$::gAdditionalOptions = "$[additionalcommands]";

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
    my $actualOperativeSystem = $^O;
    # my $fixedLocation = $::gWSAdminAbsPath;
    my %configuration;

    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    # my $websphere = WebSphere::WebSphere->new_simple($ec);
    my $websphere = WebSphere::WebSphere->new(
        $ec,
        $::gConfigurationName,
        $::gWSAdminAbsPath
    );

    my $fixedLocation = $websphere->getWSAdminPath();
    if ($::gConfigurationName ne '') {
        %configuration = getConfiguration($ec, $::gConfigurationName);
    }

    push (@args, '"'.$fixedLocation.'"');

    if ($::gAdditionalOptions && $::gAdditionalOptions ne '') {
        push(@args, $::gAdditionalOptions);
    }

    if ($::gScriptFileSource eq 'suppliedfile') {
        if ($::gScriptFileAbsPath && $::gScriptFileAbsPath ne '') {
            push(@args, '-f "' . $::gScriptFileAbsPath . '"');
            push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);
        }
    }
    elsif ($::gScriptFileSource eq 'newscriptfile') {
        my $file = 'generated_script.jython';
        $file = $websphere->write_jython_script(
            $file, {},
            augment_filename_with_random_numbers => 1,
            script => $::gScriptFile
        );
        push(@args, '-f ' . $file);
        push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);
    }

    if($::gClasspath && $::gClasspath ne '') {
        push(@args, '-wsadmin_classpath "' . $::gClasspath . '"');
    }

    my $connectionType    = $configuration{conntype};
    if($connectionType && $connectionType ne '') {
        push(@args, '-conntype ' . $connectionType);
    }

    #inject config...
    if (%configuration) {
        my $hostParamName;
        if ($connectionType eq IPC_CONNECTION_TYPE){
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
    }
    if ($::gJavaParams && $::gJavaParams ne '') {
        foreach my $param (split(SEPARATOR_CHAR, $::gJavaParams)) {
            push(@args, "-javaoption $param");
        }
    }
    if ($::gCommands && $::gCommands ne '') {
        foreach my $command (split("\n", $::gCommands)) {
            push(@args, '-command ' . $command);
        }
    }

    my $cmdLine = createCommandLine(\@args);
    my $escapedCmdLine = maskPassword($cmdLine, $configuration{'password'});

    $props{'runCustomJob'} = $escapedCmdLine;
    setProperties($ec, \%props);
    print "WSAdmin command line: $escapedCmdLine\n";
    #execute command
    my $content = `$cmdLine`;

    #print log
    print "$content\n";

    #evaluates if exit was successful to mark it as a success or fail the step
    if ($? == SUCCESS) {
        $ec->setProperty("/myJobStep/outcome", 'success');
        # set any additional error or warning conditions here
        # there may be cases that an error occurs and the exit code is 0.
        # we want to set to correct outcome for the running step
        if ($content =~ m/WSVR0028I:/) {
            # license expired warning
            $ec->setProperty("/myJobStep/outcome", 'warning');
      }
    }
    else {
        $ec->setProperty("/myJobStep/outcome", 'error');
    }
}

main();

1;
