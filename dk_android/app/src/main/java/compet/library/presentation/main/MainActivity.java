package compet.library.presentation.main;

import android.os.Bundle;

import compet.library.R;
import compet.library.presentation.settingsMap.SettingsMapFragment;
import tool.compet.appbundle.arch.DkActivity;
import tool.compet.appbundle.binder.annotation.DkBindLayout;

@DkBindLayout(value = R.layout.activity_main, containerId = R.id.frag_container)
public class MainActivity extends DkActivity {
	@Override
	protected void onCreateViewModel() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.frag_container, new SettingsMapFragment())
				.commit();
		}
	}
}
