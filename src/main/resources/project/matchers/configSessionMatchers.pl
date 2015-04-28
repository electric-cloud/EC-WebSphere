push (@::gMatchers,

  {
   id =>        "error1",
   pattern =>          q{is not recognized as an internal or external command|The system cannot find the path specified},
   action =>           q{
              my $description = ((defined $::gProperties{"summary"}) ?
                    $::gProperties{"summary"} : '');
              $description .= "Error: Can't find wsadmin path.";
              setProperty("summary", $description . "\n");
               incValue("errors"); diagnostic("", "error", -1);
   },
  },
  {
     id =>        "success",
     pattern =>          q{Session management properties set successfully},
     action =>           q{
                my $description = ((defined $::gProperties{"summary"}) ?
                      $::gProperties{"summary"} : '');
                $description .= "Session management properties set successfully.";
                setProperty("summary", $description . "\n");
     },
  },
  {
          id      => "error2",
          pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
          action  => q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Error: Error occurred while setting session configuration properties.";
                    setProperty("summary", $description . "\n");
                    incValue("errors"); diagnostic("", "error", -1);
                      }
  }
);