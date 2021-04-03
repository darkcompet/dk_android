/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

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
