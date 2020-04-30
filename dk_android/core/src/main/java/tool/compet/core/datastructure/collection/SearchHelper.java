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

package tool.compet.core.datastructure.collection;

class SearchHelper {
	/**
	 * Arrays.binarySearch() version but it does not check range.
	 */
	static int binarySearch(int[] array, int size, int value) {
		int lo = 0;
		int hi = size - 1;

		while (lo <= hi) {
			final int mid = (lo + hi) >>> 1;
			final int midVal = array[mid];
			if (midVal < value) {
				lo = mid + 1;
			}
			else if (midVal > value) {
				hi = mid - 1;
			}
			else {
				// value found
				return mid;
			}
		}
		// value not present
		return ~lo;
	}

	/**
	 * Arrays.binarySearch() version but it does not check range.
	 */
	static int binarySearch(long[] array, int size, long value) {
		int lo = 0;
		int hi = size - 1;

		while (lo <= hi) {
			final int mid = (lo + hi) >>> 1;
			final long midVal = array[mid];
			if (midVal < value) {
				lo = mid + 1;
			}
			else if (midVal > value) {
				hi = mid - 1;
			}
			else {
				// value found
				return mid;
			}
		}
		// value not present
		return ~lo;
	}
}
