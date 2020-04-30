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

package tool.compet.appbundle.timing;

/**
 * This class, register frame callback with running thread and notify to client.
 * It is used to customize frame callback provider that used in timing engine package.
 */
public interface DkFrameCallbackProvider {
	// when we post a message, we need to know the message is
	// start or next frame request to add frameDelay to delivery time.
	int FRAME_START = 1;
	int FRAME_DELAY = 2;

	/**
	 * notifies clients on each new frame
	 */
	interface Callback {
		void onFrame(long frameUptimeMillis);
	}

	/**
	 * we needs know this frame rate to request next frame
	 */
	DkFrameCallbackProvider setFrameDelay(long delayMillis);

	/**
	 * get frame rate of this provider (server)
	 */
	long getFrameDelay();

	/**
	 * client should call this method to here frame update event
	 */
	DkFrameCallbackProvider setFrameCallback(Callback callback);

	/**
	 * request next frame from running thread, we support postDelayMillis since
	 * clients (Animator, Timer...) maybe need delay when post
	 *
	 * @param frameType let provider know request frame type.
	 * @param delayMillis duration which client wanna delay before post.
	 */
	void requestNextFrame(int frameType, long delayMillis);
}
