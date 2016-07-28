/**
 * Copyright (c) 2010-2016 Michael whapples
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For attribution notices please see the file NOTICES.TXT which should be
 * included in the distribution.
 */
package org.mwhapples.jlouis;

import java.io.File;

import com.sun.jna.Callback;

public interface TableResolver extends Callback {
	/**
	 * A table resolver to be used by LibLouis.
	 * 
	 * LibLouis allows alternative table resolvers to be used instead of the default table resolver. Any class which is to be used as a table resolver must implement this interface and an instance should be registered with the Louis instance in use.
	 * 
	 * @param tableList A string specifying the tables to be resolved. It is for the resolver to determine how the table names are deliminated in the string.
	 * @param base If the table is being included by another table, then base is the file of the table which is including the requested table. If the table is not requested because of being included from another table, then base will be null. This parameter is undocumented in LibLouis and so this description is based upon the result of experimentation and thus may miss specific circumstances.
	 * @return An array of the actual table file objects.
	 */
	public File[] resolveTable(String tableList, String base);
}
