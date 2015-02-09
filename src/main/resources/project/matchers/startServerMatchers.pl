
push (@::gMatchers,
  {
   id =>        "serverStarted",
   pattern =>          q{^ADMU3000I(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "Server started successfully.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
  {
   id =>        "serverAlreadyRunning",
   pattern =>          q{^ADMU3027E(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "An instance of the server may already be running.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
);

