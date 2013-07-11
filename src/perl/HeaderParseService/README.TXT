SVMHeaderParse README
IGC

SVMHeaderParse is a utility for extracting header information
from research papers based on Support Vector Machines and
heuristic regularization.  For details on the algorithm,
please see the following paper:

Hui Han, C. Lee Giles, Eren Manavoglu, Hongyuan Zha,
Zhenyue Zhang, Edward A. Fox. "Automatic Document Metadata
Extraction Using Support Vector Machines", in Proceedings
of ACM/IEEE Joint Conference on Digital Libraries
(JCDL 2003): 37-48, 2003.

Installation:

In order to use the SVMHeaderParse web service you will need
the following modules in your perl library:

Log::Log4perl
Log::Dispatch

Edit lib/HeaderParse/Config/API_Config.pm to provide values
appropriate for your environment.  Also edit wsdl/SVMHeaderParse.wsdl
to reflect any changes to your service URL.

Command Line Usage:

Command line utilities are provided for extracting header information
from text documents.  By default, text files are expected to be encoded
in UTF-8, but the expected encoding can be adjusted using perl
command line switches.  To run SVMHeaderParse on a single document,
execute the following command:

extractHeader.pl textfile [outfile]

If "outfile" is specified, the XML output will be written to that
file; otherwise, the XML will be printed to STDOUT.

There is also a web service interface available, using the
SOAP::Lite perl module.  To start the service, just execute:

headerparse-service.pl

By default, the service will start on port 40000, but this is
configurable in the HeaderParse::Config::API_Config library module.
A WSDL file is provided with the distribution that outlines the
message details expected by the SVMHeaderParse service.  If the
service port is changed, the WSDL file must also be modified to
reflect that change.  Expected parameters in the input message
are "filePath" (a path to the text file to parse) and "repositoryID".
The SVMHeaderParse service is designed for deployment in an
environment where text files may be located on file systems mounted
from arbitrary machines on the network.  Thus, "repositoryID" provides
a means to map a given shared file system to it's mount point.
Repository mappings are configurable in the API_Config module.  The
"filePath" parameter provides a path to the text file relative to
the repository mount point.  The local file system may be specified
using the reserved repository ID "LOCAL".  In that case, an absolute
path to the text file may be specified.

A perl client is also provided that demonstrates how to use the
service.  Execute the client with the following command:

headerparse-client.pl filePath repositoryID

If the call is successful, the XML output will be printed to STDOUT.

API:

The SVMHeaderParse libraries may be used directly from external perl
applications.  The interface module is HeaderParse::API::Parser.  If
XML output is desired, use the

HeaderParse::API::Parser::_parseHeader($filePath, $jobID)

subroutine.  If the SVMHeaderParse library is used from external
Perl applications, remember to use the "-CSD" perl option for
global unicode stream support (or otherwise handle encoding) or
risk string corruption.
