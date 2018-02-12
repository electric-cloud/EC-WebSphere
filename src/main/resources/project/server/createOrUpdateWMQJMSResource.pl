#
#  Copyright 2015 Electric Cloud, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

=head1 NAME

CreateOrUpdateWMQJMSResource.pl

=head1 SYNOPSIS

=head1 DESCRIPTION

A perl library that deploys an enterprise application on a cluster or standalone server

=head1 LICENSE

Copyright (c) 2014 Electric Cloud, Inc.
All rights reserved.

=head1 AUTHOR

    ---

=head2 METHODS

=cut

# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use warnings;
use strict;
use Data::Dumper;
use JSON;

use ElectricCommander;
use ElectricCommander::PropMod qw(/myProject/modules);
use WebSphere::WebSphere;
use WebSphere::Util;


$| = 1;

# -------------------------------------------------------------------------
# Variables
# -------------------------------------------------------------------------

my $opts = {
    wsadminabspath      => '',
    configname          => '$[configname]',
    resourceScope       => '$[resourceScope]',
    wmqResourceAPI      => '$[wmqResourceAPI]',
    wmqResourceName     => '$[wmqResourceName]',
    JNDIName            => '$[JNDIName]',
    destinationJNDIName => '$[destinationJNDIName]',
    resourceDescription => '$[resourceDescription]',
    wmqResourceType     => '$[wmqResourceType]',
    topicOrQueueName    => '$[topicOrQueueName]',
    ccsid               => '$[ccsid]',
    brokerVersion       => '$[brokerVersion]',
    wildcardFormat      => '$[wildcardFormat]',
    customProperties    => '$[customProperties]',
    additionalOptions   => '$[additionalOptions]',
};

print Dumper $opts;
my $ec = ElectricCommander->new();
# $ec->abortOnError(0);

my $websphere = WebSphere::WebSphere->new(
    $ec,
    $opts->{configname},
    $opts->{wsadminabspath},
);

my $r = $websphere->getParamsRenderer(
    name     => $opts->{wmqResourceName},
    jndiName => $opts->{JNDIName},
);

$r->{description} = $opts->{resourceDescription} if $opts->{resourceDescription};
$r->{ccsid} = $opts->{ccsid} if $opts->{ccsid};
if ($opts->{brokerVersion}) {
    $r->{brokerVersion} = $opts->{brokerVersion};
}
if ($opts->{wildcardFormat}) {
    $r->{wildcardFormat} = $opts->{wildcardFormat};
}
if ($opts->{customProperties}) {
    $r->{customProperties} = $opts->{customProperties};
}


## json parse
if ($opts->{additionalOptions}) {
    my $object = {};
    eval {
        $object = decode_json($opts->{additionalOptions});
        1;
    } or do {
        $websphere->bail_out("Error occured during parsing additional options JSON: $@");
    };
    for my $k (keys %$object) {
        if (defined $object->{$k} && !exists $r->{$k}) {
            $r->{$k} = $object->{$k};
        }
    }
}
### now we need to set parameters that are required/non-required or
### conext dependent
if ($opts->{wmqResourceAPI} eq 'WMQ_Topic') {
    unless ($opts->{topicOrQueueName}) {
        $websphere->bail_out("Topic Or Queue name is mandatory when API is set to topic");
    }
    $r->{topicName} = $opts->{topicOrQueueName};

}
elsif ($opts->{wmqResourceAPI} eq 'WMQ_Queue') {

    # TODO: Add warning if these params are present.
    delete $r->{brokerVersion};
    delete $r->{wildcardFormat};

    unless ($opts->{topicOrQueueName}) {
        $websphere->bail_out("Topic Or Queue name is mandatory when API is set to topic");
    }
    $r->{queueName} = $opts->{topicOrQueueName};
}
elsif ($opts->{wmqResourceAPI} eq 'WMQ_ConnectionFactory') {
    ## TODO: Add this
}
elsif ($opts->{wmqResourceAPI} eq 'WMQ_ActivationSpec') {
    if (!$opts->{destinationJNDIName}) {
        $websphere->bail_out("Destination JNDI name is mandatory");
    }
    if (!$opts->{wmqResourceType}) {
        $websphere->bail_out("Type is mandatory parameter.");
    }
    $r->{destinationType} = $opts->{wmqResourceType};
    $r->{destinationJndiName} = $opts->{destinationJNDIName};
}
else {
    $websphere->bail_out("Unknown API $opts->{wmqRespourceAPI}");
}

my $r_edit = $websphere->getParamsRenderer(%$r);

$websphere->ec()->setProperty('/myJobStep/requestParameters' => $r->render());
$websphere->ec()->setProperty('/myJobStep/requestParametersEdit' => $r_edit->render());

my $logger = $websphere->log();
my $file = 'create_or_update_wmq_jms_resource.py';
my $script = $ec->getProperty("/myProject/wsadmin_scripts/$file")->getNodeText('//value');
$file = $websphere->write_jython_script(
    $file, {},
    augment_filename_with_random_numbers => 1
);

my $shellcmd = $websphere->_create_runfile($file, ());
my $escaped_shellcmd = $websphere->_mask_password($shellcmd);
$logger->info('WSAdmin command line: ', $escaped_shellcmd);

$logger->debug("WSAdmin script:");
$logger->debug('' . $script);
$logger->debug("== End of WSAdmin script ==");
my %props = ();

$props{createOrUpdateWMQJMSResourceLine} = $escaped_shellcmd;
my $cmd_res = `$shellcmd 2>&1`;
$logger->info($cmd_res);

my $code = $? >> 8;

my $result_params = {
    outcome => {
        target => 'myCall',
        result => '',
    },
    procedure => {
        target => 'myCall',
        msg => ''
    },
    pipeline => {
        target => 'Create Or Update WMQ JMS Resource Result:',
        msg => '',
    }
};

# success
my $operation_mode = $1 if $cmd_res =~ m/Operation\smode:\s(.*?)$/ms;
if ($code == SUCCESS) {
    my $message = sprintf 'Successfully created or updated %s: %s', $opts->{wmqResourceAPI}, $opts->{wmqResourceName};
    $message = $1 if $cmd_res =~ m/Status:\sOK,\sMessage:\s(.*?)$/ms;
    $result_params->{outcome}->{result} = 'success';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $message;
}
else {
    my $error = $websphere->extractWebSphereExceptions($cmd_res);
    if ($error) {
        $error = "Error occured during $operation_mode of $opts->{wmqResourceName}:\n$error";
    }
    else {
        $error = "Unexpected error occured.";
    }
    $result_params->{outcome}->{result} = 'error';
    $result_params->{procedure}->{msg} = $result_params->{pipeline}->{msg} = $error;
}

my $exit = $websphere->setResult(%$result_params);

$exit->();
