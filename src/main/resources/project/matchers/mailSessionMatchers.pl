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
     id =>        "error2",
     pattern =>          q{WASX7077E: Incomplete config id: need closing parenthesis in},
     action =>           q{

                my $description = ((defined $::gProperties{"summary"}) ?
                      $::gProperties{"summary"} : '');

                $description .= "Error: Invalid scope defined.";

                setProperty("summary", $description . "\n");

   },
  },
  {
     id =>        "success",
     pattern =>          q{Mail session\s(.+)\screated successfully.},
     action =>           q{
                my $description = ((defined $::gProperties{"summary"}) ?
                      $::gProperties{"summary"} : '');
                $description .= "Mail session $1 created successfully.";
                setProperty("summary", $description . "\n");
     },
  },
  {
          id      => "error2",
          pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
          action  => q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Error: Error occurred while creating mail session.";
                    setProperty("summary", $description . "\n");
                    incValue("errors"); diagnostic("", "error", -1);
                      }
  }
);