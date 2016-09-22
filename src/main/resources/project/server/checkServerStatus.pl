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
use URI;
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
    SERVER_IS_RESPONDING_CRITERIA => 'response',
    SERVER_IS_NOT_RESPONDING_CRITERIA => 'noresponse',
};


# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

$::gConfigName = "$[configname]";
$::gMaxElapsedTime = "$[maxelapsedtime]";
$::gSuccessCriteria = "$[successcriteria]";
$::gIntervalWaitTime = 10;

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------


########################################################################
# main - contains the whole process to be done by the plugin
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
  
  my $url = '';
  my $user = '';
  my $pass = '';
  my %configuration;
  
  my $elapsedTime = 0;
  my $startTimeStamp = time;
  
  #checks if max elapsed time is default
  if($::gMaxElapsedTime eq ''){
      $::gMaxElapsedTime = 0;
  }

  #check elapsed time is not negative
  if($::gMaxElapsedTime < 0){
   
      print 'Elapsed time cannot be a negative number. Enter a number greater or equal than zero.';
      exit ERROR;
      
  }
  
  #getting all info from the configuration, url, user and pass
  if($::gConfigName ne ''){
   
      #retrieve configuration hash
      %configuration = getConfiguration($ec, $::gConfigName);
      
      #insert into params the respective values by reference
      getDataFromConfig(\%configuration, \$url, \$user, \$pass);
      
  }

  #create all objects needed for response-request operations
  my $agent = LWP::UserAgent->new(env_proxy => 1,keep_alive => 1, timeout => 30);
  my $header = HTTP::Request->new(GET => $url);
  my $request = HTTP::Request->new('GET', $url, $header);
  #enter BASIC authentication
  $request->authorization_basic($user, $pass);
  
  #setting variables for iterating
  my $retries = 0;
  my $attempts = 0;
  my $continueFlag = 0;
  my $successCriteriaReached = FALSE;
  
  do{
      
      $attempts++;
      print "----\nAttempt $attempts\n";
      
      #first attempt will always be done, no need to be forced to sleep
      if($retries > 0){
         
         my $testtimestart = time;
         
         #sleeping process during N seconds
         sleep $::gIntervalWaitTime;
         
         my $elapsedtesttime = time - $testtimestart;
         
         print "Elapsed interval time on attempt $attempts: $elapsedtesttime seconds\n"
      }
      
      #execute check
      my $response = $agent->request($request);
      
      #init obtained result
      my $obtainedResult = '';
      
      # Check the outcome of the response
      if ($response->is_success){
          
          #response was successful, server is responding and is available
          #a HTTP 200 could be returned in the most common scenario
          $obtainedResult = SERVER_IS_RESPONDING_CRITERIA;
          
      }elsif ($response->is_error){
          
          #response was erroneus, either server doesn't exist, port is unavailable
          #or server is overloaded. A HTTP 5XX response code can be expected
          $obtainedResult = SERVER_IS_NOT_RESPONDING_CRITERIA;
          
      }
      
      
      print "Status returned: Attempt $attempts -> ", $response->status_line(), "\n";
      
      #get response code obtained
      my $httpCode = $response->code();
      
      print "HTTP code in attempt $attempts: $httpCode\n";
      
      #does the expected criteria match the obtained criteria?
      if($::gSuccessCriteria eq $obtainedResult){
          $successCriteriaReached = TRUE;
      }else{
          $successCriteriaReached = FALSE;
      }
      
      print "Criteria reached: ";
      
      if($successCriteriaReached == TRUE){
       
          print "True\n";
       
      }else{
         
          print "False\n";
          
      }
      
      $elapsedTime = time - $startTimeStamp;
      print "Elapsed time so far: $elapsedTime seconds\n";
      $retries++;
      
      #evaluate if loop has to be continued
      $continueFlag = keepChecking($successCriteriaReached, $elapsedTime);

      print "\n";
   
  }while($continueFlag == TRUE);
  
  #print stats
  print "\n---------------------------------\n";
  print "URL: $url\n";
  print "Attempts of connecting to the server: $attempts\n";
  print "Total elapsed time: $elapsedTime seconds";
  
  determineFinalResult($successCriteriaReached);
  
  print "---------------------------------\n";
  
  $props{'url'} = $url;
  
  setProperties($ec, \%props);
  
}

