# Copyright (c) 2016 Electric Cloud, Inc.
# All rights reserved
# -------------------------------------------------------------------------

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::Discovery;

use warnings;
use strict;
$| = 1;

#get an EC object
my $ec = new ElectricCommander( { abortOnError => 0 } );
my $disc = WebSphere::Discovery->new($ec, "$[resourceName]");
$disc->discover();
