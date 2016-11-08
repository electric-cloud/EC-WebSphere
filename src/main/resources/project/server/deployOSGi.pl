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

deployOSGi.pl - a perl library to that deploys the OSGi application.

=head1 SYNOPSIS

=head1 DESCRIPTION

a perl library to that deploys the OSGi application.This library optionally configures
external OSGi bundle library if the application to be deployed reference bundles that are stored in an external bundle repository.
This procedure can also add the bundles to internal bundle repository, if required.

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
$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gAddExternalRepo     = q($[addExternalRepo]);
$::gExternalRepoName    = trim(q($[externalRepoName]));
$::gExternalRepoURL     = trim(q($[externalRepoURL]));
$::gWSAdminAbsPath      = trim(q($[wsadminAbsPath]));
$::gLocalRepoBundleList = trim(q($[localRepoBundleList]));
$::gEbaPath             = trim(q($[ebaPath]));
$::gAppName             = trim(q($[appName]));
$::gDeployUnit          = trim(q($[deployUnit]));
$::gConfigurationName   = "$[configName]";

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
    my %configuration;
    my $ScriptFile;
    $::gEbaPath =~ s$\\$/$g;
    my $assetName = $::gEbaPath;
    my %props;
    #get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    my $websphere = WebSphere::WebSphere->new_simple($ec);

    %configuration = getConfiguration( $ec, $::gConfigurationName );

    push( @args, '"' . $::gWSAdminAbsPath . '"' );

    if ($::gAddExternalRepo) {
        ## If user wants to configure external bundle repository
        if ( $::gExternalRepoName eq '' ) {
            die "Error : External bundle repository name missing";
        }

        if ( $::gExternalRepoURL eq '' ) {
            die "Error : External bundle repository url missing";
        }

        $ScriptFile =
            "AdminTask.addExternalBundleRepository('[-name "
          . $::gExternalRepoName
          . " -url "
          . $::gExternalRepoURL
          . " ]')\n"
          . "AdminConfig.save()\n";
         $ScriptFile .= "print 'External bundle repository added successfully.'\n";
    }

    $::gLocalRepoBundleList =~ s$\\$/$g;
    my @localBundles = split( ",", $::gLocalRepoBundleList );

    foreach (@localBundles) {
        $ScriptFile .=
            "bundleToInstall = '"
          . $_ . "'\n"
          . "print 'Adding bundle ' + bundleToInstall + ' to internal bundle repository'\n"
          . "AdminTask.addLocalRepositoryBundle('[-file ' + bundleToInstall + ' ]')\n"
          . "AdminConfig.save()\n";
           $ScriptFile .= "print 'Added " . $_ . " to internal bundle repository successfully.'\n";
    }

    $ScriptFile .=
        "print 'Importing Asset:'\n"
      . 'AdminTask.importAsset(["-storageType", "FULL", "-source", "'
      . $::gEbaPath
      . "\"])\n"
      . "AdminConfig.save()\n"
      . "print 'Imported asset " . $::gEbaPath . " successfully.'\n";

    ## Asset name is derived by omitting file base path from full .eba file path.
    $assetName =~ s{.*/}{}g;    ## Remove base directory path

    $ScriptFile .=
        "bcm = AdminControl.queryMBeans('type=BundleCacheManager,*')[0]\n"
      . "bcmObjectName = bcm.getObjectName().toString()\n"
      . "downloadsComplete = AdminControl.invoke(bcmObjectName, 'areAllDownloadsComplete')\n"
      . "loopCount = 0\n"
      . "while downloadsComplete != 'true':\n"
      . "\tsleep(1)\n"
      . "\tdownloadsComplete = AdminControl.invoke(bcmObjectName, 'areAllDownloadsComplete')\n"
      . "\tloopCount += 1\n"
      . "\tif loopCount == 150:\n"
      . "\t\tprint 'Error : The bundle cache manager is indicating problems'\n"
      . "\t\tbundles = AdminControl.invoke(bcmObjectName, 'getAllBundles').splitlines()\n"
      . "\t\tfor bundleName in bundles:\n"
      . "\t\t\tprint bundleName + ' status: ' + AdminControl.invoke(bcmObjectName, 'getBundleDownloadState', bundleName)\n"
      . "\t\t\tbreak\n"
      . "print 'All bundles downloaded successfully.'\n"
      . "print 'Creating Business Level Application.'\n"
      . "AdminTask.createEmptyBLA(['-name','"
      . $::gAppName . "'])\n"
      . "AdminConfig.save()\n"
      . "print 'Created business level application " . $::gAppName . " successfully.'\n"
      . "print 'Creating Composition Unit.'\n"
      . "AdminTask.addCompUnit(['-blaID', 'WebSphere:blaname="
      . $::gAppName
      . "', '-cuSourceID', 'WebSphere:assetname="
      . $assetName
      . "','-MapTargets','[[ebaDeploymentUnit WebSphere:"
      . $::gDeployUnit
      . "]]'])\n"
      . "AdminConfig.save()\n"
      . "print 'Added asset " . $assetName . " to BLA " . $::gAppName . ".'\n"
      . "AdminNodeManagement.syncActiveNodes()\n"
      . "print '- Done'\n"
      . "AdminTask.startBLA(['-blaID', 'WebSphere:blaname="
      . $::gAppName . "'])\n"
      . "print 'Started application " . $::gAppName . " successfully.'\n";


    my $file = 'deployOSGi_script.jython';
    $file = $websphere->write_jython_script(
        $file, {},
        augment_filename_with_random_numbers => 1,
        script => $ScriptFile
    );
    push(@args, '-f ' . $file);
    push( @args, '-lang ' . DEFAULT_WSADMIN_LANGUAGE );

    my $connectionType         = $configuration{conntype};
    if ( $connectionType ne '' ) {
        push( @args, '-conntype ' . $connectionType );
    }

    my $hostParamName;

    if ( $connectionType eq IPC_CONNECTION_TYPE ) {
        $hostParamName = '-ipchost';
    }
    else {
        $hostParamName = '-host';
    }

    if ( $configuration{'websphere_url'} ne '' ) {
        push( @args, $hostParamName . ' ' . $configuration{'websphere_url'} );
    }

    if ( $configuration{'websphere_port'} ne '' ) {
        push( @args, '-port ' . $configuration{'websphere_port'} );
    }

    if ( $configuration{'user'} ne '' ) {
        push( @args, '-user ' . $configuration{'user'} );
    }

    if ( $configuration{'password'} ne '' ) {
        push( @args, '-password ' . $configuration{'password'} );
    }

    my $cmdLine = createCommandLine( \@args );
    my $escapedCmdLine = maskPassword( $cmdLine, $configuration{'password'} );

    $props{'configureEJBContainerLine'} = $escapedCmdLine;
    setProperties( $ec, \%props );

    print "WSAdmin command line: $escapedCmdLine\n";

    #execute command
    system($cmdLine);

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

