# Core

This is base module which be used at other packages, libs, app.


## Design rules

- Classes under package `tool.compet.core` MUST depend only `Android framework` or other classes at `same level`.

- Classes inside children packages of `tool.compet.core` must NOT depend other classes at `same level`,
they should only depend classes of `Android framework` or classes inside `tool.compet.core`.

- Only `basic` and `common` classes can be put in `tool.compet.core`. That is, classes in this package
will be used at least `2` other modules (packages, libs, app).

- It should provide ONLY basic features, any extension should be developed at other modules.
By default, it contains some below features:
	- datastructure (array list)
	- callback type (runner, caller)
	- datetime (format, util, helper)
	- json (convert json vs plain object)
	- string (string util)
	- array (array util)
	- collection (list, map util)
	- console log (android inside)
	- bitmap (storage, transform)
	- file (crud)
	- stream (async work)
	- reflection (field, method)
	- graphics (drawable)
	- view (animation, gesture)
	- security (crypto, uid)
	- config (app, device config)
	- color (parsing, convert color)
	- object (assert)
	- map (util)
	- util (common)
