package net.tentrup.einsatzserver.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.tentrup.einsatzserver.config.PreferenceKeys;
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
		String response = executeHttpGetRequest(getBaseUrl() + URL_ALL_OPERATIONS);
		if (response == null) {
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseAllOperationsPage(response);
	}

	public ResultWrapper<List<Operation>> getMyOperations() {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<List<Operation>>(null, loginResult);
		}
		String response = executeHttpGetRequest(getBaseUrl() + URL_MY_OPERATIONS);
		if (response == null) {
			return new ResultWrapper<List<Operation>>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseMyOperationsPage(response);
	}

	public ResultWrapper<OperationDetails> getOperationDetails(Operation inputOperation) {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<OperationDetails>(null, loginResult);
		}
		String response = executeHttpGetRequest(getBaseUrl() + URL_OPERATION_DETAILS + inputOperation.getId());
		if (response == null) {
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.LOADING_ERROR);
		}
		return new HtmlParser().parseOperationDetailsPage(inputOperation, response);
	}

	public ResultWrapper<OperationDetails> executeBooking(Operation inputOperation, String comment) {
		ResultStateEnum loginResult = login();
		if (loginResult != ResultStateEnum.SUCCESSFUL) {
			return new ResultWrapper<OperationDetails>(null, loginResult);
		}
		try {
			String commentEncoded = URLEncoder.encode(comment, "ISO-8859-1");
			String response = executeHttpGetRequest(String.format(getBaseUrl() + URL_BOOKING, commentEncoded, "" + inputOperation.getId()));
			if (response == null) {
				return new ResultWrapper<OperationDetails>(null, ResultStateEnum.LOADING_ERROR);
			}
			return new HtmlParser().parseOperationDetailsPage(inputOperation, response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new ResultWrapper<OperationDetails>(null, ResultStateEnum.LOADING_ERROR);
		}
	}

	private ResultStateEnum login() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
		return login(prefs.getString(PreferenceKeys.CONFIGURATION_USERNAME, ""), prefs.getString(PreferenceKeys.CONFIGURATION_PASSWORD, ""));
	}

	/**
	 * Performs a login.
	 */
	public ResultStateEnum login(String username, String password) {
		HttpPost tempRequest = new HttpPost(getBaseUrl() + URL_LOGIN);

		// Add login
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));

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
		InputStream tempContentStream = null;
		ByteArrayOutputStream tempByteStream = null;
		try {
			if (aResponse.getStatusLine().getStatusCode() != 200) {
				return null;
			}
			tempContentStream = aResponse.getEntity().getContent();
			tempByteStream = new ByteArrayOutputStream();
			int tempContentByte;
			while ((tempContentByte = tempContentStream.read()) != -1) {
				tempByteStream.write(tempContentByte);
			}
			byte[] tempContentByteArray = tempByteStream.toByteArray();
			String tempResult = new String(tempContentByteArray, "Cp1252");
			return tempResult;
		} finally {
			if (tempContentStream != null) {
				tempContentStream.close();
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
