# -------------------------------------------------------------------------
# File
#    runCustomJob.pl
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

$::gWSAdminAbsPath = trim(q($[wsadminabspath]));
$::gConfigurationName = "$[configname]";
$::gAppName = "$[appname]";
$::gScriptFile = "if AdminApp.isAppReady('$::gAppName'):
  print \"The application is ready.\"
  sys.exit(0)
else:
  print \"The application is not ready.\"
  sys.exit(1)";
  
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
  
  my %configuration;
  
  #get an EC object
  my $ec = new ElectricCommander();
  $ec->abortOnError(0);
  
  if($::gConfigurationName ne ''){
      %configuration = getConfiguration($ec, $::gConfigurationName);
  }
  
  push(@args, '"'.$::gWSAdminAbsPath.'"');

  open (MYFILE, '>>checkapp_script.jython');
  print MYFILE "$::gScriptFile";
  close (MYFILE);

  push(@args, '-f "checkapp_script.jython"');
  push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);
  
  push(@args, '-conntype SOAP');
  
  #inject config...
  if(%configuration){
      
      my $hostParamName;
      
      if($configuration{'websphere_url'} ne ''){
          push(@args, '-host ' . $configuration{'websphere_url'});
      }
      
      if($configuration{'websphere_port'} ne ''){
          push(@args, '-port ' . $configuration{'websphere_port'});
      }
      
      if($configuration{'user'} ne ''){
          push(@args, '-user ' . $configuration{'user'});
      }
      
      if($configuration{'password'} ne ''){
          push(@args, '-password ' . $configuration{'password'});
      }
  }
  
  my $cmdLine = createCommandLine(\@args);
  my $escapedCmdLine = maskPassword($cmdLine, $configuration{'password'});
  
  $props{'checkAppLine'} = $escapedCmdLine;
  setProperties($ec, \%props);
  
  print "WSAdmin command line: $escapedCmdLine\n";
  
  #execute command
  my $content = `$cmdLine`;
  
  #print log
  print "$content\n";
  
  #evaluates if exit was successful to mark it as a success or fail the step
  if($? == SUCCESS){
   
      $ec->setProperty("/myJobStep/outcome", 'success');
      
      #set any additional error or warning conditions here
      #there may be cases that an error occurs and the exit code is 0.
      #we want to set to correct outcome for the running step
      if($content =~ m/(The application is not ready)/){
          $ec->setProperty("/myJobStep/outcome", 'error');
      }elsif($content !~ m/(The application is ready|The application is not ready)/){
          print "Error: cannot determine application status\n";
          $ec->setProperty("/myJobStep/outcome", 'error');
      }
  }else{
      $ec->setProperty("/myJobStep/outcome", 'error');
  }
  
}

main();
 
1;
