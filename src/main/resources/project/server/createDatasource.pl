# -------------------------------------------------------------------------
# File
#    createDatasource.pl
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

#-------------------------------------------------------------------------
# Variables
#-------------------------------------------------------------------------

#get an EC object
my $ec = new ElectricCommander();
$ec->abortOnError(0);

$::gWSAdminAbsPath = ($ec->getProperty("wsadminabspath") )->findvalue("//value");
$::gJdbcProvider = ($ec->getProperty("jdbcProvider") )->findvalue("//value");
$::gAuthAliasName = ($ec->getProperty("authAliasName") )->findvalue("//value");
$::gDatasourceName = ($ec->getProperty("datasourceName") )->findvalue("//value");
$::gDatasourceDescription = ($ec->getProperty("datasourceDescription") )->findvalue("//value");
$::gDatasourceJNDIName = ($ec->getProperty("datasourceJNDIName") )->findvalue("//value");
$::gStatementCacheSize = ($ec->getProperty("statementCacheSize") )->findvalue("//value"); 
$::gDatasourceHelperClassname = ($ec->getProperty("datasourceHelperClassname") )->findvalue("//value"); 
$::gConfigurationName = ($ec->getProperty("configname") )->findvalue("//value");
$::gConnectionType = ($ec->getProperty("connectiontype") )->findvalue("//value");
		   	

#-------------------------------------------------------------------------
# Main functions
#-------------------------------------------------------------------------


#######################################################################
# main - contains the whole process to be done by the plugin, it builds 
#        the command line, sets the properties and the working directory
#
# Arguments:
#   none
#
# Returns:
#   none
#
######################################################################
sub main() {
  
  # create args array
  my @args = ();
  my %props;
  my %configuration;

  if($::gConfigurationName ne ''){
      %configuration = getConfiguration($ec, $::gConfigurationName);
  }

  push(@args, '"'.$::gWSAdminAbsPath.'"');


  my $ScriptFile = 'newjdbc = AdminConfig.getid(\'/JDBCProvider:' . $::gJdbcProvider . '/\')
mapping = []
mapping.append( [ \'authDataAlias\', \'' . $::gAuthAliasName .'\' ] )
mapping.append( [ \'mappingConfigAlias\', \'DefaultPrincipalMapping\' ] )
attrs = []
attrs.append( [ \'name\', \'' . $::gDatasourceName . '\' ] )	
attrs.append( [ \'description\', \'' . $::gDatasourceDescription . '\' ] )
attrs.append( [ \'jndiName\', \'' . $::gDatasourceJNDIName . '\' ] )
attrs.append( [ \'statementCacheSize\', ' . $::gStatementCacheSize . ' ] )
attrs.append( [ \'authDataAlias\', \'' . $::gAuthAliasName . '\' ] )
attrs.append( [ \'datasourceHelperClassname\', \'' . $::gDatasourceHelperClassname . '\' ] )

newds = AdminConfig.create(\'DataSource\', newjdbc, attrs)

ds_props = AdminConfig.create(\'J2EEResourcePropertySet\', newds, [])

AdminConfig.save()';
	
  open (MYFILE, '>>createDS_script.jython');
  
  print MYFILE "$ScriptFile";
  close (MYFILE);
      
  push(@args, '-f createDS_script.jython');
  push(@args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE);
  push(@args, '-conntype ' . $::gConnectionType);
  
  	
  #inject config...
  if(%configuration){
      
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
  
  $props{'createDatasourceLine'} = $escapedCmdLine;
  setProperties($ec, \%props);
  
  print "WSAdmin command line: $escapedCmdLine\n";

  #execute command
  my $content = `$cmdLine`;
  
  #print log
  print "$content\n";
}

main();
 
1;
