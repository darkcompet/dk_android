package compet.library.presentation;

import androidx.databinding.ViewDataBinding;

import tool.compet.core.compact.DkCompactActivity;
import tool.compet.core.compact.DkCompactLogic;

public abstract class AppActivity<L extends DkCompactLogic, M, B extends ViewDataBinding>
	extends DkCompactActivity<L, M, B> {
}
