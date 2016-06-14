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
use Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(trim createCommandLine maskPassword setProperties getConfiguration
                 SUCCESS ERROR PLUGIN_NAME EXECUTABLE IPC_CONNECTION_TYPE
                 SEPARATOR_CHAR DEFAULT_WSADMIN_LANGUAGE WIN_IDENTIFIER TRUE FALSE);

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

    my %configToUse;
    my $pluginConfigs = new ElectricCommander::PropDB($ec,"/myProject/websphere_cfgs");
    my %configRow = $pluginConfigs->getRow($configName);

    # Check if configuration exists
    unless(keys(%configRow)) {
        print "Error: Configuration '$configName' doesn't exist\n";
        exit ERROR;
    }                                                                                                                                             

    # Get user/password out of credential
    my $xpath = $ec->getFullCredential($configRow{credential});
    $configToUse{'user'} = $xpath->findvalue("//userName");
    $configToUse{'password'} = $xpath->findvalue("//password");
      
    foreach my $c (keys %configRow) {
        #getting all values except the credential that was read previously
        if($c ne 'credential'){
            $configToUse{$c} = $configRow{$c};
        }
    }

    return %configToUse;
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
    
    #remove leading and trailing spaces
    $string =~ s/^\s+(.*?)\s+$/$1/;
    return $string;
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