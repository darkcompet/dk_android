/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

import java.util.concurrent.Callable;

import tool.compet.core.DkLogs;

/**
 * This node can pause, resume, cancel the task.
 */
public class DkControllable<T> extends MyControllable implements Callable<T>, DkObserver<T> {
	protected final DkObserver<T> child;

	public DkControllable(DkObserver<T> child) {
		this.child = child;
	}

	@Override
	public T call() {
		throw new RuntimeException("Must implement this method");
	}

	@Override
	public void onSubscribe(DkControllable controllable) throws Exception {
		if (controllable == this) {
			DkLogs.complain(this, "Wrong implementation ! God observer must be parentless");
		}
		this.parent = controllable;
		this.child.onSubscribe(controllable);
	}

	@Override
	public void onNext(T result) throws Exception {
		child.onNext(result);
	}

	@Override
	public void onError(Throwable e) {
		child.onError(e);
	}

	@Override
	public void onComplete() throws Exception {
		child.onComplete();
	}

	@Override
	public void onFinal() {
		child.onFinal();
	}
}
