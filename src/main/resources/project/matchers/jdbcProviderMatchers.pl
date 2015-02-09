
push (@::gMatchers,
  
  {
   id =>        "error1",
   pattern =>          q{Error: Configuration},
   action =>           q{&addSimpleError("Error: Configuration doesn't exist", "error");},
  },
  
  {
   id =>        "error2",
   pattern =>          q{is not recognized as an internal or external command},
   action =>           q{&addSimpleError("Error: Can't find wsadmin path", "error");},
  },
  
  {
   id =>        "error3",
   pattern =>          q{The system cannot find the path specified},
   action =>           q{&addSimpleError("Error: Can't find wsadmin path", "error");},
  },    
  
  {
   id =>        "error4",
   pattern =>          q{WASX7444E: Invalid parameter value (.+) on command "remove"},
   action =>           q{&addSimpleError("Error: Can't find the specified JDBC Provider.", "error");},
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