/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tool.compet.appbundle.DkCompactActivity;
import tool.compet.appbundle.DkCompactViewLogic;

public abstract class DkPreferenceActivity<VL extends DkCompactViewLogic> extends DkCompactActivity<VL> implements DkPreference {
	/**
	 * Caller must provide preference view id (id of recycler view)
	 */
	protected abstract int preferenceViewId();

	protected ThePreferenceManager preferenceManager;
	protected MyAdapter adapter;
	private final MyPreferenceListener listener = new MyPreferenceListener() {
		@Override
		public void onPreferenceChanged(String key) {
			DkPreferenceActivity.this.onPreferenceChanged(key);
		}

		@Override
		public void notifyDataSetChanged() {
			DkPreferenceActivity.this.adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferenceManager = new ThePreferenceManager(context, storage(), listener);
		adapter = new MyAdapter(preferenceManager.getPreferences());

		RecyclerView prefView = layout.findViewById(preferenceViewId());
		onSetupPreferenceView(prefView);
		onCreatePreferences(preferenceManager);
	}

	/**
	 * Subclass can override this to setup preference view (RecyclerView)
	 */
	protected void onSetupPreferenceView(RecyclerView prefView) {
		prefView.setAdapter(adapter);
		prefView.setHasFixedSize(true);
		prefView.setLayoutManager(new LinearLayoutManager(context));
		prefView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
	}

	@Override
	public ThePreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	private static class MyViewHolder extends RecyclerView.ViewHolder {
		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
		}

		void decorate(MyBasePreference preference) {
			preference.decorateView(itemView);
		}
	}

	private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
		private final List<MyBasePreference> preferences;

		MyAdapter(List<MyBasePreference> preferences) {
			this.preferences = preferences;
		}

		@NonNull
		@Override
		public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			// At below method `getItemViewType()`, we has set viewType as position of the preference in list
			MyBasePreference preference = preferences.get(viewType);
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
