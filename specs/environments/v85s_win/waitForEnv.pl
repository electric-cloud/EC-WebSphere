use strict;
use warnings;
use LWP::UserAgent;

$| = 1;

$ENV{PERL_LWP_SSL_VERIFY_HOSTNAME} = 0;
my $url = 'https://websphere85s:9443/ibm/console';
my $ua = LWP::UserAgent->new();

my $done = 0;
print "will wait until app server is up\n";
while (!$done) {
    my $resp = $ua->get($url);
    my $code = $resp->code();
    print $resp->decoded_content();
    print "Code: $code\n";
    if ($resp->code() eq '404') {
        $done = 1;
        last;
    }
    sleep 10;
}

exit 0;
