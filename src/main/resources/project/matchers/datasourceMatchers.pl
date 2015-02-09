
push (@::gMatchers,
  
  {
   id =>        "error1",
   pattern =>          q{the jndiName attribute of an existing DataSource object has the same value as},
   action =>           q{&addSimpleError("Error: Duplicate JNDI Name", "error");},
  },
  
  {
   id =>        "error2",
   pattern =>          q{is not recognized as an internal or external command},
   action =>           q{&addSimpleError("Error: Can't find wsadmin path", "error");},
  },
  
  {
   id =>        "error3",
   pattern =>          q{Error: Configuration},
   action =>           q{&addSimpleError("Error: Configuration doesn't exist", "error");},
  },
  
  {
   id =>        "error4",
   pattern =>          q{WASX7444E: Invalid parameter value (.+) on command "create"},
   action =>           q{&addSimpleError("Error: Invalid parameter value for parameter \"parent config id\". Check the JDBC Provider.", "error");},
  },    
  
  {
   id =>        "error5",
   pattern =>          q{The system cannot find the path specified},
   action =>           q{&addSimpleError("Error: Can't find wsadmin path", "error");},
  },
  
  {
   id =>        "error6",
   pattern =>          q{WASX7444E: Invalid parameter value (.+) on command "remove"},
   action =>           q{&addSimpleError("Error: Can't find the specified datasource.", "error");},
  },    
    
);


sub addSimpleError {
    my ($customError, $type) = @_;	
    my $ec = new ElectricCommander();
    $ec->abortOnError(0);
	
	setProperty("summary", $customError);
	if ($type eq "success") {
		$ec->setProperty("/myJobStep/outcome", 'success');
	} elsif ($type eq "error") {
		$ec->setProperty("/myJobStep/outcome", 'error');
	} else {
		$ec->setProperty("/myJobStep/outcome", 'warning');
	}
}