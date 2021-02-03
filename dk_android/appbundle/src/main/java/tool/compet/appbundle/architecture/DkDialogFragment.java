/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tool.compet.appbundle.architecture.compact.DkCompactFragment;

public class DkDialogFragment extends DkCompactFragment implements DialogInterface.OnCancelListener,
    DialogInterface.OnDismissListener {

    @Override
    public boolean isRetainInstance() {
        return false;
    }

    @Override
    public int layoutResourceId() {
        return 0;
    }

    @Override
    public int fragmentContainerId() {
        return 0;
    }

    /**
     * Normal dialog.
     */
    public static final int STYLE_NORMAL = 0;

    /**
     * Not include a title area.
     */
    public static final int STYLE_NO_TITLE = 1;

    /**
     * Not draw any frame at all; the view hierarchy returned by {@link #onCreateView}
     * is entirely responsible for drawing the dialog.
     */
    public static final int STYLE_NO_FRAME = 2;

    /**
     * Like {@link #STYLE_NO_FRAME}, but also disables all input to the dialog.
     * The user can not touch it, and its window will not receive input focus.
     */
    public static final int STYLE_NO_INPUT = 3;

    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_STYLE = "android:style";
    private static final String SAVED_THEME = "android:theme";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";

    private Handler handler;

    // Info for dialog
    private Dialog dialog;
    private int style = STYLE_NORMAL;
    private boolean isDismissed;
    private boolean isViewDestroyed;
    private boolean showDialog = true;
    private boolean isCancelable = true;
    private int theme;
    private int backStackId;
    private boolean isShownByMe;
    private Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                onDismiss(dialog);
            }
        }
    };

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        if (!showDialog) {
            return super.onGetLayoutInflater(savedInstanceState);
        }

        dialog = onCreateDialog();

        if (dialog != null) {
            setupDialog(dialog, style);
            return (LayoutInflater) dialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return super.onGetLayoutInflater(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (showDialog) {
            View view = getView();

            if (view != null) {
                if (view.getParent() != null) {
                    throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
                }
                dialog.setContentView(view);
            }

            final Activity activity = getActivity();

            if (activity != null) {
                dialog.setOwnerActivity(activity);
            }

            dialog.setCancelable(isCancelable);
            dialog.setOnCancelListener(this);
            dialog.setOnDismissListener(this);

            if (savedInstanceState != null) {
                Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);

                if (dialogState != null) {
                    dialog.onRestoreInstanceState(dialogState);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (dialog != null) {
            dialog.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (dialog != null) {
            // Set removed here because this dismissal is just to hide
            // the dialog -- we don't want this to cause the fragment to
            // actually be removed.
            isViewDestroyed = true;

            // Instead of waiting for a posted onDismiss(), null out
            // the listener and call onDismiss() manually to ensure
            // that the callback happens before onDestroy()
            dialog.setOnDismissListener(null);
            dialog.dismiss();

            if (!isDismissed) {
                // Don't send a second onDismiss() callback if we've already
                // dismissed the dialog manually in dismissInternal()
                onDismiss(dialog);
            }

            dialog = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (dialog != null) {
            Bundle dialogState = dialog.onSaveInstanceState();
            outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
        }
        if (style != STYLE_NORMAL) {
            outState.putInt(SAVED_STYLE, style);
        }
        if (theme != 0) {
            outState.putInt(SAVED_THEME, theme);
        }
        if (!isCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, isCancelable);
        }
        if (!showDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, showDialog);
        }
        if (backStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, backStackId);
        }
    }

    @Override // dialog be cancelled
    public void onCancel(DialogInterface dialog) {
    }

    @Override // dialog be dismissed
    public void onDismiss(DialogInterface dialog) {
        if (!isViewDestroyed) {
            // Note: we need to use allowStateLoss, because the dialog
            // dispatches this asynchronously so we can receive the call
            // after the activity is paused.  Worst case, when the user comes
            // back to the activity they see the dialog again.
            dismissInternal(true, true);
        }
    }

    private void dismissInternal(boolean allowStateLoss, boolean fromOnDismiss) {
        if (isDismissed) {
            return;
        }

        isDismissed = true;
        isShownByMe = false;

        if (dialog != null) {
            // Instead of waiting for a posted onDismiss(), null out
            // the listener and call onDismiss() manually to ensure
            // that the callback happens before onDestroy()
            dialog.setOnDismissListener(null);
            dialog.dismiss();

            if (!fromOnDismiss) {
                // onDismiss() is always called on the main thread, so
                // we mimic that behavior here. The difference here is that
                // we don't post the message to ensure that the onDismiss()
                // callback still happens before onDestroy()
                if (Looper.myLooper() == handler.getLooper()) {
                    onDismiss(dialog);
                }
                else {
                    handler.post(dismissRunnable);
                }
            }
        }

        isViewDestroyed = true;

        if (backStackId >= 0) {
            requireFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            backStackId = -1;
        }
        else {
            FragmentTransaction ft = requireFragmentManager().beginTransaction();
            ft.remove(this);

            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            }
            else {
                ft.commit();
            }
        }
    }

    protected Dialog onCreateDialog() {
        dialog = new Dialog(context);
        return dialog;
    }

    private void setupDialog(Dialog dialog, int style) {
        switch (style) {
            case STYLE_NO_INPUT:
                Window window = dialog.getWindow();

                if (window != null) {
                    window.addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                // fall through...
            case STYLE_NO_FRAME:
            case STYLE_NO_TITLE:
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
