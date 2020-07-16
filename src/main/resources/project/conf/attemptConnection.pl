use strict;
use warnings;

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;

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

my $wsadminabspath = '$[wsadminabspath]';
my $websphere_url  = '$[websphere_url]';
my $websphere_port = '$[websphere_port]';
my $conntype       = '$[conntype]';
my $credential     = '$[credential]';
my $debug_level    = '$[debug]';

my $cred_xpath = $ec->getFullCredential($credential);
my $username   = $cred_xpath->findvalue("//userName");
my $password   = $cred_xpath->findvalue("//password");

# print(Dumper(['#001', ''.$username, ''.$password]));
# print(Dumper(['#002', $projName, $pluginName, $pluginKey]));
# print(Dumper(['#003', $wsadminabspath, $websphere_url, $websphere_port, $conntype, $credential]));

#*****************************************************************************
sub configurationErrorWithSuggestions {
    my ($errmsg) = @_;

    my $suggestions = q{Reasons could be due to one or more of the following. Please ensure they are correct and try again.:
1. WebSphere Host & WebSphere Connector Port - Is your URL complete and reachable?
2. WSAdmin Absolute Path  - Is your Path to the Script correct?
3 Connection Type - Is the appropriate connector configured properly?
4. Test Resource - Is your Test resource correctly wired with CloudBees CD?  Is your Test Resource correctly setup with WebSphere?
5. Credentials - Are your credentials correct? Are you able to use these credentials to log in to WebSphere using its console?
};

    $ec->setProperty('/myJob/configError', $errmsg . "\n\n" . $suggestions);
    $ec->setProperty('/myJobStep/summary', $errmsg . "\n\n" . $suggestions);

    logErrorDiag("Create Configuration failed.\n\n$errmsg");
    logInfoDiag($suggestions);

    return;
}

#*****************************************************************************
sub logInfoDiag {
    my (@params) = @_;

    return printDiagMessage('INFO', @params);
}

#*****************************************************************************
sub logErrorDiag {
    my (@params) = @_;

    return printDiagMessage('ERROR', @params);
}

#*****************************************************************************
sub logWarningDiag {
    my (@params) = @_;

    return printDiagMessage('WARNING', @params);
}

#*****************************************************************************
sub printDiagMessage {
    my (@params) = @_;

    my $level = shift @params;

    if (!$level || !@params) {
        return 0;
    }

    $level = uc $level;
    if ($level !~ m/^(?:ERROR|WARNING|INFO)$/s) {
        return 0;
    }

    # \n[OUT][%s]: %s :[%s][OUT]\n
    my $begin = "\n[OUT][$level]: ";
    my $end   = " :[$level][OUT]\n";

    my $msg = join '', @params;
    $msg = $begin . $msg . $end;

    return print($msg);
} ## end sub printDiagMessage

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
        next if ($arg =~ m/['"]/s);

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
        "'AdminApp.list()'",
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
    print('STDERR: ', $stderr);

    if (isWin() && ($stderr =~ m/[\w\d]{8}E:/ms)) {
        print("Detected an error on windows. Changing code to 1.\n");
        $code = 1;
    }
}

print('STDOUT: ', $stdout) if ($stdout);
print('ERRMSG: ', $errmsg) if ($errmsg);
print('EXIT_CODE: ', $code);

if ($code) {
    $errmsg ||= $stderr || $stdout;

    configurationErrorWithSuggestions($errmsg);

    exit(ERROR);
}
else {
    print("Connection succeeded");
    exit(SUCCESS);
}

#*****************************************************************************
