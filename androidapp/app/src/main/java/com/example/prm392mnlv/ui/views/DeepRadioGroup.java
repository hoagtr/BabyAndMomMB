package com.example.prm392mnlv.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>Do <b>NOT</b> manually call
 * {@link CompoundButton#setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener) setOnCheckedChangeListener}
 * on any {@link RadioButton} within this View's hierarchy, lest the multiple-exclusion mechanism breaks.</p>
 */
public class DeepRadioGroup extends RadioGroup {

    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private RecursivePassThroughHierarchyChangeListener mRecursivePassThroughListener;

    private final Set<Integer> mIds = new HashSet<>();

    public DeepRadioGroup(Context context) {
        super(context);
        override();
    }

    public DeepRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        override();
    }

    private void override() {
        try {
            Field field = Objects.requireNonNull(getClass().getSuperclass()).getDeclaredField("mChildOnCheckedChangeListener");
            field.setAccessible(true);
            mChildOnCheckedChangeListener = (CompoundButton.OnCheckedChangeListener) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mRecursivePassThroughListener = new RecursivePassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mRecursivePassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mRecursivePassThroughListener.mOnHierarchyChangeListener = listener;
    }

    private class RecursivePassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewAdded(View parent, View child) {
            trySetCheckedStateListenerRecursive(child, mChildOnCheckedChangeListener);
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewRemoved(View parent, View child) {
            trySetCheckedStateListenerRecursive(child, null);
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }

        private void trySetCheckedStateListenerRecursive(View view, CompoundButton.OnCheckedChangeListener listener) {
            if (view instanceof RadioButton) {
                int id = view.getId();
                // generates an id if it's missing
                // or if it clashes
                if (id == View.NO_ID || mIds.contains(id)) {
                    id = View.generateViewId();
                    view.setId(id);
                }
                if (listener != null) mIds.add(id);
                else mIds.remove(id);
                ((RadioButton) view).setOnCheckedChangeListener(
                        listener);
            }
            if (!(view instanceof ViewGroup)) return;
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                trySetCheckedStateListenerRecursive(viewGroup.getChildAt(i), listener);
            }
        }
    }
}
