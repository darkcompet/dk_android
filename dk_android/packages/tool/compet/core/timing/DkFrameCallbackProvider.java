/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.timing;

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
	 * @param frameType   let provider know request frame type.
	 * @param delayMillis duration which client wanna delay before post.
	 */
	void requestNextFrame(int frameType, long delayMillis);
}
