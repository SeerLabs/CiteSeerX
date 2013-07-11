#
# Copyright 2007 Penn State University
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#     http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
package DocFilter::Filter;
##
##  Isaac Councill, 7/31/07
##
use strict;


sub filter {
    my ($filePath) = @_;

    if (!open (IN, "<$filePath")) {
	return (0, 0, "Could not open file $filePath: $!");
    }
    my $text;
    {
	local $/ = undef;
	$text = <IN>;
    }

    if (hasReferences(\$text) <= 0) {
	return (1, 0, "No reference section is present");
    }
    return (1, 1, "All filters passed");

} # filter


sub hasReferences {
    my $rText = shift;
    if ($$rText =~ /\b(REFERENCES?|References?|BIBLIOGRAPHY|Bibliography|REFERENCES AND NOTES|References and Notes)\:?\s*\n/sg) {
	return 1;
    } else {
	return 0;
    }

} # hasReferences


1;
