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
package DocFilter::Config;


## Global

$algorithmName = "BasicDocFilter";
$algorithmVersion = "1.0";


## Repository Mappings

%repositories = ('example1' => '/',
		 'example2' => '/home',
		 );


## WS Settings

$serverURL = '127.0.0.1';
$serverPort = 10666;
$URI = 'http://citeseerx.org/algorithms/docfilter/wsdl';

1;
