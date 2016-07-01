# Copyright (c) 2016 Electric Cloud, Inc.
# All rights reserved
# -------------------------------------------------------------------------

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use Data::Dumper;

use warnings;
use strict;
$| = 1;

#get an EC object
my $ec = new ElectricCommander( { abortOnError => 0 } );

my @resourceNames = split( /\n/, "$[resourceNames]" );

if ( scalar(@resourceNames) < 1 ) {
	print "Error: resourceNames parameter must be provided.\n";
	exit 1;
}

# For each resource, create steps which actually execute resource-level discovery
for my $resourceName (@resourceNames) {
	my $xpath = $ec->createJobStep(
		{
			jobStepName     => "Discover Resource - $resourceName",
			subprocedure    => 'DiscoverResource',
			resourceName    => $resourceName,
			actualParameter => [
				{
					actualParameterName => 'configurationName',
					value               => '$[configurationName]'
				}
			]
		}
	);
}

1;
