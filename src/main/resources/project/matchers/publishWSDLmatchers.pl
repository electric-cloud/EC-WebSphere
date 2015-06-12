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