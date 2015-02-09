# -------------------------------------------------------------------------
# File
#    startServer.pl
#
# Dependencies
#    None
#
# Copyright (c) 2014 Electric Cloud, Inc.
# All rights reserved
# -------------------------------------------------------------------------


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
  
  
  push(@args, '"'.$::gScriptLocation.'"');
  
  # if target: add to command string
  if($::gInstanceName && $::gInstanceName ne '') {
      push(@args, $::gInstanceName);
  }
  
  if($::gAdditionalCommands && $::gAdditionalCommands ne '') {
      push(@args, $::gAdditionalCommands);
  }

  my $cmdLine = createCommandLine(\@args);
  $props{'startServerLine'} = $cmdLine;
  setProperties($ec, \%props);

  print "WSAdmin command line: $cmdLine\n";

  #execute command
  my $content = `$cmdLine`;

  #print log
  print "$content\n";
  
  #evaluates if exit was successful to mark it as a success or fail the step
  if($? == SUCCESS){
   
      $ec->setProperty("/myJobStep/outcome", 'success');
      
  }else{
      $ec->setProperty("/myJobStep/outcome", 'error');
  }
 
}

main();
 
1;
