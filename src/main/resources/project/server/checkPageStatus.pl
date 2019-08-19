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
use LWP::UserAgent;
use HTTP::Request;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Util;

use warnings;
use strict;
$|=1;

# -------------------------------------------------------------------------
# Constants
# -------------------------------------------------------------------------
use constant {
    #possible success criterias
    PAGE_FOUND_CRITERIA => 'pagefound',
    PAGE_NOT_FOUND_CRITERIA => 'pagenotfound',
    HTTP_PAGE_NOT_FOUND_CODE => '404',
    #error-only possible criterias
    SERVER_FAILURE_CRITERIA => 'serverfailure',
    UNEXPECTED_RESPONSE_CODE => 'unexpectedresponse',
    UNAUTHORIZED_RESPONSE_CODE_CRITERIA => 'unauthorizedresponse',
};

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gURL = '$[targeturl]';
$::gMaxElapsedTime = '$[maxelapsedtime]';
$::gSuccessCriteria = '$[successcriteria]';
# This is the _name_ of the attached credential parameter, not its value! This is used to get the actual credential user/password
$::gCredentialParam = "credentialName";
# This is the value of the attached credential parameter (the name of the user-attached credential), for log use only
$::gAttachedCredentialName = '$[credentialName]';
$::gIntervalWaitTime = 10;
$::gFailureCount = 0;

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
    # get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    # create args array
    my %props;
    my $url = $::gURL;
    my $user = '';
    my $pass = '';
    my %configuration;

    my $elapsedTime = 0;
    my $startTimeStamp = time;

    #checks if max elapsed time is default
    if ($::gMaxElapsedTime eq '') {
        $::gMaxElapsedTime = 0;
    }

    #check elapsed time is not negative
    if ($::gMaxElapsedTime < 0) {
        print 'Elapsed time cannot be a negative number. Enter a number greater or equal than zero.';
        exit ERROR;
    }

    #create all objects needed for response-request operations
    my $agent = LWP::UserAgent->new(env_proxy => 1,keep_alive => 1, timeout => 30);
    my $header = HTTP::Request->new(GET => $url);
    my $request = HTTP::Request->new('GET', $url, $header);

    #enter BASIC authentication
    if ($::gAttachedCredentialName && $::gAttachedCredentialName ne '') {
        print "Getting credential '$::gAttachedCredentialName' from parameter $::gCredentialParam\n";
        my @creds = getCredentialInfo($::gAttachedCredentialName);
        $user = $creds[0];
        $pass = $creds[1];
        $request->authorization_basic($user, $pass);
    }
    else {
        print "Authentication not needed\n";
    }

    #setting variables for iterating
    my $retries = 0;
    my $attempts = 0;
    my $continueFlag = 0;
    my $successCriteriaReached = FALSE;
    my $serverFailingFlag = FALSE;
    my $obtainedResult = '';

    do{
        $attempts++;
        print "----\nAttempt $attempts\n";

        #first attempt will always be done, no need to be forced to sleep
        if ($retries > 0) {
            my $testtimestart = time;
            #sleeping process during N seconds
            sleep $::gIntervalWaitTime;
            my $elapsedtesttime = time - $testtimestart;
            print "Elapsed interval time on attempt $attempts: $elapsedtesttime seconds\n"
        }

        #execute check
        my $response = $agent->request($request);
        my $httpCode = $response->code();

        #getting the status of the success criteria
        $successCriteriaReached = getSuccessCriteriaResult($response, \$obtainedResult, \$serverFailingFlag);

        #print "http code $httpCode. result: $obtainedResult\n";
        print "Status returned: Attempt $attempts -> ", $response->status_line(), "\n";
        print "HTTP code in attempt $attempts: $httpCode\n";
        print "Criteria reached: $successCriteriaReached\n";
        $elapsedTime = time - $startTimeStamp;
        print "Elapsed time so far: $elapsedTime seconds\n";
        $retries++;
        if ($successCriteriaReached == TRUE) {
            $continueFlag = FALSE;
        }
        else {
            #evaluate if loop has to be continued
            $continueFlag = keepCheckingServer($::gMaxElapsedTime, $successCriteriaReached, $elapsedTime, $obtainedResult, $httpCode);
        }
        print "\n";
    } while ($continueFlag == TRUE);

    # print stats
    print "\n---------------------------------\n";    
    printFinalStats($url, $attempts, $elapsedTime, $::gFailureCount);

    #set the result of the step
    my $analysisResult = determineAnalysisResult($serverFailingFlag, $successCriteriaReached, $::gSuccessCriteria, $obtainedResult);
    print "---------------------------------\n";
    $props{'url'} = $::gURL;
    $props{'successcriteria'} = $::gSuccessCriteria;
    $props{'result'} = $analysisResult;
    setProperties($ec, \%props);

}

