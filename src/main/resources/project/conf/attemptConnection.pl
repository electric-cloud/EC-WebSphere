use strict;
use warnings;

use ElectricCommander;
use ElectricCommander::PropDB;
use ElectricCommander::PropMod;

use Data::Dumper;

#*****************************************************************************
use constant {
    SUCCESS => 0,
    ERROR   => 1,
};

use constant {
    LEVEL_ERROR => -1,
    LEVEL_INFO  => 0,
    LEVEL_DEBUG => 1,
    LEVEL_TRACE => 2,
};

#*****************************************************************************
my $ec = ElectricCommander->new();
# $ec->abortOnError(0);

my $projName   = '$[/myProject/projectName]';
my $pluginName = '@PLUGIN_NAME@';
my $pluginKey  = '@PLUGIN_KEY@';

my $wsadminabspath = '$[wsadminabspath]';
my $websphere_url  = '$[websphere_url]';
my $websphere_port = '$[websphere_port]';
my $conntype       = '$[conntype]';
my $credential     = '$[credential]';
my $debug_level    = '$[debug]';

ElectricCommander::PropMod::loadPerlCodeFromProperty($ec, '/myProject/EC::Plugin::Core');
ElectricCommander::PropMod::loadPerlCodeFromProperty($ec, '/myProject/EC::WebSphere');

my $ws = EC::WebSphere->new(
    project_name => $projName,
    plugin_name  => $pluginName,
    plugin_key   => $pluginKey
);

my $cred_xpath = $ec->getFullCredential($credential);
my $username   = $cred_xpath->findvalue("//userName");
my $password   = $cred_xpath->findvalue("//password");

$ws->logger->level($debug_level);
$ws->debug_level($debug_level + 1);

# $ws->logger->debug(Dumper(['#001', ''.$username, ''.$password]));
# $ws->logger->debug(Dumper(['#002', $projName, $pluginName, $pluginKey]));
# $ws->logger->debug(Dumper(['#003', $wsadminabspath, $websphere_url, $websphere_port, $conntype, $credential]));

#*****************************************************************************
sub genFileName {
    my ($key, $ext) = @_;

    return File::Spec->catfile($ENV{COMMANDER_WORKSPACE}, sprintf('%s-%06s.%s', $key, int(rand(1_000_000)), $ext || 'txt'));
}

#*****************************************************************************
sub isWin {
    return ($^O eq 'MSWin32') ? 1 : 0;
}

#*****************************************************************************
sub escArgs {
    my (@args) = @_;

    for my $arg (@args) {
        next unless (defined($arg));
        next unless ($arg =~ m/[\s'"]/s);

        my $esca = (isWin) ? q{"} : q{'};

        $arg =~ s/$esca/\\$esca/gs;
        $arg = qq{$esca$arg$esca};
    }

    return @args;
}

#*****************************************************************************
sub hidePwd {
    my (@args) = @_;

    for my $arg (@args) {
        next unless (defined($arg));
        $arg =~ s/-password.+?\s/-password *** /s;
    }

    return @args;
}

#*****************************************************************************
sub readOut {
    my ($fileName) = @_;

    my $msg = '';
    if (open(my $file, $fileName)) {
        local $/ = undef;
        $msg = <$file>;
        close($file);
    }
    else {
        $msg = "Can't open file ($fileName) : $!";
    }

    return $msg;
}

#*****************************************************************************
sub runCommand {
    my (@args) = escArgs(@_);

    printf("Run Command: %s\n", join(' ', hidePwd(@args)));

    if (isWin) {
        print("MSWin32 detected\n");
        $ENV{NOPAUSE} = 1;
    }

    my $fileOut = genFileName('out');
    my $fileErr = genFileName('err');

    local $! = undef;

    system(join(' ', @args, "1>$fileOut", "2>$fileErr"));

    my $ret    = $?;
    my $errmsg = $!;
    my $code   = $ret >> 8;

    my $stdout = readOut($fileOut);
    my $stderr = readOut($fileErr);

    if ($ret == -1) {
        $code = 1;
    }
    elsif ($ret & 0x7f) {
        $code = 1;
        my $err = sprintf("Command died with signal %d, %s coredump\n", ($ret & 0x7f), ($ret & 0x80) ? 'with' : 'without');
        if ($errmsg) {
            $errmsg .= "\n$err";
        }
        else {
            $errmsg .= $err;
        }
    }

    return ($code, $stdout, $stderr, $errmsg);
} ## end sub runCommand

#*****************************************************************************
sub checkConnection {

    #~ ./wsadmin.sh -lang jython -host 127.0.0.1 -port 8879 -conntype SOAP -user wsadmin -password changeme -c 'AdminApp.list()'

    my @args = (
        '-lang',
        'jython',
        '-c',
        'AdminApp.list()',
    );

    if ($conntype) {
        push(@args, '-conntype', $conntype);
    }

    if ($websphere_url) {
        push(@args, ($conntype eq 'IPC') ? '-ipchost' : '-host', $websphere_url);
    }

    if ($websphere_port) {
        push(@args, '-port', $websphere_port);
    }

    if ($username) {
        push(@args, '-user', $username);
    }

    if ($password) {
        push(@args, '-password', $password);
    }

    return runCommand($wsadminabspath, @args);
} ## end sub checkConnection

#*****************************************************************************
my ($code, $stdout, $stderr, $errmsg) = eval {checkConnection();};

my $evalError = $@;
if ($evalError) {
    if ($errmsg) {
        $errmsg .= "\n$evalError";
    }
    else {
        $errmsg = $evalError;
    }

    $code ||= 1;
}

if ($stderr) {
    $ws->out(LEVEL_INFO, 'STDERR: ', $stderr);

    if (isWin() && ($log =~ m/[\w\d]{8}E:/ms)) {
        $ws->logger->debug("Detected an error on windows. Changing code to 1.");
        code = 1;
    }
}

$ws->out(LEVEL_INFO, 'STDOUT: ', $stdout) if ($stdout);
$ws->out(LEVEL_INFO, 'ERRMSG: ', $errmsg) if ($errmsg);
$ws->out(LEVEL_INFO, 'EXIT_CODE: ', $code);

if ($code) {
    $errmsg ||= $stderr || $stdout;

    $ws->configurationErrorWithSuggestions($errmsg);

    exit(ERROR);
}
else {
    $ws->logger->info("Connection succeeded");
    exit(SUCCESS);
}

#*****************************************************************************
