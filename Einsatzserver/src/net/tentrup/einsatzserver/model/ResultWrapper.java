package net.tentrup.einsatzserver.model;

public class ResultWrapper<E> {

	private final E m_result;

	private final ResultStateEnum m_state;

	public ResultWrapper(E result, ResultStateEnum state) {
		m_result = result;
		m_state = state;
	}

	public E getResult() {
		return m_result;
	}

	public ResultStateEnum getState() {
		return m_state;
	}
}
