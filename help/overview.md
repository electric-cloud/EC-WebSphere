WebSphere Application Server (WAS) is a software product that performs the role of a web application server.
More specifically, it is a software framework and middleware that hosts Java based web applications.
It is the flagship product within IBM's WebSphere software suite.
It was initially created by Donald F. Ferguson, who later became CTO of Software for Dell.
The first version was launched in 1998.
WAS is built using open standards such as Java EE, XML, and Web Services. 
It is supported on the following platforms:
 - Windows
 - AIX
 - Linux
 - Solaris
 - IBM i and z/OS.
 
Beginning with Version 6.1 and now into Version 9.0, the open standard specifications are aligned and common across all the platforms.
Platform exploitation, to the extent it takes place, is done below the open standard specification line.
It works with a number of Web servers including Apache HTTP Server, Netscape Enterprise Server, Microsoft Internet Information Services (IIS), IBM HTTP Server for i5/OS, IBM HTTP Server for z/OS, and IBM HTTP Server for AIX/Linux/Microsoft Windows/Solaris.
It uses port 9060 for connection as the default administration port and port 9080 as the default website publication port.
In case you install more WebSphere instances these values will be changed.

For more information about WebSphere, go to the <a href="http://www-01.ibm.com/software/websphere/">WebSphere Home Page</a>.

## CloudBees Flow Integration to WebSphere
You can use this plugin to interact with an IBM
WebSphere Server. You can start and stop the server.
You can also manage applications within the server (start,
deploy, undeploy, stop or custom task) using the provided
WebSphere scripts in the **bin** directory under
the installation home directory (wsadmin, startServer,
stopServer). You can run Jython scripts that are
provided by the user or defined when a procedure is
created.

## Integrated Version

The plugin was tested on following WebSphere versions:
 - WAS and WASND 9.0 on Linux and Windows
 - WAS and WASND 8.5.5 on Linux and Windows
 - WAS and WASND 8.5.0 on Linux and Windows
 - WAS and WASND 8.0 on Linux and Windows
 - WAS 7.0 on Linux and Windows.
   Beginning Release 2.4 we do not certify this plugin on the 7.0 version of WAS considering that this version is not actively supported by IBM since April 2018.
   Please refer to this <a href="https://www-01.ibm.com/common/ssi/ShowDoc.wss?docURL=/common/ssi/rep_ca/3/897/ENUS916-143/index.html&amp;lang=en&amp;request_locale=en">IBM Link</a>

