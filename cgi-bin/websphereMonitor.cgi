#!/bin/sh

exec "$COMMANDER_HOME/bin/ec-perl" -x "$0" "${@}"

#!perl

################################
# monitorJob.cgi
#
# Monitors a job: waits for it to complete and reports on its success or
# failure.
#
################################
use warnings;
use strict;
use ElectricCommander;
use XML::XPath;
use CGI;

use constant {
    SUCCESS => 0,
    ERROR   => 1,
};

my $gTimeout = 20;

################################
# main - Main program for the application.
#
# Arguments:
#   -
#
# Returns:
#   -
#
################################
sub main {

    # Get CGI args
    my $cgi     = CGI->new();
    my $cgiArgs = $cgi->Vars;

    # Check for required args
    my $jobId = $cgiArgs->{jobId};
    if (!defined $jobId || "$jobId" eq "") {
        reportError($cgi, "jobId is a required parameter");
    }

    # Wait for job
    my $ec = ElectricCommander->new({abortOnError => 0});
    $ec->abortOnError(0);
    my $xpath = $ec->waitForJob($jobId, $gTimeout);
    my $errors = $ec->checkAllErrors($xpath);

    if ("$errors" ne "") {
        reportError($cgi, $errors);
    }

    my $status = $xpath->findvalue("//status");
    if ("$status" ne "completed") {

        # Abort job and report failure
        abortJobAndReportError($cgi, $ec, $jobId);
    }

    my $outcome = $xpath->findvalue("//outcome");
    if ("$outcome" ne "success") {

        # Report job errors
        reportJobErrors($cgi, $ec, $jobId);
    }

    # <!-- This behaviour was copied from the ServiceNowMonitor.cgi but commented out due to the FLOWPLUGIN-7918 -->
    # # If the job was successful and the debug flag is not set, delete it
    # my $debug = $cgiArgs->{debug};
    # if (!defined $debug || "$debug" ne "1") {
    #   $ec->deleteJob($jobId);
    # }

    # Report the job's success
    reportSuccess($cgi);
} ## end sub main

################################
# abortJobAndReportError - Abort the job and report the timeout error.
#
# Arguments:
#   cgi
#   ec    - ElectricCommander instance
#   jobId - int identifier for the job
#
# Returns:
#   -
#
################################
sub abortJobAndReportError($$$) {
    my ($cgi, $ec, $jobId) = @_;

    my $errMsg = "Aborting job after reaching timeout";

    # Try to abort the job
    my $xpath  = $ec->abortJob($jobId);
    my $errors = $ec->checkAllErrors($xpath);
    if ("$errors" ne '') {
        reportError($cgi, $errMsg . "\n" . $errors);
    }

    # Wait for the job to finish aborting
    $xpath = $ec->waitForJob($jobId, $gTimeout);
    $errors = $ec->checkAllErrors($xpath);
    if ("$errors" ne "") {
        reportError($cgi, $errMsg . "\n" . $errors);
    }

    # Check to see if the job actually aborted
    my $status = $xpath->findvalue("//status");
    if ("$status" ne "completed") {
        reportError($cgi, $errMsg . "\nJob still running after abort");
    }

    reportError($cgi, $errMsg . "\nJob successfully aborted");
} ## end sub abortJobAndReportError($$$)

################################
# reportJobErrors - Look for errors in the job to report.
#
# Arguments:
#   cgi
#   ec    - ElectricCommander instance
#   jobId - int identifier for the job
#
# Returns:
#   -
#
################################
sub reportJobErrors($$$) {
    my ($cgi, $ec, $jobId) = @_;

    # Get job details
    my $xpath         = $ec->getJobDetails($jobId);
    my $procedureName = eval {$xpath->findvalue('//job/procedureName')->string_value();};
    my $errors        = $ec->checkAllErrors($xpath);
    if ("$errors" ne "") {
        reportError($cgi, $errors);
    }

    # Look for configError first
    my $configError = $xpath->findvalue("//job/propertySheet/property[propertyName='configError']/value");
    if (defined $configError && "$configError" ne "") {
        reportError($cgi, $configError)
    }

    # Find the first error message and report it
    my @errorMessages = $xpath->findnodes("//errorMessage");
    if (@errorMessages > 0) {
        my $firstMessage = $errorMessages[0]->string_value();
        reportError($cgi, $firstMessage);
    }

    # Report a generic error message if we couldn't find a specific one on the
    # job
    if ($procedureName && ($procedureName eq 'EditConfiguration')) {
        reportError($cgi, "Edit configuration failed");
    }
    else {
        reportError($cgi, "Configuration creation failed");
    }
} ## end sub reportJobErrors($$$)

################################
# reportError - Print the error message and exit.
#
# Arguments:
#   cgi
#   error - string to print
#
# Returns:
#   0 - Success
#   1 - Error
#
################################
sub reportError($$) {
    my ($cgi, $error) = @_;

    print $cgi->header("text/html");
    print $error;
    exit ERROR;
}

################################
# reportSuccess - Report success.
#
# Arguments:
#   cgi
#
# Returns:
#   -
#
################################
sub reportSuccess($) {
    my ($cgi) = @_;

    print $cgi->header("text/html");
    print "Success";
}

# ------------------------------------------------------------------------
# validateUserSession
#
#      Check current session on valid, if not - redirects
#      user to the login page.
# ------------------------------------------------------------------------
sub validateUserSession() {

    open my $fh, ">/tmp/log" or die $!;
    print $fh "here";
    my $ec = new ElectricCommander({abortOnError => 0});
    $ec->login();
    print $fh 'login';
    if($ec->getError()) {
        print "Location: ../\n\n";
        exit 0;
    }
}

validateUserSession();

main();
exit SUCCESS;
