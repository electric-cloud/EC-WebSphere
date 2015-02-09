
push (@::gMatchers,
  
  {
   id =>        "appStatus",
   pattern =>          q{(The application is ready|The application is not ready)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "$1";
                              
              setProperty("summary", $description . "\n");
    
   },
  },

);

