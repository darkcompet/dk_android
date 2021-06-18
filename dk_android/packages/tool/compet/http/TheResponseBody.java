/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import tool.compet.core.DkByteArrayList;
import tool.compet.core.DkConst;
import tool.compet.core.DkJsonHelper;
import tool.compet.core.DkLogs;
import tool.compet.core.DkUtils;
import tool.compet.core.graphics.DkBitmaps;

import static tool.compet.core.BuildConfig.DEBUG;

public class TheResponseBody {
	private final HttpURLConnection connection;

	TheResponseBody(HttpURLConnection connection) {
		this.connection = connection;
	}

	/**
	 * Decode body to array of byte.
	 * This method auto disconnect remote server after called even error occured.
	 *
	 * @return Body as byte[] if succeed. Otherwise null.
	 */
	public byte[] bytes() {
		try {
			DkByteArrayList byteList = new DkByteArrayList();
			byte[] buffer = new byte[1 << 12];
			int readCount;

			while ((readCount = connection.getInputStream().read(buffer)) != -1) {
				byteList.addRange(buffer, 0, readCount);
			}

			if (DEBUG) {
				DkLogs.info(this, "Got response as bytes, count: %d", byteList.size());
			}

			return byteList.toArray();
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			return null;
		}
		finally {
			connection.disconnect();
		}
	}

	/**
	 * Decode stream to string with default charset utf-8.
	 * This method auto disconnect remote server after called even error occured.
	 *
	 * @return Body as string if succeed. Otherwise null.
	 */
	public String string() {
		StringBuilder builder = new StringBuilder(1 << 12);
		String line;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			while ((line = br.readLine()) != null) {
				builder.append(line).append(DkConst.LS);
			}
		}
		catch (Exception e) {
			DkLogs.error(DkLogs.class, e);
			return null;
		}
		finally {
			connection.disconnect();
		}

		if (DEBUG) {
			DkLogs.info(this, "Got response as string: %s", builder.toString());
		}

		return builder.toString();
	}

	/**
	 * Consider body as json and Decode it to object which has type of given class.
	 * This method automatically close the stream and disconnect remote server after called.
	 *
	 * @return Body as object if succeed. Otherwise null.
	 */
	public <R> R json(Class<R> responseClass) {
		try {
			String json = DkUtils.stream2string(connection.getInputStream());

			if (DEBUG) {
				DkLogs.info(this, "Got response as json: %s", json);
			}

			return DkJsonHelper.getIns().json2obj(json, responseClass);
		}
		catch (Exception e) {
			DkLogs.error(DkLogs.class, e);
			return null;
		}
		finally {
			connection.disconnect();
		}
	}

	public Bitmap bitmap() {
		try {
			return DkBitmaps.load(connection.getInputStream());
		}
		catch (Exception e) {
			DkLogs.error(DkLogs.class, e);
			return null;
		}
		finally {
			connection.disconnect();
		}
	}

	/**
	 * Obtain input stream of body.
	 * Caller must call `body.close()` to release resource, disconnect remote after manual decoded.
	 */
	public InputStream byteStream() throws IOException {
		return connection.getInputStream();
	}

	/**
	 * Call this to disconnect server.
	 */
	public void close() {
		connection.disconnect();
	}
}