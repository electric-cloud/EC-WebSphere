def testProjectName = args.projectName
def wasResourceName = args.wasResourceName
project testProjectName

procedure 'GetResources', {
  description = ''
  jobNameTemplate = ''
  projectName = testProjectName
  wasResourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'filePath', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'fileURL', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  formalParameter 'wasResourceName', defaultValue: '', {
    description = ''
    expansionDeferred = '0'
    label = null
    orderIndex = null
    required = '1'
    type = 'entry'
  }

  step 'GetResources', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = '''
use strict;
use warnings;
use Data::Dumper;
use LWP::UserAgent;
my $file_path = '$[filePath]';

my $ua = LWP::UserAgent->new();
my $url = '$[fileURL]';
my $response = $ua->get($url);
open (my $fh, '>', $file_path) or die "Cant open file: $!\\n";
binmode $fh;

my $content = $response->decoded_content();
print $fh $content;

'''
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    projectName = testProjectName
    releaseMode = 'none'
    resourceName = '$[wasResourceName]'
    shell = 'ec-perl'
    subprocedure = null
    subproject = null
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = ''
    workspaceName = '$[wasResourceName]'
  }

  // Custom properties

  property 'ec_customEditorData', {

    // Custom properties

    property 'parameters', {

      // Custom properties

      property 'filePath', {

        // Custom properties
        formType = 'standard'
      }

      property 'fileURL', {

        // Custom properties
        formType = 'standard'
      }

      property 'wasResourceName', {

        // Custom properties
        formType = 'standard'
      }
    }
  }
}
