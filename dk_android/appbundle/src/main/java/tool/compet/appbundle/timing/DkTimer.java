/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.timing;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static tool.compet.appbundle.timing.DkFrameCallbackProvider.FRAME_DELAY;
import static tool.compet.appbundle.timing.DkFrameCallbackProvider.FRAME_START;

/**
 * 本クラスは、タスクを定期的にスケジューリングした時間エンジンを提供し、TimerとValueAnimatorから由来されたものです。
 * 本来、ValueAnimatorと違う点は、各オブジェクトが固有のframeDelayを持ち、共有されないようになっています。
 * <p></p>
 *
 * <p></p>
 * 基本的に、アニメーションには向いていないですが、目的に応じてsetFrameCallbackProvider()を呼び出すことで
 * フレームコールバックをカストマイズできますので、静的なframeDelayが作れてアニメーションが可能になります。
 * <p></p>
 * デフォルトのフレームコールバックはHandlerのサブクラスなので、start()が呼び出された前にLooperのloop()が
 * 既に実行されている必要があります。
 */
public class DkTimer implements DkFrameCallbackProvider.Callback {
    private static final long DURATION_INFINITE = -1;

    private static final int STATE_STARTED = 1;
    private static final int STATE_RUNNING = 2;
    private static final int STATE_RESUMED = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_CANCELLED = 5;
    private static final int STATE_ENDED = 6;

    private int timerState = STATE_ENDED;

    private long startedTime;
    private long duration = DURATION_INFINITE;
    private long remainDuration = DURATION_INFINITE;
    private DkFrameCallbackProvider provider;
    private ArrayList<Listener> listeners;
    private ArrayList<UpdateListener> updaterListeners;

    public interface Listener {
        void onStart(long uptimeMillis);

        void onResume(long uptimeMillis);

        void onPause(long uptimeMillis);

        void onEnd(long uptimeMillis);

        void onCancel(long uptimeMillis);
    }

    public interface UpdateListener {
        void onUpdate(long uptimeMillis);
    }

    private DkTimer() {
    }

    public static DkTimer newIns() {
        return new DkTimer();
    }

    @Override
    public void onFrame(long frameUptimeMillis) {
        final int state = timerState;

        // 取消、停止、終了の状態には、次のフレームを予約しない
        if (state == STATE_CANCELLED || state == STATE_PAUSED || state == STATE_ENDED) {
            return;
        }

        if (state != STATE_RUNNING) {
            timerState = STATE_RUNNING;
            // 開始から最初のフレームが来るまでの経過時間で引いて残り時間を更新
            if (duration != DURATION_INFINITE) {
                remainDuration -= frameUptimeMillis - startedTime;
            }
        }

        // リスナーに通知する
        if (updaterListeners != null) {
            for (UpdateListener listener : updaterListeners) {
                listener.onUpdate(frameUptimeMillis);
            }
        }

        // 次のフレームを予約してみる
        if (duration == DURATION_INFINITE) {
            getProvider().requestNextFrame(FRAME_DELAY, 0);
        }
        else {
            // 残り時間を更新
            remainDuration -= SystemClock.uptimeMillis() - frameUptimeMillis;

            if (remainDuration > 0) {
                getProvider().requestNextFrame(FRAME_DELAY, 0);
            }
            else {
                timerState = STATE_ENDED;

                if (listeners != null) {
                    for (Listener listener : listeners) {
                        listener.onEnd(frameUptimeMillis);
                    }
                }
            }
        }
    }

    public void start(long delayMillis) {
        timerState = STATE_STARTED;
        startedTime = SystemClock.uptimeMillis();

        remainDuration = duration;

        getProvider()
            .setFrameCallback(this)
            .requestNextFrame(FRAME_START, delayMillis);
    }

    public void resume() {
        if (timerState == STATE_PAUSED) {
            timerState = STATE_RESUMED;
            getProvider().requestNextFrame(FRAME_DELAY, 0);
        }
    }

    public void pause() {
        if (timerState == STATE_STARTED || timerState == STATE_RUNNING) {
            timerState = STATE_PAUSED;
        }
    }

    public void cancel() {
        if (timerState < STATE_CANCELLED) {
            timerState = STATE_CANCELLED;
        }
    }

    public DkTimer addListener(Listener listener) {
        ArrayList<Listener> listeners = this.listeners;
        if (listeners == null) {
            this.listeners = listeners = new ArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    public void removeListener(Listener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void removeAllListeners() {
        if (listeners != null) {
            listeners.clear();
            listeners = null;
        }
    }

    public DkTimer addUpdateListener(UpdateListener listener) {
        ArrayList<UpdateListener> updaterListeners = this.updaterListeners;
        if (updaterListeners == null) {
            this.updaterListeners = updaterListeners = new ArrayList<>();
        }
        if (!updaterListeners.contains(listener)) {
            updaterListeners.add(listener);
        }
        return this;
    }

    public void removeUpdateListener(UpdateListener listener) {
        if (updaterListeners != null) {
            updaterListeners.remove(listener);
        }
    }

    public void removeAllUpdateListeners() {
        if (updaterListeners != null) {
            updaterListeners.clear();
            updaterListeners = null;
        }
    }

    public DkFrameCallbackProvider getProvider() {
        if (provider == null) {
            provider = new MyProvider();
        }
        return provider;
    }

    public DkTimer setProvider(DkFrameCallbackProvider provider) {
        this.provider = provider;
        return this;
    }

    public DkTimer setFrameDelay(long delayMillis) {
        getProvider().setFrameDelay(delayMillis);
        return this;
    }

    public DkTimer setFrameDelay(long delay, TimeUnit timeUnit) {
        getProvider().setFrameDelay(timeUnit.toMillis(delay));
        return this;
    }

    public long getFrameDelay() {
        return getProvider().getFrameDelay();
    }

    public DkTimer setFps(int fps) {
        getProvider().setFrameDelay(1000 / fps);
        return this;
    }

    public int getFps() {
        return (int) (1000 / getProvider().getFrameDelay());
    }

    public DkTimer setDuration(long duration, TimeUnit timeUnit) {
        this.duration = timeUnit.toMillis(duration);
        return this;
    }

    public DkTimer setDuration(long durationMillis) {
        duration = durationMillis;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * フレームコールバックのカストマイズクラス
     */
    private static class MyProvider extends Handler implements DkFrameCallbackProvider {
        private long frameDelay = 10;
        private DkFrameCallbackProvider.Callback frameCallback;
        private long lastRequestTime;

        @Override
        public DkFrameCallbackProvider setFrameDelay(long delayMillis) {
            frameDelay = delayMillis;
            return this;
        }

        @Override
        public long getFrameDelay() {
            return frameDelay;
        }

        @Override
        public MyProvider setFrameCallback(DkFrameCallbackProvider.Callback callback) {
            this.frameCallback = callback;
            return this;
        }

        @Override
        public void requestNextFrame(int frameType, long delayMillis) {
            long nextRequestTime = SystemClock.uptimeMillis() + delayMillis;

            if (frameType == FRAME_DELAY) {
                long delta = frameDelay - (nextRequestTime - lastRequestTime);

                if (delta > 0) {
                    nextRequestTime += delta;
                }
            }

            lastRequestTime = nextRequestTime;
            sendMessageAtTime(Message.obtain(this, frameType), nextRequestTime);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FRAME_START:
                case FRAME_DELAY: {
                    if (frameCallback != null) {
                        frameCallback.onFrame(SystemClock.uptimeMillis());
                    }
                    break;
                }
            }
        }
    }
}
