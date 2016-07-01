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
   id =>        "error1",
   pattern =>          q{is not recognized as an internal or external command|The system cannot find the path specified|sh: 1:\s(.+)\snot found},
   action =>           q{

              my $description = ((defined $::gProperties{"summary"}) ?
                    $::gProperties{"summary"} : '');

              $description .= "Error: Can't find wsadmin path.";

              setProperty("summary", $description . "\n");

   },
  },
  {
           id      => "error3",
           pattern => q{WSWS0047E: Cannot find any WSDL files to publish.},
           action  => q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                           $::gProperties{"summary"} : '');
                     $description = "Error: Invalid location to store WSDL files.Give filename along with path where WSDL file should get stored.";
                     setProperty("summary", $description . "\n");
                     incValue("errors"); diagnostic("", "error", -1);
                       }
  },
  {
     id =>        "success",
     pattern =>          q{WSDL files for\s(.+)\spublished successfully},
     action =>           q{
                my $description = ((defined $::gProperties{"summary"}) ?
                      $::gProperties{"summary"} : '');
                $description .= "WSDL files for $1 published successfully.";
                setProperty("summary", $description . "\n");
     },
  },
  {
          id      => "error2",
          pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
          action  => q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Error: Error occurred while publishing WSDL files.";
                    setProperty("summary", $description . "\n");
                    incValue("errors"); diagnostic("", "error", -1);
                      }
  }
);