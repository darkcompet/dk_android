/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.stream;

/**
 * Observable.subscribe()で登録されたObserverをどんどん上のObservableに伝搬していきます。
 * 上の親Observerに何かあったとき、子Observerに下記のような関数を通して通知します。
 */
public interface DkObserver<T> {
	/**
	 * タスクが実行用スレッド(通常はバックグラウンドスレッド)に移る直前に、実行支配可能のControllableが
	 * この関数を通じて子Observerに渡します。子Observerでは、いつでもタスクを支配(キャンセル、再生、停止等)できます。
	 * <p></p>
	 * 注意：この関数はストリーム開始直後、何回も呼ばれ、ストリームの制御のチャンスをユーザーにあげます。
	 *
	 * @throws Exception This was wrapped with try/catch block at parent node, so normally,
	 *                   child node does not need to try/catch this when parent passed down this to.
	 */
	void onSubscribe(DkControllable controllable) throws Exception;

	/**
	 * 新しいアイテムが来たとき、親Observerがこの関数を通じて、子Observerに通知します。
	 * <p></p>
	 * 注意：この関数はアイテム数によって、何回もコールされることがあります。
	 *
	 * @throws Exception This was wrapped with try/catch block at parent node, so normally,
	 *                   child node does not need to try/catch this when parent passed down this to.
	 */
	void onNext(T item) throws Exception;

	/**
	 * エラー等が発生したとき、親Observerがこの関数を通じて、子Observerに通知します。
	 * <p></p>
	 * 注意：この関数は最大一回だけコールされます。また、呼ばれたことがあれば、onComplete()が呼ばれなくなります。
	 * This was not wrapped with try/catch block, so child node should handle error itself since
	 * parent node just pass exception without try/catch enabled.
	 */
	void onError(Throwable e);

	/**
	 * キャンセル命令、エラー等がなく、全てのアイテムが正常に処理できたら、この関数が呼び出されます。
	 * <p></p>
	 * 注意：#onComplete() か #onError() のどちらかが呼び出されます。
	 *
	 * @throws Exception This was wrapped with try/catch block at parent node, so normally,
	 *                   child node does not need to try/catch this when parent passed down this to.
	 */
	void onComplete() throws Exception;

	/**
	 * エラー等の有無に関わらず、全てのアイテムが処理できたら、この関数が呼び出されます。
	 * <p></p>
	 * 注意：この関数は一回だけ呼び出されます。
	 * This was not wrapped with try/catch block, so child node should handle error itself since
	 * parent node just pass exception without try/catch enabled.
	 */
	void onFinal();
}
