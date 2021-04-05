/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

import tool.compet.appbundle.R;
import tool.compet.appbundle.architecture.preference.DkPreferenceStorage;
import tool.compet.core.DkRunner1;

/**
 * 本クラス、メニューアイテムを表すToolbar、DrawerLayout、BottomView等のようなビューで使われる
 * ListViewの拡張クラスです。
 * <p></p>
 * 基本的に、設定用SharedPreferenceと同期しますので、MenuItemViewをデコレーションしたい場合は、
 * setViewAdapter()、setModelCreator()を呼び出して、ビューとモデルともにカストマイズしてください。
 */
public abstract class DkMenuView<T extends DkMenuItemModel> extends ListView {
	protected abstract DkPreferenceStorage storage();

	private Context mContext;
	private int mCurMenuRes;
	private ArrayAdapter<T> mAdapter;
	private DKViewAdapter mViewAdapter;
	private Callable<T> mModelCreator;
	private DkRunner1<T> mOnItemClickListener;

	public interface DKViewAdapter<T extends DkMenuItemModel> {
		View getView(Context context, int pos, @Nullable View view, @NonNull ViewGroup parent, T model);
	}

	public DkMenuView(Context context) {
		this(context, null);
	}

	public DkMenuView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DkMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mContext = getContext();

		mAdapter = new ArrayAdapter<T>(context, android.R.layout.simple_list_item_single_choice) {
			@NonNull
			@Override
			@SuppressWarnings("unchecked")
			public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
				return getViewAdapter().getView(context, position, view, parent, mAdapter.getItem(position));
			}
		};

		super.setAdapter(mAdapter);
		super.setOnItemClickListener((parent, view, position, id) -> {
			T model = mAdapter.getItem(position);
			if (model == null) {
				return;
			}
			boolean canForward = false;
			if (model.hasChildMenu()) {
				canForward = forwardMenu(model.getChildMenuRes());
			}
			if (!canForward && mOnItemClickListener != null) {
				mOnItemClickListener.run(mAdapter.getItem(position));
			}
		});
	}

	public DkMenuView<T> setViewAdapter(DKViewAdapter<T> viewAdapter) {
		mViewAdapter = viewAdapter;
		return this;
	}

	public DkMenuView setModelCreator(Callable<T> modelCreator) {
		mModelCreator = modelCreator;
		return this;
	}

	public DkMenuView setOnItemClickListener(DkRunner1<T> onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
		return this;
	}

	@SuppressWarnings("unchecked")
	public DkMenuView<T> inflateMenu(int menuRes, boolean notifyDataSetChanged) {
		if (menuRes <= 0) {
			return this;
		}

		List<T> nextModels = DkMenuInflater.getIns()
			.setModelCreator(mModelCreator)
			.inflate(mContext, menuRes);

		if (nextModels == null) {
			return this;
		}

		mCurMenuRes = menuRes;

		mAdapter.clear();
		mAdapter.addAll(nextModels);
		if (notifyDataSetChanged) {
			mAdapter.notifyDataSetChanged();
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	private boolean forwardMenu(int nextMenuRes) {
		if (nextMenuRes <= 0) {
			return false;
		}

		List<T> nextModels = DkMenuInflater.getIns()
			.setModelCreator(mModelCreator)
			.inflate(mContext, nextMenuRes);

		if (nextModels == null) {
			return false;
		}

		List<T> curModels = DkMenuInflater.getIns()
			.setModelCreator(mModelCreator)
			.inflate(mContext, mCurMenuRes);
		int parentMenuRes = curModels != null && curModels.size() > 0 ? nextModels.get(0).getParentMenuRes() : 0;

		if (parentMenuRes > 0) {
			for (T nextModel : nextModels) {
				nextModel.setParentMenuRes(parentMenuRes);
			}
		}

		inflateMenu(nextMenuRes, true);

		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean backMenu() {
		List<T> curModels = DkMenuInflater.getIns()
			.setModelCreator(mModelCreator)
			.inflate(mContext, mCurMenuRes);

		int parentMenuRes = curModels != null && curModels.size() > 0 ? curModels.get(0).getParentMenuRes() : 0;

		if (parentMenuRes > 0) {
			inflateMenu(parentMenuRes, true);
			return true;
		}

		return false;
	}

	public DKViewAdapter getViewAdapter() {
		if (mViewAdapter == null) {
			mViewAdapter = new DefaultViewAdapter();
		}
		return mViewAdapter;
	}

	private class DefaultViewAdapter implements DKViewAdapter {
		@Override
		public View getView(Context context, int pos, @Nullable View view,
			@NonNull ViewGroup parent, DkMenuItemModel model) {

			if (view == null) {
				view = View.inflate(context, R.layout.dk_menu_item, null);
			}

			final ImageView ivTitle = view.findViewById(R.id.ivTitle);
			if (model.hasIconTitleRes()) {
				ivTitle.setImageResource(model.getIconTitleRes());
			}
			else {
				ivTitle.setImageDrawable(null);
			}

			final TextView tvTitle = view.findViewById(R.id.dk_dialog_tv_title);

			if (model.hasTitle()) {
				tvTitle.setText(model.getTitle());
			}
			else {
				tvTitle.setText("");
			}

			final ImageView ivStatus = view.findViewById(R.id.ivStatus);
			boolean notNeedStatus = true;

			if (model.hasSettingPreference()) {
				String value = storage().getString("" + model.getSettingPrefKey());

				if (value != null && value.equals(model.getSettingPrefTagValue())) {
					if (model.hasIconStatusRes()) {
						notNeedStatus = false;
						ivStatus.setImageResource(model.getIconStatusRes());
					}
				}
			}

			if (notNeedStatus) {
				ivStatus.setImageDrawable(null);
			}

			return view;
		}
	}
}
