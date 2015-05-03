push (@::gMatchers,

  {
   id =>        "error1",
   pattern =>          q{is not recognized as an internal or external command|The system cannot find the path specified},
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