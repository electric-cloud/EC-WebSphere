use strict;
use warnings;
use Data::Dumper;
use Test::More tests => 4;
use Test::VirtualModule qw(ElectricCommander ElectricCommander::PropDB);
use XML::XPath;

# Read entire file into string
sub read_file {
	my ($file) = @_;

	return do {
		local $/ = undef;
		open my $fh, "<", $file or die "could not open $file: $!";
		<$fh>;
	};
}

Test::VirtualModule->mock_sub(
	'ElectricCommander',
	new => sub {
		return bless {}, 'ElectricCommander';
	},
	getFullCredential => sub {
		return XML::XPath->new(
			q{
            <credential>
                <userName>admin</userName>
                <password>changeme</password>
            </credential>}
		);
	}
);

Test::VirtualModule->mock_sub(
	'ElectricCommander::PropDB',
	new => sub {
		return bless {}, 'ElectricCommander::PropDB';
	},
	getRow => sub {
		my ( $self, $confirurationName ) = @_;

		return () unless $confirurationName eq 'websphere';

		return (
			'websphere_port' => '8880',
			'websphere_url'  => 'localhost',
			'user'           => 'websphere',
			'password '      => 'webspherePassword'
		);
	}
);

use WebSphere::WebSphere;

my $ec = new ElectricCommander();

my $websphere = new WebSphere::WebSphere( $ec, 'webspher' );
is( undef, $websphere );

my $wspath = 'C:\Program Files (x86)\IBM\WebSphere\AppServer\bin\wsadmin.bat';

$websphere = new WebSphere::WebSphere( $ec, 'websphere', $wspath );
is( 1, $websphere->isa('WebSphere::WebSphere') );

is(
qq{cmd /c ""$wspath" -password changeme -conntype SOAP -lang jython -user admin -port 8880 -host localhost -f "discover.py""},
	$websphere->_create_runfile('discover.py')
);

my $expected = {
	json => [
		{ 'DefaultApplication'     => { 'serverName' => 'Server2' } },
		{ 'ivtApp'                 => { 'serverName' => 'Server2' } },
		{ 'query'                  => { 'serverName' => 'Server2' } },
		{ 'DefaultApplication.ear' => { 'serverName' => 'Server2' } }
	],
	messages => [
		{
			'WASX7209I' =>
'Connected to process "server1" on node win-2008R2-stdNode01 using SOAP connector;  The type of process is: UnManagedProcess'
		}
	  ]
};

my $output =
  $websphere->_parse_wsadmin_output( read_file('t/wsadmin_output.log') );
is_deeply( $output, $expected ) || diag explain $output;
