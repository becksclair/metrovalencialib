package com.heliasar.metrovalencialib.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.heliasar.tools.Utils;

public class WebHelper {

	// API Base URL
	public static final String MTV_TIMETABLES_ENDPOINT = "http://www.metrovalencia.es/horarios.mobi.php";

	public static class Response {
		public int statusCode;
		public String resp;
	}

	public static Response post(String url, String postData) {
		Response response = new Response();
		try {
			Utils.l("Making web post request to: " + url);
			//disableSSLCertificateChecking();

			HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.connect();

			// Send POST data to the server
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(postData);
			out.flush();

			// Get the response from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				sb.append(line + '\n');
			}

			// Store response information in Response object
			try {
				response.statusCode = conn.getResponseCode();

			} catch (IOException e) {
				e.printStackTrace();
				response.statusCode = 401;
			}
			response.resp = sb.toString();

			// Clean up
			conn.disconnect();
			conn = null;
			out = null;
			in = null;
			sb = null;

		} catch (IOException e) {
			response.statusCode = 401;
		}
		return response;
	}

	public static Response get(String url) {
		Response response = new Response();
		try {
			Utils.l("Making web get request to: " + url);
			//disableSSLCertificateChecking();

			// Setup connection
			HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			// Get response from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				sb.append(line + '\n');
			}

			// Store response in a Response object
			try {
				response.statusCode = conn.getResponseCode();

			} catch (IOException e) {
				response.statusCode = 401;
			}
			response.resp = sb.toString();

			// Clean up
			conn.disconnect();
			conn = null;
			in = null;
			sb = null;
		} catch (IOException e) {
			response.statusCode = 401;
		}
		return response;
	}

	/*private static void disableSSLCertificateChecking() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}*/

}
