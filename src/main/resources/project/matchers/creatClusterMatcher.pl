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
        id => "error1",
        pattern =>
q{WASL6046E: The system can not create the\s(.+)\sobject because it already exists in your configuration.},
        action =>
q{&addSimpleError("Error: Cluster with given name already exits.", "error");}
    },
    {
        id      => "error",
        pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
        action  => q{
                                      incValue("errors"); diagnostic("", "error", -1);
                                      &addSimpleError("Error: Error occurred while creating cluster.", "error");
                                   }
    },
    {
       id =>        "error2",
       pattern =>          q{is not recognized as an internal or external command|The system cannot find the path specified|sh: 1:\s(.+)\snot found},
       action =>           q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                        $::gProperties{"summary"} : '');
                  $description .= "Error: Can't find wsadmin path.";
                  setProperty("summary", $description . "\n");
                   incValue("errors"); diagnostic("", "error", -1);
                  }
    },
    {
        id => "clusterCreate",
        pattern =>
          '(.+)\(cells\/.+\/clusters\/.+\|cluster.xml#ServerCluster_.+',
        action => q{

                      my $description = ((defined $::gProperties{"summary"}) ?
                                             $::gProperties{"summary"} : '');

                                       $description = "Successfully created cluster : $1";

                                       setProperty("summary", $description . "\n");

                  }
    },
    {
        id => "memberCreate",
        pattern =>
          '(.+)\(cells\/.+\/clusters\/.+\|cluster.xml#ClusterMember_.+',
        action => q{
                     my $description = ((defined $::gProperties{"summary"}) ?
                                            $::gProperties{"summary"} : '');

                                            $description = "Successfully created cluster member: $1";

                                            setProperty("summary", $description . "\n");
                 }
    },
    {
        id      => "clusterStatus",
        pattern => q{Cluster\sstatus\s=\s(.+)},
        action  => q{

                         my $description = ((defined $::gProperties{"summary"}) ?
                               $::gProperties{"summary"} : '');

                         $description = "Cluster status : $1";

                         setProperty("summary", $description . "\n");

              }
    },
    {
        id      => "appdeploy",
        pattern => q{Application\s(.+)\sinstalled successfully.},
        action  => q{
                       my $description = ((defined $::gProperties{"summary"}) ?
                                              $::gProperties{"summary"} : '');

                                              $description .= "Application \'$1\'deployed successfully on cluster.";

                                              setProperty("summary", $description . "\n");
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