########################################################################
# keepChecking - determines if analysis must be continued or aborted
#
# Arguments:
#   -successCriteriaReached: indicates if the selected success criteria by
#          the user matches the criteria so far.
#   -elapsedTime: current analysis' elapsed time
#
# Returns:
#   -continueFlag: determines if process must continued or terminated
#                      (1 => continued. 0 => terminated)
#
#########################################################################  
sub keepChecking($){
 
    my ($successCriteriaReached, $elapsedTime) = @_;
    my $continueFlag;
    
    #If entered max elapsed time is default or criteria is reached, 
    # evaluation is done.
    #If current elapsed time is lower than the maximum established 
    # by the user and criteria has not been reached, evaluation 
    # shall continue.
    #If current elapsed is equal or greater, than the maximum permitted. The
    # evaluation must be terminated.
    if($::gMaxElapsedTime == 0 || $successCriteriaReached == TRUE){
     
        $continueFlag = FALSE;
     
    }elsif($elapsedTime < $::gMaxElapsedTime && $successCriteriaReached == FALSE){
       
        $continueFlag = TRUE;
     
    }elsif($elapsedTime >= $::gMaxElapsedTime){
     
        $continueFlag = FALSE;
       
    }
    #print "max time $::gMaxElapsedTime continue flag $continueFlag";
    
    return $continueFlag;
 
}

#########################################################################
# determineFinalResult - prints the final obtained result and sets the step's
#                              outcome
#
# Arguments:
#   -successCriteriaReached: indicates if the selected success criteria by
#          the user matches the criteria so far.
#
# Returns:
#   none
#
#########################################################################
sub determineFinalResult($){
 
    my ($successCriteriaReached) = @_;
    
    # get an EC object
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
    
    if($successCriteriaReached == TRUE){
        
        if($::gSuccessCriteria eq SERVER_IS_RESPONDING_CRITERIA){
         
            print "\nRESULT: Criteria reached, server is responding\n";
               
        }else{
         
            print "\nRESULT: Criteria reached, server is not responding\n";
               
        }
        $ec->setProperty("/myJobStep/outcome", 'success');
        
    }else{
     
        if($::gSuccessCriteria eq SERVER_IS_RESPONDING_CRITERIA){
         
            print "\nRESULT: Criteria not reached, server is not responding, check log for more details\n";
               
        }else{
         
            print "\nRESULT: Criteria not reached, server is responding, check log for more details\n";
               
        }
        
        $ec->setProperty("/myJobStep/outcome", 'error');
        
    }
 
}

#########################################################################
# getDataFromConfig - gets the data required from the config for this procedure
#                        and pass it by reference to the actual function's
#                        parameters.
#
# Arguments:
#   -configuration: hash containing the data from the config
#   -url: parameter that will receive the value of the URL, must be passed
#              as reference.
#   -user: config's user whose value is set in this function, must be passed
#              as reference.
#   -pass: config's password whose value is set in this function, must be passed
#              as reference.
#
# Returns:
#   none
#
#########################################################################  
sub getDataFromConfig($){
 
      my($configuration, $url, $user, $pass) = @_;

      my $uri;
      if($configuration->{'websphere_url'} && $configuration->{'websphere_url'} ne ''){
          $uri = URI->new($configuration->{'websphere_url'});

          unless($uri->scheme) {
            # oops, it seems we do not have a schema - so we will recreate uri with the default one
            # TODO maybe we need https in fact
            $uri = URI->new('http://' . $configuration->{'websphere_url'});
          }
      }else{
          print "Error: Could not get URL from configuration '$::gConfigName'\n";
          exit ERROR;
      }
			
      if($configuration->{'websphere_port'} && $configuration->{'websphere_port'} ne ''){
          my $port = $configuration->{'websphere_port'};
          $uri->port($port);
      }else{
          #print "Error: Could not get port from configuration '$::gConfigName'\n";
          #exit ERROR;
      }

      ${$url} = $uri->as_string;
      
      if($configuration->{'user'} && $configuration->{'user'} ne ''){
          ${$user} = $configuration->{'user'};
      }else{
          #print "Error: Could not get user from configuration '$::gConfigName'\n";
          #exit ERROR;
      }
      
      if($configuration->{'password'} && $configuration->{'password'} ne ''){
          ${$pass} = $configuration->{'password'};
      }else{
          #print "Error: Could not get password from configuration $::gConfigName'\n";
          #exit ERROR;
      }
 
}

main();
 
1;
