package net.tentrup.einsatzserver.model;

public class ResultWrapper<E> {

	private final E m_Result;

	private final ResultStateEnum m_State;

	public ResultWrapper(E result, ResultStateEnum state) {
		m_Result = result;
		m_State = state;
	}

	public E getResult() {
		return m_Result;
	}

	public ResultStateEnum getState() {
		return m_State;
	}

}
