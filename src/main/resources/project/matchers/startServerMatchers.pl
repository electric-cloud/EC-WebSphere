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


push (@::gMatchers,
  {
   id =>        "serverStarted",
   pattern =>          q{^ADMU3000I(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "Server started successfully.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
  {
   id =>        "serverAlreadyRunning",
   pattern =>          q{^ADMU3027E(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "An instance of the server may already be running.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
);

