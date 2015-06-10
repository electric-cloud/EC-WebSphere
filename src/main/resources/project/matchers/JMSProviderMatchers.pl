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
        pattern => q{The system cannot find the path specified},
        action =>
          q{&addSimpleError("Error: Can't find wsadmin path", "error");},
    },
    {
            id      => "error3",
            pattern => q{SyntaxError: invalid syntax},
            action =>
              q{&addSimpleError("Error: Invalid property set value provided.", "error");},
    },
    {
        id      => "error4",
        pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
        action  => q{
                       incValue("errors"); diagnostic("", "error", -1);
                       &addSimpleError("Error: Error occurred while creating JMS provider.", "error");
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