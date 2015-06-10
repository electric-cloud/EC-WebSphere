
push(
    @::gMatchers,
    {
        id      => "error1",
        pattern => q{is not recognized as an internal or external command},
        action =>
          q{&addSimpleError("Error: Can't find wsadmin path", "error");},
    },
    {
        id      => "error2",
        pattern => q{Error :\s(.+)\s(.+)\sdoes not exist.},
        action  => q{&addSimpleError("Error: $1 $2 does not exist.", "error");},
    },
    {
        id      => "error3",
        pattern => q{The system cannot find the path specified},
        action =>
          q{&addSimpleError("Error: Can't find wsadmin path", "error");},
    },
    {
        id      => "error4",
        pattern => q{ERROR\s:|[Ee]rror\s:|[Ee]xception},
        action  => q{
                           incValue("errors"); diagnostic("", "error", -1);
                           &addSimpleError("Error: Error occurred while listing cluster members.", "error");
                      }
    },
     {
         id      => "listmembers",
         pattern => q{Cluster member:\s(.+)},
         action  => q{
                        my $description;
                        $description = "$1";
                       setProperty("clusterMembers", $description );
                   }
     },
     {
            id      => "memberConfiguration",
            pattern => q{(\[.+\])},
            action  => q{
                           my $description;
                           $description = "$1";
                          setProperty("memberConfiguration", $description );
                      }
     }


);

sub setProperty {
     my ( $propName, $propValue) = @_;

     my $ec = new ElectricCommander();
     $ec->abortOnError(0);
     my $prevPropValue = ( $ec->getProperty("/myJob/" . $propName) )->findvalue("//value");
     if( $prevPropValue ne '') {
         $propValue = $prevPropValue . "," . $propValue;
     }
     $ec->setProperty( "/myJob/" . $propName, $propValue );

}

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
