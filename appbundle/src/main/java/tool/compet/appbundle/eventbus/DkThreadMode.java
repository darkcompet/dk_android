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

package tool.compet.appbundle.eventbus;

/**
 * Run mode for Subscriber method.
 */
public interface DkThreadMode {
	// Subscriber method will run on same thread with Poster
	int POSTER = 1;

	// Defaul mode, subscriber method will run on Android main thread
	int MAIN = 2;

	// Subscriber method will run on Android main thread in ordered
	int MAIN_ORDERED = 3;

	// Subscriber method will run on background thread
	int ASYNC = 4;

	// Subscriber method will run on background thread in ordered
	int ASYNC_ORDERED = 5;
}
