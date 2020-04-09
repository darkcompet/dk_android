/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class prevents fast continuous clicking. Next click
 * will be available again after specific duration elapsed.
 */
public class DkOneClickButton extends AppCompatButton implements View.OnClickListener {
	private long lastClickTime;
	private long duration = 1000L;
	private OnClickListener onClickListener;

	public DkOneClickButton(Context context) {
		super(context);
	}

	public DkOneClickButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DkOneClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFinishInflate() {
		super.setOnClickListener(this);
		super.onFinishInflate();
	}

	@Override
	public void setOnClickListener(@Nullable OnClickListener clickListener) {
		this.onClickListener = clickListener;
	}

	@Override
	public void onClick(View view) {
		long now = System.currentTimeMillis();

		if (now - lastClickTime >= duration) {
			lastClickTime = now;

			if (onClickListener != null) {
				onClickListener.onClick(this);
			}
		}
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
