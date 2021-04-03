/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.menu;

import android.content.Context;
import android.content.res.XmlResourceParser;

import java.util.ArrayList;
import java.util.List;

/**
 * It does not support nested menu for item tag since backtracking-convenience in DKMenuView.
 * So to work with submenu, you maybe use dk_submenu attribute in item tag of xml layout.
 */
public class DkMenuResourceParser {
	/**
	 * @return a list of tags, each tag is 2N-length attributes array (2i-index = key, (2i+1)-index = value).
	 */
	public List<String[]> parse(Context context, int xmlRes) throws Exception {
		List<String[]> tags = new ArrayList<>(32);
		XmlResourceParser parser = context.getResources().getXml(xmlRes);

		int eventType = parser.getEventType();

		while (eventType != XmlResourceParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlResourceParser.START_TAG: {
					// only "item" tag can be processed
					if (!"item".equals(parser.getName())) {
						break;
					}
					int N = parser.getAttributeCount();
					String[] tag = new String[N << 1];
					for (int i = 0, w = 0; i < N; ++i, w = i << 1) {
						tag[w] = parser.getAttributeName(i);
						tag[w + 1] = parser.getAttributeValue(i);
					}
					tags.add(tag);
					break;
				}
				case XmlResourceParser.END_TAG: {
					break;
				}
				case XmlResourceParser.TEXT: {
					break;
				}
			}

			eventType = parser.next();
		}

		return tags;
	}
}
