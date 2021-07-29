# Compet module

This module contains ALL other modules,
when an app wants a module, it will link to that module.


## Setup

- Because it contains ALL other modules, so just we need links ALL Java-based packages to this.

	```bash
	# Jump to tool/compet folder
	cd /Users/compet/workspace/darkcompet/libraries/android/dk_android/compet/src/main/java/tool/compet

	# Link ALL java based libraries (don't ignore anyone)
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/core4j core4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/stream4j stream4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/reflection4j reflection4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/json4j json4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/database4j database4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/http4j http4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/security4j security4j
	ln -s ~/workspace/darkcompet/libraries/java/dk_java/src/tool/compet/eventbus4j eventbus4j
	```
