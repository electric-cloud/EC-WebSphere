
push (@::gMatchers,
  {
   id =>        "serverStopError",
   pattern =>          q{^ADMU0509I(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "The server cannot be reached. It appears to be stopped.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },
  {
   id =>        "serverStopped",
   pattern =>          q{^ADMU4000I(.+)},
   action =>           q{
    
              my $description = ((defined $::gProperties{"summary"}) ? 
                    $::gProperties{"summary"} : '');
                    
              $description .= "The server has been stopped successfully.";
                              
              setProperty("summary", $description . "\n");
    
   },
  },

);

