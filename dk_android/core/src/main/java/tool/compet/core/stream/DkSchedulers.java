/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import tool.compet.core.helper.DkExecutorService;

public class DkSchedulers {
    private static DkScheduler IO;
    private static DkScheduler MAIN;

    @SuppressWarnings("unchecked")
    public static <T> DkScheduler<T> io() {
        if (IO == null) {
            synchronized (DkSchedulers.class) {
                if (IO == null) {
                    IO = new DkIoScheduler<>(DkExecutorService.getIns());
                }
            }
        }
        return (DkScheduler<T>) IO;
    }

    @SuppressWarnings("unchecked")
    public static <T> DkScheduler<T> main() {
        if (MAIN == null) {
            synchronized (DkSchedulers.class) {
                if (MAIN == null) {
                    MAIN = new DkAndroidMainScheduler<>();
                }
            }
        }
        return (DkScheduler<T>) MAIN;
    }
}
