package net.tentrup.einsatzserver;

import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

/**
 * Listener for the filter choices.
 *
 * @author Tentrup
 *
 */
public class FilterChoiceClickListener implements OnMultiChoiceClickListener {

	private final boolean[] m_checked;

	public FilterChoiceClickListener(boolean[] checked) {
		m_checked = checked;
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		m_checked[which] = isChecked;
	}

	public boolean[] getChecked() {
		return m_checked;
	}
}
