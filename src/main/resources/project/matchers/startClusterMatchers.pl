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


push(
    @::gMatchers,
    {
        id      => "error1",
        pattern => q{is not recognized as an internal or external command|sh: 1:\s(.+)\snot found},
        action =>
          q{&addSimpleError("Error: Can't find wsadmin path", "error");},
    },
    {
        id      => "error2",
        pattern => q{Error :\s(.+)\s(.+)\sdoes not exist.},
        action  => q{&addSimpleError("Error: $1 $2 does not exist.", "error");},
    },
    {
        id      => "error3",
        pattern => q{The system cannot find the path specified},
        action =>
          q{&addSimpleError("Error: Can't find wsadmin path", "error");},
    },
    {
        id      => "error4",
        pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
        action  => q{
                           incValue("errors"); diagnostic("", "error", -1);
                           &addSimpleError("Error: Error occurred while starting cluster.", "error");
                      }
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
