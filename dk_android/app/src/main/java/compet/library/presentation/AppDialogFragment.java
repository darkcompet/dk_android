package compet.library.presentation;

import androidx.databinding.ViewDataBinding;

import tool.compet.core.compact.DkCompactDialogFragment;
import tool.compet.core.compact.DkCompactFragment;
import tool.compet.core.compact.DkCompactLogic;

public abstract class AppDialogFragment<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends DkCompactDialogFragment<L, M, B> {
}
