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

$::gCommands = q($[commands]);
$::gAppName = trim(q($[appname]));
$::gScriptFile = 'appmgr = AdminControl.queryNames(\'name=ApplicationManager,*\')' . "\n" .
             'AdminControl.invoke(appmgr,\'stopApplication\',\'' . $::gAppName . '\')';
$::gWSAdminAbsPath = trim(q($[wsadminabspath]));
$::gClasspath = trim(q($[classpath]));
$::gConnectionType = trim(q($[connectiontype]));
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
  my $fixedLocation = $::gWSAdminAbsPath;
  my %configuration;
  
  #get an EC object
  my $ec = new ElectricCommander();
  $ec->abortOnError(0);
  
  if($::gConfigurationName ne ''){
      %configuration = getConfiguration($ec, $::gConfigurationName);
  }
  
  push(@args, '"'.$fixedLocation.'"');

  if($::gAdditionalOptions && $::gAdditionalOptions ne '') {
      push(@args, $::gAdditionalOptions);
  }

  open (MYFILE, '>>stopapp_script.jython');    
print MYFILE "$::gScriptFile";
close (MYFILE);
      
push(@args, '-f "stopapp_script.jython"');
push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);

  if($::gClasspath && $::gClasspath ne '') {
      push(@args, '-wsadmin_classpath "' . $::gClasspath . '"');
  }
  
  if($::gConnectionType && $::gConnectionType ne '') {
      push(@args, '-conntype ' . $::gConnectionType);
  }
  
  #inject config...
  if(%configuration){
      
      my $hostParamName;
      
      if($::gConnectionType eq IPC_CONNECTION_TYPE){
         $hostParamName = '-ipchost';
      }else{         
         $hostParamName = '-host';
      }
      
      if($configuration{'websphere_url'} ne ''){
          push(@args, $hostParamName . ' ' . $configuration{'websphere_url'});
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
  
  if($::gJavaParams && $::gJavaParams ne '') {
      foreach my $param (split(SEPARATOR_CHAR, $::gJavaParams)) {
          push(@args, "-javaoption $param");
      }
  }
  
  if($::gCommands && $::gCommands ne '') {
      foreach my $command (split("\n", $::gCommands)) {
          push(@args, "-command $command");
      }
  }

  my $cmdLine = createCommandLine(\@args);
  my $escapedCmdLine = maskPassword($cmdLine, $configuration{'password'});
  
  $props{'stopAppLine'} = $escapedCmdLine;
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
      if($content =~ m/WSVR0028I:/){
          #license expired warning
          $ec->setProperty("/myJobStep/outcome", 'warning');
      }
      
  }else{
      $ec->setProperty("/myJobStep/outcome", 'error');
  }

}

main();
 
1;