sub getCredentialInfo($){
    my ($credentialName) = @_;

    my @creds = ();

	## get an EC object
	my $ec = new ElectricCommander();
	$ec->abortOnError(0);

    my $jobStepId = $ENV{"COMMANDER_JOBSTEPID"};
    # Note: when using an "attached credential parameter", this is the _name_ of the parameter not its value!
    # The credential itself needs to be _attached_by_the_user_ to the subprocedure step they created using this plugin procedure
    # The call will find and use the actual credential

    my $xPath = $ec->getFullCredential($credentialName, {jobStepId => $jobStepId });

    if (!defined $xPath) {
        my $msg = $ec->getError();
        printf "Error: retrieving credential $msg \n";
        exit ERROR;
    }

    # Get user and password from Credential
    my $user  = $xPath->findvalue('//credential/userName');
    my $pass  = $xPath->findvalue('//credential/password');
    # DEBUG ONLY -- DON'T PRINT THIS IN PRODUCTION USE !!
    # print "Obtaining credentials: $user / $pass\n";
    push(@creds, $user);
    push(@creds, $pass);

    return @creds;
}

########################################################################
# keepCheckingServer - determines if page should be checked or not, during a loop
#
# Arguments:
#   -maxElapsedTime: amount of seconds into which process times out
#   -successCriteriaReached: flag that indicates if the user specified success condition
#      is reached.
#   -elapsedTime: current spent amount of seconds
#   -obtainedResult: obtained result into the actual iteration
#
# Returns:
#   - 1 (or TRUE constant) if analysis must be continued
#   - 0 (or FALSE constant) if analysis is done and results must be printed
#
########################################################################  
sub keepCheckingServer($){
    #If entered max elapsed time is default or criteria is reached, 
    # evaluation is done.
    #If current elapsed time is lower than the maximum established 
    # by the user and criteria has not been reached, evaluation 
    # shall continue.
    #If current elapsed is equal or greater, than the maximum permitted. The
    # evaluation must be terminated.
    my ($maxElapsedTime, $successCriteriaReached, $elapsedTime, $obtainedResult, $httpCode) = @_;

    my $continueCondition;
    if ($::gMaxElapsedTime == 0 || $successCriteriaReached == TRUE) {
        $continueCondition = FALSE;
    }
    elsif ($elapsedTime < $::gMaxElapsedTime && $successCriteriaReached == FALSE) {
        #we are within the time frame and success has not being reached
        #we analyze if process must continue depending of the given error condition
        if ($obtainedResult eq SERVER_FAILURE_CRITERIA) {
            #if server is down, we would like to keep checking
            #server may be starting...
            $continueCondition = TRUE;
        }
        elsif ($obtainedResult eq UNAUTHORIZED_RESPONSE_CODE_CRITERIA) {
            #an HTTP 401 was received during the attempt of analysis.
            #analysis is signaled to stop.

            #Access not authorized, process must be terminated and error is reported.
            #This is an immediate authorization error.
            $continueCondition = FALSE;
        }
        elsif ($obtainedResult eq UNEXPECTED_RESPONSE_CODE) {
            #immediate unexpected result ERROR
            #another response code might be uncommon, so it is immediately reported
            #process must not continue
            $continueCondition = FALSE;
        }
        else {
            $continueCondition = TRUE;
        }
    }
    elsif ($elapsedTime >= $::gMaxElapsedTime) {
        $continueCondition = FALSE;
    }
    return $continueCondition;
}


