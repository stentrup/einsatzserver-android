package net.tentrup.einsatzserver;

import android.content.Context;
import net.tentrup.einsatzserver.model.Person;

/**
 * Switchable columns for the {@link OperationPersonnelActivity}.
 *
 * @author Tentrup
 *
 */
public abstract class SwitchableTableColumn {
	private final int m_dialogNameResourceId;
	private final int m_tableHeaderNameResourceId;
	private final String m_preferenceKey;
	private final boolean m_preferenceDefaultValue;

	public SwitchableTableColumn(int dialogNameResourceId, int tableHeaderNameResourceId, String preferenceKey, boolean preferenceDefaultValue) {
		m_dialogNameResourceId = dialogNameResourceId;
		m_tableHeaderNameResourceId = tableHeaderNameResourceId;
		m_preferenceKey = preferenceKey;
		m_preferenceDefaultValue = preferenceDefaultValue;
	}

	/**
	 * Returns the column name as shown in the choice dialog
	 */
	public String getDialogName(Context context) {
		return context.getString(m_dialogNameResourceId);
	}

	/**
	 * Returns the column name as shown in the table header
	 */
	public String getTableHeaderName(Context context) {
		return context.getString(m_tableHeaderNameResourceId);
	}

	/**
	 * Returns the preference key
	 */
	public String getPreferenceKey() {
		return m_preferenceKey;
	}

	/**
	 * Returns the preference default value
	 */
	public boolean getPreferenceDefaultValue() {
		return m_preferenceDefaultValue;
	}

	/**
	 * Returns the text for the given {@link Person} to be shown in this column
	 */
	public abstract String getText(Person person);
}