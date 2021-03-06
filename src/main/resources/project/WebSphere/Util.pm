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

Common functions for EC-WebSphere plugin

=head1 DESCRIPTION

Common functions for EC-WebSphere plugin

Copyright (c) 2014 Electric Cloud, Inc.
All rights reserved
=cut

package WebSphere::Util;

use ElectricCommander::PropDB;

use strict;
use warnings;

BEGIN {
    require ElectricCommander;
    import ElectricCommander;

    my $ec = ElectricCommander->new();
    my @locations = (
        '/myProject/pdk/',
        # '/myProject/perl/core/lib/',
        # '/myProject/perl/lib/'
    );
    my $display;
    my $pdk_loader = sub {
        my ($self, $target) = @_;

        $display = '[EC]@PLUGIN_KEY@-@PLUGIN_VERSION@/' . $target;
        # Undo perl'd require transformation
        # Retrieving framework part and lib part.
        my $code;
        for my $prefix (@locations) {
            my $prop = $target;
            # $prop =~ s#\.pm$##;

            $prop = "$prefix$prop";
            $code = eval {
                $ec->getProperty("$prop")->findvalue('//value')->string_value;
            };
            last if $code;
        }
        return unless $code; # let other module paths try ;)

        # Prepend comment for correct error attribution
        $code = qq{# line 1 "$display"\n$code};

        # We must return a file in perl < 5.10, in 5.10+ just return \$code
        #    would suffice.
        open my $fd, "<", \$code
            or die "Redirect failed when loading $target from $display";

        return $fd;
    };

    push @INC, $pdk_loader;
}
use FlowPDF;
use FlowPDF::Context;
use FlowPDF::ContextFactory;
use Exporter;
use Try::Tiny;
use Data::Dumper;
our @ISA = qw(Exporter);
our @EXPORT = qw(
    trim
    rtrim
    createCommandLine
    maskPassword
    setProperties
    getConfiguration
    SUCCESS
    ERROR
    PLUGIN_NAME
    EXECUTABLE
    IPC_CONNECTION_TYPE
    SEPARATOR_CHAR
    DEFAULT_WSADMIN_LANGUAGE
    WIN_IDENTIFIER
    TRUE
    FALSE
);

# -------------------------------------------------------------------------
# Constants
# -------------------------------------------------------------------------
use constant {
    SUCCESS => 0,
    ERROR   => 1,

    TRUE => 1,
    FALSE => 0,

    PLUGIN_NAME => 'EC-WebSphere',
    EXECUTABLE => 'wsadmin',
    IPC_CONNECTION_TYPE => 'IPC',
    SEPARATOR_CHAR => ';',
    DEFAULT_WSADMIN_LANGUAGE => 'jython',
    WIN_IDENTIFIER => 'MSWin32',

};


##########################################################################
# getConfiguration - get the information of the configuration given
#
# Arguments:
#   -ec: Commander instance
#   -configName: name of the configuration to retrieve
#
# Returns:
#   -configToUse: hash containing the configuration information
#
#########################################################################
sub getConfiguration {
    my ($ec, $configName) = @_;

    ### FlowPDF part. Detecting procedure name and step name:
    my $procedureName = $ec->getProperty('/myProcedure/procedureName')->findvalue('//value')->string_value();
    my $stepName = $ec->getProperty('/myJobStep/stepName')->findvalue('//value')->string_value();
    ###
    my $flowpdf = FlowPDF->new({
        pluginName      => '@PLUGIN_KEY@',
        pluginVersion   => '@PLUGIN_VERSION@',
        configFields    => ['config', 'config_name', 'configurationName', 'configName', 'configname'],
        configLocations => ['websphere_cfgs', 'ec_plugin_cfgs'],
        contextFactory  => FlowPDF::ContextFactory->new({
            procedureName => $procedureName,
            stepName      => $stepName
        })
    });

    my $cfg = undef;
    try {
        $cfg = $flowpdf->getContext()->getConfigValuesAsHashref();
    } catch {
        my ($e) = @_;

        unless (ref $e) {
            print "Error: Configuration '$configName' doesn't exist\n";
            exit ERROR;
        }
        my $err_msg  = $e->getMessage();
        if ($e->is('FlowPDF::Exception::ConfigDoesNotExist')) {
            if ($err_msg =~ m/Configuration\s'(.*?)'/s) {
                $err_msg = qq|Configuration "$1" does not exist|;
            }
        }
        print "Error: Configuration '$configName' doesn't exist\n";
        exit ERROR;
    };
    my %configuration = %$cfg;
    $configuration{configurationName} = $configName;
    if (wantarray()) {
        return %configuration;
    }
    return \%configuration;


    ### End of flowpdf part
    # my %configToUse;
    # my $pluginConfigs = new ElectricCommander::PropDB($ec,"/myProject/websphere_cfgs");
    # my %configRow = $pluginConfigs->getRow($configName);

    # # Check if configuration exists
    # unless(keys(%configRow)) {
    #     print "Error: Configuration '$configName' doesn't exist\n";
    #     exit ERROR;
    # }

    # # Get user/password out of credential
    # my $xpath = $ec->getFullCredential($configRow{credential});
    # $configToUse{'user'} = $xpath->findvalue("//userName");
    # $configToUse{'password'} = $xpath->findvalue("//password");

    # foreach my $c (keys %configRow) {
    #     #getting all values except the credential that was read previously
    #     if($c ne 'credential'){
    #         $configToUse{$c} = $configRow{$c};
    #     }
    # }

    # return %configToUse;
}


########################################################################
# trim - deletes blank spaces before and after the entered value in
# the argument
#
# Arguments:
#   -untrimmedString: string that will be trimmed
#
# Returns:
#   trimmed string
#
#########################################################################
sub trim {
    my ($string) = @_;

    # remove leading and trailing spaces
    # $string =~ s/^\s+(.*?)\s+$/$1/;
    $string =~ s/^\s+//gs;
    $string =~ s/\s+$//gs;
    return $string;
}

# same as trim, but trims value by reference.
sub rtrim {
    for (my $i = 0; $i < scalar @_; $i++) {
        $_[$i] =~ s/^\s+//gs;
        $_[$i] =~ s/\s+$//gs;
    }
}


########################################################################
# createCommandLine - creates the command line for the invocation
# of the program to be executed.
#
# Arguments:
#   -arr: array containing the command name (must be the first element)
#         and the arguments entered by the user in the UI
#
# Returns:
#   -the command line to be executed by the plugin
#
########################################################################
sub createCommandLine {
  my ($arr) = @_;

  my $commandName = shift(@$arr);
  my $command = $commandName;

  foreach my $elem (@$arr) {
      $command .= " $elem";
  }

  return $command;
}

sub maskPassword {
    my ($line, $password) = @_;
    return $line unless defined $password && length($password);

    $line =~ s/-password $password/-password ****/;
    return $line;
}

########################################################################
# setProperties - set a group of properties into the Electric Commander
#
# Arguments:
#   -ec:       commander instance
#   -propHash: hash containing the ID and the value of the properties
#              to be written into the Electric Commander
#
# Returns:
#   none
#
########################################################################
sub setProperties {
    my ($ec, $propHash) = @_;

    foreach my $key (keys % $propHash) {
        my $val = $propHash->{$key};
        $ec->setProperty("/myCall/$key", $val);
    }
}

1;
