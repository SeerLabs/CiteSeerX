/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.disambiguation.dbscan;

import java.util.Set;
import java.util.List;

/**
 * BlockingDatabase
 *
 * a data structure to be used by DBSCAN clustering algorithm
 * 
 * @author Puck Treeratpituk
 */
public abstract class BlockingDatabase 
{
	public abstract Set<Integer> getAllPoints();
	public abstract Set<Integer> getNeighbors(Integer point, double eps);
	public abstract void printPoint(Integer point);
} //- class BlockingDatabase