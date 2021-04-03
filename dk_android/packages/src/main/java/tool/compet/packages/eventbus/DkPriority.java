/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.eventbus;

/**
 * Priority for subscription methods. For same subscription id,
 * subscriber which has higher priority will be executed first.
 */
public interface DkPriority {
	int MIN = Integer.MIN_VALUE;
	int NORMAL = 0;
	int MAX = Integer.MAX_VALUE;
}
