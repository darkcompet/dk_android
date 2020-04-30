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

package tool.compet.core.storage;

/**
 * This is singleton class, is combination of LruCache and DiskLruCache.
 *
 * When you have a big amount of data to cache, this will store it in memory first
 * as possible until given momery-limit-up (MLU) via LruCache. For remained data which can't
 * store in memory since MLU, this will store them into internal storage via DiskLruCache.
 *
 * Note that, each data (inside snapshot) will have own priority to be kept in memory. Lower
 * priority will be popped and stored into disk when MLU happen.
 */
public class DkDualCache {}
