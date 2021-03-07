/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Subclass can extend this to implement preference via View.
 */
public abstract class DkPreferenceView extends RecyclerView implements DkPreferenceInf {
    private ThePreferenceManager preferenceManager;
    private MyAdapter adapter;
    private final MyPreferenceListener listener = new MyPreferenceListener() {
        @Override
        public void onPreferenceChanged(String key) {
            DkPreferenceView.this.onPreferenceChanged(key);
        }

        @Override
        public void notifyDataSetChanged() {
            DkPreferenceView.this.adapter.notifyDataSetChanged();
        }
    };

    public DkPreferenceView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DkPreferenceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DkPreferenceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.preferenceManager = new ThePreferenceManager(context, storage(), listener);
        this.adapter = new MyAdapter(preferenceManager.getPreferences());

        onSetupPreferenceView(context);
        onCreatePreferences(preferenceManager);
    }

    /**
     * Subclass can override this to setup preference view (RecyclerView)
     */
    protected void onSetupPreferenceView(Context context) {
        this.setAdapter(adapter);
        this.setHasFixedSize(true);
        this.setLayoutManager(new LinearLayoutManager(context));
        this.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
    }

    @Override
    public ThePreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    private static class MyViewHolder extends ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void decorate(DkPreference preference) {
            preference.decorateView(itemView);
        }
    }

    private static class MyAdapter extends Adapter<MyViewHolder> {
        private final List<DkPreference> preferences;

        MyAdapter(List<DkPreference> preferences) {
            this.preferences = preferences;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // At below method `getItemViewType()`, we has set viewType as position of the preference in list
            DkPreference preference = preferences.get(viewType);
            return new MyViewHolder(preference.createView(parent.getContext(), parent));
        }

        @Override
        public int getItemViewType(int position) {
            // Consider viewType as position
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.decorate(preferences.get(position));
        }

        @Override
        public int getItemCount() {
            return preferences.size();
        }
    }
}
