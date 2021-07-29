package compet.library.presentation;

import androidx.databinding.ViewDataBinding;

import tool.compet.core.compact.DkCompactFragment;
import tool.compet.core.compact.DkCompactLogic;

public abstract class AppFragment<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends DkCompactFragment<L, M, B> {
}
