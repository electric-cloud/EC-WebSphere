
push (@::gMatchers,
  {
   id =>        "profileCreated",
   pattern =>          q{INSTCONFSUCCESS: Success: Profile (.+) now exists.},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "Profile $1 created successfully.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
    {
     id =>        "error",
     pattern =>          q{INSTCONFFAILED:},
     action =>           q{

                my $description = ((defined $::gProperties{"summary"}) ?
                      $::gProperties{"summary"} : '');

                $description .= "Error occured while creating profile.";
                diagnostic("Error", "error", -1, 0);
                setProperty("summary", $description . "\n");

     },
    },
  {
   id =>        "validationErrors",
   pattern =>          q{validation errors},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "Provided profile configuration invalid.";
              diagnostic("Invalid Input", "error", 1, forwardWhile(".+", 1));
              setProperty("summary", $description . "\n");
    
   },
  },
);

