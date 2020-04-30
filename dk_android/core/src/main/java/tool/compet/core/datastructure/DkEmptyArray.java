/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
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
 */

package tool.compet.core.datastructure;

public interface DkEmptyArray {
	boolean[] BOOLEAN = new boolean[0];
	char[] CHAR = new char[0];
	byte[] BYTE = new byte[0];
	int[] INT = new int[0];
	long[] LONG = new long[0];
	float[] FLOAT = new float[0];
	double[] DOUBLE = new double[0];

	Object[] OBJECT = new Object[0];
	String[] STRING = new String[0];
	Class<?>[] CLASS = new Class[0];
	Throwable[] THROWABLE = new Throwable[0];
	StackTraceElement[] STACK_TRACE_ELEMENT = new StackTraceElement[0];
}
