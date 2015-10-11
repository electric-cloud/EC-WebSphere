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
              &addSimpleError("Error: Can't find wsadmin path", "error");
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
          id      => "error2",
          pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
          action  => q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Error: Error occurred while updating application.";
                    setProperty("summary", $description . "\n");
                    incValue("errors"); diagnostic("", "error", -1);
                      }
  },
  {
       id =>        "success",
       pattern =>          q{Update of\s(.+)\shas ended.},
       action =>           q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                        $::gProperties{"summary"} : '');
                  $description = "Application $1 updated.";
                  setProperty("summary", $description . "\n");
       },
  },
  {
        id =>        "success1",
        pattern =>          q{Is App Ready =\s(.+)},
        action =>           q{
                   my $description = ((defined $::gProperties{"summary"}) ?
                         $::gProperties{"summary"} : '');
                   $description = "Is application ready to start = $1";
                   setProperty("summary", $description . "\n");
        },
  },
  {
         id =>        "success2",
         pattern =>          q{The application is ready to restart.},
         action =>           q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Restarting the application.";
                    setProperty("summary", $description . "\n");
         },
  },
  {
      id =>        "success3",
      pattern =>          q{Application is UP!},
      action =>           q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                       $::gProperties{"summary"} : '');
                 $description = "Application is updated and started successfully.";
                 setProperty("summary", $description . "\n");
      },
  }
);

sub addSimpleError {
    my ( $customError, $type ) = @_;
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);

    setProperty( "summary", $customError );
    if ( $type eq "success" ) {
        $ec->setProperty( "/myJobStep/outcome", 'success' );
    }
    elsif ( $type eq "error" ) {
        $ec->setProperty( "/myJobStep/outcome", 'error' );
    }
    else {
        $ec->setProperty( "/myJobStep/outcome", 'warning' );
    }
}