push(
    @::gMatchers,
    {
        id      => "error1",
        pattern => q{is not recognized as an internal or external command|sh: 1:\s(.+)\snot found},
        action  => q{
                        incValue("errors"); diagnostic("", "error", -1);
                        &addSimpleError("Error: Can't find wsadmin path", "error");
                    },
    },
    {
        id      => "error2",
        pattern => q{The system cannot find the path specified},
        action  => q{
                        incValue("errors"); diagnostic("", "error", -1);
                        &addSimpleError("Error: Can't find wsadmin path", "error");
                    },
    },
    {
        id      => "error3",
        pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
        action  => q{
                       incValue("errors"); diagnostic("", "error", -1);
                       &addSimpleError("Error: Error occurred while deploying OSGi Application.", "error");
                    }
    },
    {
        id      => "step1",
        pattern => q{External bundle repository added successfully.},
        action  => q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "External bundle repository added successfully.";
                    setProperty("summary", $description . "\n");
         },
    },
    {
        id      => "step2",
        pattern => q{Added\s(.+)\sto internal bundle repository successfully.},
        action  => q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                        $::gProperties{"summary"} : '');
                  $description = "Added $1 to internal bundle repository successfully.";
                  setProperty("summary", $description . "\n");
       },
    },
    {
        id      => "step3",
        pattern => q{Imported asset\s(.+)\ssuccessfully.},
        action  => q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Imported asset $1 successfully.";
                    setProperty("summary", $description . "\n");
                     },
    },
    {
        id      => "step4",
        pattern => q{All bundles downloaded successfully.},
        action  => q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                        $::gProperties{"summary"} : '');
                  $description = "All bundles downloaded successfully.";
                  setProperty("summary", $description . "\n");
                           },
    },
    {
        id      => "step5",
        pattern => q{Created business level application\s(.+)\ssuccessfully.},
        action  => q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Created business level application $1 successfully.";
                    setProperty("summary", $description . "\n");
                                 },
    },
    {
        id      => "step6",
        pattern => q{Added asset\s(.+)\sto BLA\s(.+)\s.},
        action  => q{
                      my $description = ((defined $::gProperties{"summary"}) ?
                            $::gProperties{"summary"} : '');
                      $description = "Added asset $1 to BLA $2 .";
                      setProperty("summary", $description . "\n");
                                       },
    },
    {
        id      => "step7",
        pattern => q{Started application\s(.+)\ssuccessfully.},
        action  => q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Started application $1 successfully.";
                    setProperty("summary", $description . "\n");
                                             },
    },

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
