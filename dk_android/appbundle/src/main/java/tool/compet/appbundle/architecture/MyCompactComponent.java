/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

public class MyCompactComponent {
    // Real component
    final Object obj;

    // Indicates all component-fields inside it is not yet initialized.
    boolean needInitialize;

    MyCompactComponent(Object component) {
        this.obj = component;
        this.needInitialize = true;
    }
}
