package compet.library.presentation.nfcReader;

import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import tool.compet.appbundle.arch.DkViewModel;

public class NfcReaderViewModel extends DkViewModel<NfcReaderFragment> {
	NfcAdapter nfcAdapter;

	@Override
	protected void onCreate(FragmentActivity host, @Nullable Bundle savedInstanceState) {
		super.onCreate(host, savedInstanceState);

		nfcAdapter = NfcAdapter.getDefaultAdapter(host);

		if (nfcAdapter == null) {
			//todo quit the app
			return;
		}

		if (nfcAdapter.isEnabled()) {
		}
		else {
			//todo promt enable nfc
		}

		handleIntent();
	}
}