########################################################################
# determineAnalysisResult - determines if criteria is reached and set success 
#        or error into the actual running step
#
# Arguments:
#   -serverFailingFlag: flag that indicates if server failed
#   -successCriteriaReached: flag that indicates if success criteria was met
#   -successCriteria: actual success criteria
#
# Returns:
#   -none
#
########################################################################  
sub determineAnalysisResult($){
    # get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    my $result;

    my ($serverFailingFlag, $successCriteriaReached, $successCriteria, $obtainedResult) = @_;
    if ($serverFailingFlag == FALSE) {
        if ($successCriteriaReached == TRUE) {
            if ($successCriteria eq PAGE_FOUND_CRITERIA) {
                print "\nRESULT: Criteria reached, page was found\n";
            }
            elsif ($successCriteria eq PAGE_NOT_FOUND_CRITERIA) {
                print "\nRESULT: Criteria reached, page was not found\n";
            }
            $ec->setProperty("/myJobStep/outcome", 'success');
            $result = 'success';
        }
        else {
            #signal an error in the outcome of the step
            $ec->setProperty("/myJobStep/outcome", 'error');
            #in this case success criteria was not achieved

            #we check first if an unexpected error occurred, or just criteria mismatch
            if ($obtainedResult eq UNEXPECTED_RESPONSE_CODE) {
                print "\nRESULT: Unexpected response code, check log for more details\n";
                $result = 'error';
            }
            elsif ($obtainedResult eq UNAUTHORIZED_RESPONSE_CODE_CRITERIA) {
                print "\nRESULT: Page access not authorized, check log for more details\n";
                $result = 'error';
            }
            else {
                #received an expected http code
                if ($successCriteria eq PAGE_FOUND_CRITERIA) {
                    print "\nRESULT: Criteria not reached, requested page was not found, check log for more details\n";
                    $result = 'failure';
                }
                elsif ($successCriteria eq PAGE_NOT_FOUND_CRITERIA) {
                    print "\nRESULT: Criteria not reached, requested page was found, check log for more details\n";
                    $result = 'failure';
                }
            }
        }
    }
    else {
        #server failed on the evaluation
        print "\nRESULT: ERROR: Server error occurred, check log for more details\n";
        $ec->setProperty("/myJobStep/outcome", 'error');
        $result = 'error';
    }
    return $result;
}


########################################################################
# printFinalStats - prints data of the results obtained after checking
#
# Arguments:
#   -url: actual checked URL
#   -attempts: number of attempts done
#   -failureCount: number of failures reported
#   -totalElapsedTime: elapsed time in seconds
#
# Returns:
#   -none
#
########################################################################    
sub printFinalStats($) {
    my ($url, $attempts, $elapsedTime, $failureCount) = @_;

    print "URL: $url\n";
    print "Attempts of connecting to the server: $attempts\n";
    print "Server failures: $failureCount out of $attempts attempts of connection\n";
    print "Total elapsed time: $elapsedTime seconds";
}

sub getSuccessCriteriaResult($) {
    my ($response, $obtainedResult, $serverFailingFlag) = @_;

    ${$serverFailingFlag} = FALSE;
    my $successCriteriaReached = FALSE;

    #init obtained result
    my $httpCode = $response->code();

    # Check the outcome of the response
    if ($response->is_success) {
        # get response code obtained
        ${$serverFailingFlag} = FALSE;
        # response was successful, page is available
        # a HTTP 200 could be returned in the most common scenario
        if ($httpCode =~ m/2\d\d/) {
            if($httpCode eq '200') {
                ${$obtainedResult} = PAGE_FOUND_CRITERIA;
            }
            else {
                ${$obtainedResult} = UNEXPECTED_RESPONSE_CODE;
            }
        }
        else {
            ${$obtainedResult} = UNEXPECTED_RESPONSE_CODE;
        }
    }
    elsif ($response->is_error) {
        # response was erroneus, either server doesn't exist, port 
        # is unavailable or server is overloaded.
        if ($httpCode =~ m/4\d\d/) {
            # returned an HTTP 4XX code
            ${$serverFailingFlag} = FALSE;

            if ($httpCode eq HTTP_PAGE_NOT_FOUND_CODE) {
                ${$obtainedResult} = PAGE_NOT_FOUND_CRITERIA;
            }
            else {
                # handle other 4xx codes
                if ($httpCode eq '401') {
                    #forbidden access to requested page
                    ${$obtainedResult} = UNAUTHORIZED_RESPONSE_CODE_CRITERIA;
                }
                else {
                    # another http 4xx response handling can be managed here
                    #everything else is an unexpected response code
                    ${$obtainedResult} = UNEXPECTED_RESPONSE_CODE;
                }
            }
        }
        elsif ($httpCode =~ m/5\d\d/) {
            # returned an HTTP 5XX code
            ${$obtainedResult} = SERVER_FAILURE_CRITERIA;
            ${$serverFailingFlag} = TRUE;
            $::gFailureCount++;
        }
        else {
            # todo: manage another http codes
        }
    }
    # print "http code $httpCode. result: ${$obtainedResult}\n";
    # does the expected criteria match the obtained criteria?
    if ($::gSuccessCriteria eq ${$obtainedResult}) {
        $successCriteriaReached = TRUE;
    }
    else {
        $successCriteriaReached = FALSE;
    }
    return $successCriteriaReached;
}

main();

1;
