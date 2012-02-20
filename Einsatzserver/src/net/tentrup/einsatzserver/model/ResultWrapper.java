package net.tentrup.einsatzserver.model;

public class ResultWrapper<E> {

	private final E m_result;

	private final ResultStateEnum m_state;

	private final String m_username;

	public ResultWrapper(E result, ResultStateEnum state, String username) {
		m_result = result;
		m_state = state;
		m_username = username;
	}

	public ResultWrapper(E result, ResultStateEnum state) {
		m_result = result;
		m_state = state;
		m_username = null;
	}

	public E getResult() {
		return m_result;
	}

	public ResultStateEnum getState() {
		return m_state;
	}

	public String getUsername() {
		return m_username;
	}

}
