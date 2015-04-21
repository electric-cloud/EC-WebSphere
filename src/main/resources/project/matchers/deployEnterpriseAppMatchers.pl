push (@::gMatchers,

  {
   id =>        "error1",
   pattern =>          q{is not recognized as an internal or external command|The system cannot find the path specified},
   action =>           q{
              my $description = ((defined $::gProperties{"summary"}) ?
                    $::gProperties{"summary"} : '');
              $description = "Error: Can't find wsadmin path.";
              setProperty("summary", $description . "\n");
   },
  },
  {
       id =>        "step1",
       pattern =>          q{Installing\s(.+)\s....},
       action =>           q{
                  my $description = ((defined $::gProperties{"summary"}) ?
                        $::gProperties{"summary"} : '');
                  $description = "Installing $1.";
                  setProperty("summary", $description . "\n");
       },
  },
  {
         id =>        "step2",
         pattern =>          q{Synchronizing configuration repository with nodes.},
         action =>           q{
                    my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Synchronizing configuration with nodes.";
                    setProperty("summary", $description . "\n");
         },
  },
  {
           id =>        "step3",
           pattern =>          q{Application\s(.+)\sinstalled completely},
           action =>           q{
                      my $description = ((defined $::gProperties{"summary"}) ?
                            $::gProperties{"summary"} : '');
                      $description = "Application $1 installed completely";
                      setProperty("summary", $description . "\n");
           },
  },
  {
             id =>        "step4",
             pattern =>          q{The application is ready to start.},
             action =>           q{
                        my $description = ((defined $::gProperties{"summary"}) ?
                              $::gProperties{"summary"} : '');
                        $description = "Starting application...";
                        setProperty("summary", $description . "\n");
             },
  },
  {
               id =>        "step5",
               pattern =>          q{Application\s(.+)\sstarted successfully.},
               action =>           q{
                          my $description = ((defined $::gProperties{"summary"}) ?
                                $::gProperties{"summary"} : '');
                          $description = "$1 started successfully.";
                          setProperty("summary", $description . "\n");
               },
  },
  {
          id      => "error2",
          pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
          action  => q{
                 my $description = ((defined $::gProperties{"summary"}) ?
                          $::gProperties{"summary"} : '');
                    $description = "Error: Error occurred while deploying enterprise application.";
                    setProperty("summary", $description . "\n");
                    incValue("errors"); diagnostic("", "error", -1);
                      }
  }
);