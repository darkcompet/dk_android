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

package tool.compet.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class, provides basic common operations on an array.
 */
public class DkArrays {
	public static void swap(int[] arr, int i, int j) {
		int N = arr.length;

		if (i >= 0 && j >= 0 && i < N && j < N && i != j) {
			arr[i] ^= arr[j];
			arr[j] ^= arr[i];
			arr[i] ^= arr[j];
		}
	}

	public static void reverse(int[] arr) {
		int N = arr.length;
		int tmp;

		for (int i = 0, j = N - 1; i < j; ++i, --j) {
			tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	public static void reverse(long[] arr) {
		int N = arr.length;
		long tmp;

		for (int i = 0, j = N - 1; i < j; ++i, --j) {
			tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	public static void reverse(float[] arr) {
		int N = arr.length;
		float tmp;

		for (int i = 0, j = N - 1; i < j; ++i, --j) {
			tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	public static void reverse(double[] arr) {
		int N = arr.length;
		double tmp;

		for (int i = 0, j = N - 1; i < j; ++i, --j) {
			tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
	}

	public static <T> List<T> toList(T... args) {
		return args == null ? new ArrayList<>() : Arrays.asList(args);
	}
}
