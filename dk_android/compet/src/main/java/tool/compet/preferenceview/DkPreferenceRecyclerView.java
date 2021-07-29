/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.preferenceview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Subclass can extend this to implement preference via View.
 */
public abstract class DkPreferenceRecyclerView extends RecyclerView implements DkPreferenceView {
	private ThePreferenceManager preferenceManager;
	private MyPreferenceAdapter preferenceAdapter;
	private final DkPreferenceListener preferenceListener = new DkPreferenceListener() {
		@Override
		public void onPreferenceChanged(String key) {
			DkPreferenceRecyclerView.this.onPreferenceChanged(key);
		}

		@Override
		public void notifyDataSetChanged() {
			DkPreferenceRecyclerView.this.preferenceAdapter.notifyDataSetChanged();
		}
	};

	public DkPreferenceRecyclerView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public DkPreferenceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DkPreferenceRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.preferenceManager = new ThePreferenceManager(context, storage(), preferenceListener);
		this.preferenceAdapter = new MyPreferenceAdapter(preferenceManager.getPreferences());

		// Subclass can override this to customize preference view (recycler view)
		onSetupPreferenceView(context);

		// Subclass can override this to create preference list
		onCreatePreferences(preferenceManager);
	}

	/**
	 * Subclass can override this to setup preference view (RecyclerView)
	 */
	protected void onSetupPreferenceView(Context context) {
		this.setAdapter(preferenceAdapter);
		this.setHasFixedSize(true);
		this.setLayoutManager(new LinearLayoutManager(context));
//		this.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
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

	private static class MyPreferenceAdapter extends Adapter<MyViewHolder> {
		private final List<DkPreference> preferences;

		MyPreferenceAdapter(List<DkPreference> preferences) {
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
