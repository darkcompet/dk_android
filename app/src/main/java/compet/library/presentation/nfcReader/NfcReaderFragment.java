package compet.library.presentation.nfcReader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import compet.library.R;
import tool.compet.appbundle.arch.DiFragment;
import tool.compet.appbundle.arch.DkFragment;
import tool.compet.appbundle.binder.annotation.DkBindLayout;
import tool.compet.appbundle.binder.annotation.DkBindView;

@DkBindLayout(value = R.layout.frag_nfc_reader)
public class NfcReaderFragment extends DkFragment {
	@DkBindView(R.id.toolbar) private Toolbar toolbar;
	@DkBindView(R.id.tvGuideline) private TextView tvGuideline;

	private NfcReaderViewModel vm;

	public static DiFragment newIns() {
		return new NfcReaderFragment();
	}

	@Override
	protected void onCreateViewModel() {
		vm = registerViewModel(NfcReaderViewModel.class);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		toolbar.setTitle("会員認証");
	}
}
