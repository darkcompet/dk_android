package compet.library.presentation.main;

import compet.library.R;
import compet.library.databinding.ActivityMainBinding;
import compet.library.presentation.AppActivity;

public class MainActivity extends AppActivity<MainLogic, MainModel, ActivityMainBinding> {
	@Override
	public int layoutResourceId() {
		return R.layout.activity_main;
	}

	@Override
	public int fragmentContainerId() {
		return R.id.root;
	}
}
