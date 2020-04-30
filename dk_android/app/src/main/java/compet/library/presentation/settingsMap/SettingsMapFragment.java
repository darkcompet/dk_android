package compet.library.presentation.settingsMap;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import compet.library.R;
import compet.library.presentation.home.AreaMap;
import compet.library.presentation.nfcReader.NfcReaderFragment;
import tool.compet.appbundle.arch.DkFragment;
import tool.compet.appbundle.binder.annotation.DkBindLayout;
import tool.compet.appbundle.binder.annotation.DkBindView;
import tool.compet.core.config.DkConfig;
import tool.compet.core.math.DkMaths;
import tool.compet.core.storage.DkPreferenceStorage;
import tool.compet.core.util.DkStrings;
import tool.compet.core.view.DkViews;

@DkBindLayout(value = R.layout.frag_settings_map)
public class SettingsMapFragment extends DkFragment {
	@DkBindView(R.id.toolbar) private Toolbar toolbar;
	@DkBindView(R.id.edtWidth) private EditText edtWidth;
	@DkBindView(R.id.edtHeight) private EditText edtHeight;
	@DkBindView(R.id.btnApplyDimension) private Button btnApplyDimension;
	@DkBindView(R.id.ibtnUndo) private ImageButton ibtnUndo;
	@DkBindView(R.id.ibtnRedo) private ImageButton ibtnRedo;
	@DkBindView(R.id.ibtnAddPoint) private ImageButton ibtnAddPoint;
	@DkBindView(R.id.ibtnAddLine) private ImageButton ibtnAddLine;
	@DkBindView(R.id.setupMapView) private SettingsMapView mapView;
	@DkBindView(R.id.btnClear) private Button btnClear;
	@DkBindView(R.id.btnRegister) private Button btnRegister;

	private SettingsMapViewModel vm;

	@Override
	protected void onCreateViewModel() {
		vm = registerViewModel(SettingsMapViewModel.class);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		toolbar.setTitle("地図の作成・登録");

		if (DkPreferenceStorage.getIns().contains(SettingsMap.class)) {
			SettingsMap map = DkPreferenceStorage.getIns().load(SettingsMap.class);

			if (map != null) {
				edtWidth.setText(String.valueOf(map.areaWidth));
				edtHeight.setText(String.valueOf(map.areaHeight));

				mapView.setupMapFromCache(map.areaWidth, map.areaHeight, map.getActions());
			}
		}

		btnApplyDimension.setOnClickListener(v -> {
			hideSoftKeyboard();

			float w = DkMaths.parseFloat(edtWidth.getText().toString());
			float h = DkMaths.parseFloat(edtHeight.getText().toString());

			mapView.setAreaDimension(w, h, () -> {
				String msg = DkStrings.format("現地図の頂点の配置(点分布等)では、横縦比(%.1f : %.1f)の領域に入り切れません。" +
					"いくつかの点を削除してください。", w, h);
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			});
		});

		int normalColor = Color.LTGRAY;
		int pressedColor = Color.parseColor("#669999");

		ImageButton[] items = {ibtnUndo, ibtnRedo, ibtnAddPoint, ibtnAddLine};

		for (ImageButton item : items) {
			DkViews.injectStateListDrawable(item, 8 * DkConfig.device.density,
				new int[] {normalColor, pressedColor},
				new boolean[] {true, true, true, true});
		}

		ibtnUndo.setOnClickListener(v -> {
			if (!mapView.undo()) {
				Toast.makeText(context, "戻る項目がありません", Toast.LENGTH_SHORT).show();
			}
		});

		ibtnRedo.setOnClickListener(v -> {
			if (!mapView.redo()) {
				Toast.makeText(context, "進む項目がありません", Toast.LENGTH_SHORT).show();
			}
		});

		final ImageButton[] img_btns = {ibtnAddPoint, ibtnAddLine};
		final int[] img_modes = {SettingsMapView.MODE_ADD_POINT, SettingsMapView.MODE_ADD_LINE};

		for (int i = img_btns.length - 1; i >= 0; --i) {
			ImageButton img_btn = img_btns[i];
			int img_mode = img_modes[i];

			img_btn.setOnClickListener(v -> {
				if (mapView.mode == img_mode) {
					mapView.mode = -1;

					for (ImageButton item : img_btns) {
						item.setBackgroundColor(normalColor);
					}
				}
				else {
					mapView.mode = img_mode;

					for (ImageButton item : img_btns) {
						item.setBackgroundColor(normalColor);
					}

					img_btn.setBackgroundColor(pressedColor);
				}
			});
		}

		btnClear.setOnClickListener(v -> {
			mapView.clear();

			for (ImageButton item : items) {
				item.setBackgroundColor(normalColor);
			}
		});

		btnRegister.setOnClickListener(v -> {
			float areaWidth = mapView.getAreaWidth();
			float areaHeight = mapView.getAreaHeight();

			SettingsMap map = new SettingsMap();
			map.prepareCaching(areaWidth, areaHeight, mapView.actions);

			DkPreferenceStorage.getIns().save(map);

			ArrayList<Point> points = mapView.getPoints();
			RectF bounds = mapView.getBounds();

			float[][] vertices = new float[points.size()][2];
			float boundsWidth = bounds.right - bounds.left;
			float boundsHeight = bounds.bottom - bounds.top;

			for (int i = points.size() - 1; i >= 0; --i) {
				Point p = points.get(i);

				vertices[i][0] = (p.x - bounds.left) * areaWidth / boundsWidth;
				vertices[i][1] = (p.y - bounds.top) * areaHeight / boundsHeight;
			}

			AreaMap areaMap = new AreaMap();
			areaMap.width = areaWidth;
			areaMap.height = areaHeight;
			areaMap.vertices = vertices;

			DkPreferenceStorage.getIns().save(areaMap);

			getParentNavigator().beginTransaction()
				.replace(NfcReaderFragment.newIns())
				.commit();
		});
	}
}
