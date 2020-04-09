package at.wirecube.additiveanimations.additive_animator.view_visibility;

import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.wirecube.additiveanimations.additive_animator.animation_set.AnimationAction;
import at.wirecube.additiveanimations.additive_animator.animation_set.AnimationState;


public class ViewVisibilityAnimation extends AnimationState<View> {

    /**
     * Sets the visibility of the view to View.VISIBLE and fades it in.
     */
    public static ViewVisibilityAnimation fadeIn() {
        return new ViewVisibilityAnimation(View.VISIBLE,
                Collections.singletonList(new Animation<View>(View.ALPHA, 1f)));
    }

    /**
     * Sets the visibility of the view to View.VISIBLE, fades it in and also set its translationX and translationY back to 0.
     */
    public static ViewVisibilityAnimation fadeInAndTranslateBack() {
        return new ViewVisibilityAnimation(View.VISIBLE,
                Arrays.asList(
                        new Animation<>(View.ALPHA, 1f),
                        new Animation<>(View.TRANSLATION_X, 0f),
                        new Animation<>(View.TRANSLATION_Y, 0f)));
    }

    /**
     * Fades out the target and then sets its visibility to either View.INVISIBLE or GONE, depending on the gone parameter.
     */
    public static ViewVisibilityAnimation fadeOut(boolean gone) {
        return new ViewVisibilityAnimation(gone ? View.GONE : View.INVISIBLE,
                Collections.singletonList(new Animation<>(View.ALPHA, 0f)));
    }

    /**
     * Fades out the target and then sets its visibility to either View.INVISIBLE or GONE, depending on the gone parameter.
     * Also moves the view by xTranslation and yTranslation.
     */
    public static ViewVisibilityAnimation fadeOutAndTranslate(boolean gone, float xTranslation, float yTranslation) {
        return new ViewVisibilityAnimation(gone ? View.GONE : View.INVISIBLE,
                Arrays.asList(
                        new Animation<>(View.ALPHA, 0f),
                        new Animation<>(View.TRANSLATION_X, xTranslation),
                        new Animation<>(View.TRANSLATION_Y, yTranslation)));
    }

    /**
     * Fades out the target and then sets its visibility to either View.INVISIBLE or GONE, depending on the gone parameter.
     * Also moves the view horizontally by xTranslation.
     */
    public static ViewVisibilityAnimation fadeOutAndTranslateX(boolean gone, float xTranslation) {
        return new ViewVisibilityAnimation(gone ? View.GONE : View.INVISIBLE,
                Arrays.asList(
                        new Animation<>(View.ALPHA, 0f),
                        new Animation<>(View.TRANSLATION_X, xTranslation)));
    }

    /**
     * Fades out the target and then sets its visibility to either View.INVISIBLE or GONE, depending on the gone parameter.
     * Also moves the view vertically by yTranslation.
     */
    public static ViewVisibilityAnimation fadeOutAndTranslateY(boolean gone, float yTranslation) {
        return new ViewVisibilityAnimation(gone ? View.GONE : View.INVISIBLE,
                Arrays.asList(
                        new Animation<>(View.ALPHA, 0f),
                        new Animation<>(View.TRANSLATION_Y, yTranslation)));
    }


    private static int GONE_STATE_ID = 100001;
    private static int VISIBLE_STATE_ID = 200001;
    private static int INVISIBLE_STATE_ID = 300001;

    private int mStateId;
    private List<Animation<View>> mAnimations;
    private AnimationEndAction<View> mEndAction;
    private AnimationStartAction<View> mStartAction;

    public ViewVisibilityAnimation(int visibility, List<Animation<View>> animations) {
        switch (visibility) {
            case View.VISIBLE:
                mStateId = VISIBLE_STATE_ID;
                mStartAction = view -> view.setVisibility(View.VISIBLE);
                break;
            case View.INVISIBLE:
                mStateId = INVISIBLE_STATE_ID;
                mEndAction = (view, wasCancelled) -> view.setVisibility(View.INVISIBLE);
                break;
            case View.GONE:
                mStateId = GONE_STATE_ID;
                mEndAction = (view, wasCancelled) -> view.setVisibility(View.GONE);
                break;
        }
        mAnimations = animations;
    }

    @Override
    public List<Animation<View>> getAnimations() {
        return mAnimations;
    }

    @Override
    public int getId() {
        return mStateId;
    }

    @Override
    public AnimationEndAction<View> getAnimationEndAction() {
        return mEndAction;
    }

    @Override
    public AnimationStartAction<View> getAnimationStartAction() {
        return mStartAction;
    }
}
