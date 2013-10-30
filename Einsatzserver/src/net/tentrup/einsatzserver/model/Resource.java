package net.tentrup.einsatzserver.model;

import java.io.Serializable;

public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String m_name;

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}
}
