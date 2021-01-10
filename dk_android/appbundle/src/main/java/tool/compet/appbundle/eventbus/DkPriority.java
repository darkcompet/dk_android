/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.eventbus;

/**
 * Priority for subscription methods. For same subscription id,
 * higher priority subscriber will be executed first.
 */
public interface DkPriority {
    int MIN = 1;
    int NORMAL = 5;
    int HIGH = 8;
    int MAX = 10;
}
