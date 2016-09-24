use strict;
use warnings;
use Data::Dumper;
use Test::More tests => 7;
use Test::VirtualModule qw(ElectricCommander ElectricCommander::PropDB);
use XML::XPath;

# Read entire file into string
sub read_file {
    my ($file) = @_;

    return do {
        local $/ = undef;
        open my $fh, "<", $file or die "could not open $file: $!";
        split( "\n", <$fh> );
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
        my ( $self, $configurationName ) = @_;

        return () unless $configurationName eq 'websphere';

        return (
            'websphere_port' => '8880',
            'websphere_url'  => 'localhost',
            'user'           => 'websphere',
            'password '      => 'webspherePassword',
            'conntype'       => 'SOAP'
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

$^O = 'MSWin32';

is( $websphere->_create_runfile('discover.py'),
qq{"$websphere->{wsadminPath}" -password "changeme" -conntype SOAP -lang jython -user admin -port 8880 -host localhost -f "discover.py"}
);

is(
    $websphere->_mask_password( $websphere->_create_runfile('discover.py') ),
qq{"$websphere->{wsadminPath}" -password "*****" -conntype SOAP -lang jython -user admin -port 8880 -host localhost -f "discover.py"}
);

$^O = 'linux';

is( $websphere->_create_runfile('discover.py'),
qq{"$websphere->{wsadminPath}" -password 'changeme' -conntype SOAP -lang jython -user admin -port 8880 -host localhost -f "discover.py"}
);

is(
    $websphere->_mask_password( $websphere->_create_runfile('discover.py') ),
qq{"$websphere->{wsadminPath}" -password '*****' -conntype SOAP -lang jython -user admin -port 8880 -host localhost -f "discover.py"}
);

my $expected = {
    json => {
        clusters => {
            'WIN-GHQSVBOKFFTCellManager01:wsCluster' => [],
            'WIN-GHQSVBOKFFTNode01:wsCluster'        => [],
            'WIN-GHQSVBOKFFTNode03:wsCluster' =>
              [ 'DefaultApplication', 'query' ]
        },
        servers => {
            'WIN-GHQSVBOKFFTNode01:server1'          => [],
            'WIN-GHQSVBOKFFTNode03:server1'          => ['DefaultApplication'],
            'WIN-GHQSVBOKFFTNode03:wsClusterMember1' => ['query']
        }
    },
    messages => [
        {
            WASX7209I =>
'Connected to process "dmgr" on node WIN-GHQSVBOKFFTCellManager01 using SOAP connector;  The type of process is: DeploymentManager'
        },
        {
            WASX7309W =>
'No "save" was performed before the script ".\\discover.py" exited; configuration changes will not be saved.'
        }
    ]
};

my $output =
  $websphere->_parse_wsadmin_output( read_file('t/wsadmin_output.log') );
is_deeply( $output, $expected ) || diag explain $output;
