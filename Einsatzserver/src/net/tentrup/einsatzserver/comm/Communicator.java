package net.tentrup.einsatzserver.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.OperationDetails;
import net.tentrup.einsatzserver.model.ResultStateEnum;
import net.tentrup.einsatzserver.model.ResultWrapper;
import net.tentrup.einsatzserver.parser.HtmlParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Communicator {

	/**
	 * Shared Preferences Key
	 */
	public static final String PREF_TESTMODE = "net.tentrup.einsatzserver.testmode";

	private static final String URL_BASE = "http://www.drk-d.de/eis/";
	private static final String URL_BASE_TESTMODE = "http://www.tentrup.net/eis/";
	private static final String URL_LOGIN = "index.php";
	private static final String URL_ALL_OPERATIONS = "einsatz.php";
	private static final String URL_MY_OPERATIONS = "index.php";
	private static final String URL_OPERATION_DETAILS = "einsatz_uebersicht.php?einsatz_id=";
	private static final String URL_BOOKING = "einsatz_uebersicht.php?bemerkung=%s&einsatz_id=%s&teilnahmewunsch=1&Vormerken=Vormerken";

	private final DefaultHttpClient m_httpClient;
	private final Context m_context;

	public Communicator(Context context) {
		m_httpClient = new DefaultHttpClient();
		m_context = context;
	}

	private String getBaseUrl() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
		boolean testmode = prefs.getBoolean(PREF_TESTMODE, false);
		if (testmode) {
			return URL_BASE_TESTMODE;
		} else {
			return URL_BASE;
		}
	}

	public ResultWrapper<List<Operation>> getAllOperations() {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<List<Operation>>(null, loginResult);
		}
		String htmlErgebnis = executeHttpGetRequest(getBaseUrl() + URL_ALL_OPERATIONS);
		if (htmlErgebnis == null) {
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseAllOperationsPage(htmlErgebnis);
	}

	public ResultWrapper<List<Operation>> getMyOperations() {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<List<Operation>>(null, loginResult);
		}
		String htmlErgebnis = executeHttpGetRequest(getBaseUrl() + URL_MY_OPERATIONS);
		if (htmlErgebnis == null) {
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseMyOperationsPage(htmlErgebnis);
	}

	public ResultWrapper<OperationDetails> getOperationDetails(int operationId) {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<OperationDetails>(null, loginResult);
		}
		String htmlErgebnis = executeHttpGetRequest(getBaseUrl() + URL_OPERATION_DETAILS + operationId);
		if (htmlErgebnis == null) {
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseOperationDetailsPage(operationId, htmlErgebnis);
	}

	public boolean executeBooking(int operationId, String comment) {
		if (login() != ResultStateEnum.SUCCESSFUL) {
			return false;
		}
		try {
			String commentEncoded = URLEncoder.encode(comment, "ISO-8859-1");
			executeHttpGetRequest(String.format(getBaseUrl() + URL_BOOKING, commentEncoded, "" + operationId));
			return true; //TODO parse response
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Performs a login.
	 */
	private ResultStateEnum login() {
		HttpPost tempRequest = new HttpPost(getBaseUrl() + URL_LOGIN);

		// Add login
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
		nameValuePairs.add(new BasicNameValuePair("username", prefs.getString("configuration.username", "")));
		nameValuePairs.add(new BasicNameValuePair("password", prefs.getString("configuration.password", "")));

		try {
			tempRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = m_httpClient.execute(tempRequest);
			if (response.getStatusLine().getStatusCode() != 200) {
				return ResultStateEnum.LOADING_ERROR;
			}
			String responseText = getTextFromResponse(response);
			if (responseText.contains("Name: ")) {
				return ResultStateEnum.SUCCESSFUL;
			} else {
				return ResultStateEnum.LOGIN_FAILED;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultStateEnum.LOADING_ERROR;
	}

	private String getTextFromResponse(HttpResponse aResponse) throws IllegalStateException, IOException {
		BufferedReader tempReader = null;
		try {
			if (aResponse.getStatusLine().getStatusCode() != 200) {
				return null;
			}
			tempReader = new BufferedReader(new InputStreamReader(aResponse.getEntity().getContent(), "ISO-8859-1"));
			StringBuilder tempResult = new StringBuilder();
			String tempLine = null;
			while ((tempLine = tempReader.readLine()) != null) {
				tempResult.append(tempLine).append(System.getProperty("line.separator"));
			}
			return tempResult.toString();
		} finally {
			if (tempReader != null) {
				tempReader.close();
			}
		}
	}

	private String executeHttpGetRequest(String url) {
		HttpGet tempRequest = new HttpGet(url);
		try {
			HttpResponse tempResponse = m_httpClient.execute(tempRequest);
			return getTextFromResponse(tempResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
