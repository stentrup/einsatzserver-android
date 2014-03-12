package net.tentrup.einsatzserver;

import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.util.Template;

import org.apmem.tools.layouts.FlowLayout;
import org.joda.time.LocalDate;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShareMessagePreference extends DialogPreference {
	
	private EditText text;
	private String textValue;
	private final String defaultText;

	public ShareMessagePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.share_message_preference);
		defaultText = context.getString(R.string.configuration_share_message_default);
	}

	@Override
	protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
		super.onAttachedToHierarchy(preferenceManager);
		textValue = getPersistedString(defaultText);
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		builder.setTitle(R.string.configuration_share_message);
		super.onPrepareDialogBuilder(builder);
	}

	@Override
	protected void onBindDialogView(final View view) {
		text = (EditText) view.findViewById(R.id.share_message_et);
		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// NoOp
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// NoOp
			}
			@Override
			public void afterTextChanged(Editable s) {
				TextView preview = (TextView)view.findViewById(R.id.share_message_preview_tv);
				Template template = new Template(s.toString());
				String previewTextValue = template.apply(
						getContext().getString(R.string.configuration_share_message_example_operation),
						getContext().getString(R.string.configuration_share_message_example_location),
						Operation.printDate(new LocalDate(), false, false));
				preview.setText(getContext().getString(R.string.configuration_share_message_preview, previewTextValue));
				template.adjustSpans(s); 
			}
		});
		text.setText(textValue);

		FlowLayout layout = (FlowLayout)view.findViewById(R.id.share_message_buttons_layout);
		Button btnDienstname = new Button(getContext());
		layout.addView(btnDienstname);
		btnDienstname.setText(R.string.configuration_share_message_tag_description);
		btnDienstname.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				insert(text, "{$" + Template.BEZEICHNUNG + "}");
			}
		});
		Button btnDatum = new Button(getContext());
		layout.addView(btnDatum);
		btnDatum.setText(R.string.configuration_share_message_tag_date);
		btnDatum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				insert(text, "{$" + Template.DATUM + "}");
			}
		});
		Button btnOrt = new Button(getContext());
		layout.addView(btnOrt);
		btnOrt.setText(R.string.configuration_share_message_tag_location);
		btnOrt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				insert(text, "{$" + Template.ORT + "}");
			}
		});

		Button btnReset = (Button) view.findViewById(R.id.share_message_reset);
		btnReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				text.setText(defaultText);
			}
		});
		super.onBindDialogView(view);
	}

	private void insert(EditText editText, String text) {
		int start = Math.max(editText.getSelectionStart(), 0);
		int end = Math.max(editText.getSelectionStart(), 0);
		editText.getText().replace(start, end, text, 0, text.length());
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult && text != null) {
			textValue = text.getText().toString();
			persistString(textValue);
		}
		super.onDialogClosed(positiveResult);
	}
}
