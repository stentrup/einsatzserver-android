package net.tentrup.einsatzserver;

import java.util.List;

import net.tentrup.einsatzserver.model.Operation;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OperationsListAdapter extends BaseAdapter {

	private List<Operation> m_operations;
	private final LayoutInflater m_inflater;

	public OperationsListAdapter(Context applicationContext) {
		m_inflater = (LayoutInflater)applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (m_operations == null) {
			return 0;
		}
		return m_operations.size();
	}

	@Override
	public String getItem(int position) {
		if (m_operations == null) {
			return null;
		}
		Operation operation = m_operations.get(position);
		return Operation.printDate(operation.getStartDate(), true, false) + " " + operation.getDescription();
	}

	@Override
	public long getItemId(int position) {
		if (m_operations == null) {
			return 0;
		}
		return m_operations.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        String title = getItem(position);
        if (convertView == null) {
            tv = (TextView) m_inflater.inflate(R.layout.operations_item, parent, false);
            tv.setGravity(Gravity.LEFT);
        } else {
            tv = (TextView) convertView;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        tv.setText(title);
        return tv;
	}

	public void setItems(List<Operation> operations) {
		m_operations = operations;
		notifyDataSetChanged();
	}

}
