# core

This is base (core) library of other libs, apps.


## Rules

- Only common classes can be put in `tool.compet.core`. That is, classes in this package
will be used at other packages.

- Each package from `tool.compet.core` is independent, a package should NOT use classes at
other packages, instead of that, they should use classes of `tool.compet.core` directly, or
classes inside inner (nested) packages.