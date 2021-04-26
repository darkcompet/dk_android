///*
// * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
// */
//
//package tool.compet.packages.location;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.text.style.CharacterStyle;
//import android.text.style.StyleSpan;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.common.data.DataBufferUtils;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.AutocompletePrediction;
//import com.google.android.gms.location.places.AutocompletePredictionBuffer;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.maps.model.LatLngBounds;
//
//import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//
//public class DkPlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
//	private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
//	private final ArrayList<AutocompletePrediction> autocompletePredict = new ArrayList<>();
//	private final GoogleApiClient googleApiClient;
//	private LatLngBounds bounds;
//	private final AutocompleteFilter placeFilter;
//
//	public DkPlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter) {
//		super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
//		this.googleApiClient = googleApiClient;
//		this.bounds = bounds;
//		this.placeFilter = filter;
//	}
//
//	public void setBounds(LatLngBounds bounds) {
//		this.bounds = bounds;
//	}
//
//	@Override
//	public int getCount() {
//		return autocompletePredict.size();
//	}
//
//	@Override
//	public AutocompletePrediction getItem(int pos) {
//		return (pos >= 0 && pos < autocompletePredict.size()) ? autocompletePredict.get(pos) : null;
//	}
//
//	@NonNull
//	@Override
//	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
//		View view = super.getView(position, convertView, parent);
//
//		AutocompletePrediction item = getItem(position);
//
//		TextView textView1 = view.findViewById(android.R.id.text1);
//		TextView textView2 = view.findViewById(android.R.id.text2);
//		textView1.setText(item != null ? item.getPrimaryText(STYLE_BOLD) : "null");
//		textView2.setText(item != null ? item.getSecondaryText(STYLE_BOLD) : "null");
//
//		return view;
//	}
//
//	@NonNull
//	@Override
//	public Filter getFilter() {
//		return new Filter() {
//			@Override
//			protected FilterResults performFiltering(CharSequence constraint) {
//				FilterResults results = new FilterResults();
//				if (constraint != null) {
//					autocompletePredict.clear();
//					autocompletePredict.addAll(getAutocomplete(constraint));
//					results.values = autocompletePredict;
//					results.count = autocompletePredict.size();
//				}
//				return results;
//			}
//
//			@Override
//			protected void publishResults(CharSequence constraint, FilterResults results) {
//				if (results != null && results.count > 0) {
//					notifyDataSetChanged();
//				}
//				else {
//					notifyDataSetInvalidated();
//				}
//			}
//
//			@Override
//			public CharSequence convertResultToString(Object resultValue) {
//				if (resultValue instanceof AutocompletePrediction) {
//					return ((AutocompletePrediction) resultValue).getFullText(null);
//				}
//				else {
//					return super.convertResultToString(resultValue);
//				}
//			}
//		};
//	}
//
//	private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
//		if (googleApiClient.isConnected()) {
//			PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
//				.getAutocompletePredictions(googleApiClient, constraint.toString(), bounds, placeFilter);
//
//			AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
//
//			final Status status = autocompletePredictions.getStatus();
//			if (!status.isSuccess()) {
//				autocompletePredictions.release();
//				return null;
//			}
//
//			return DataBufferUtils.freezeAndClose(autocompletePredictions);
//		}
//		return null;
//	}
//}
