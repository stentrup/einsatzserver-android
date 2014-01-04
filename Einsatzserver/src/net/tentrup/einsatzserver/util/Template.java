package net.tentrup.einsatzserver.util;

import android.graphics.Color;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;

import com.x5.template.Chunk;
import com.x5.template.SnippetPart;
import com.x5.template.SnippetTag;

public class Template extends Chunk {

	public static final String BEZEICHNUNG = "Bezeichnung";
	public static final String DATUM = "Datum";
	public static final String ORT = "Ort";

	public Template(String text) {
		append(text);
	}

	public String apply(String description, String location, String date) {
		set(BEZEICHNUNG, description);
		set(ORT, location);
		set(DATUM, date);
		return toString();
	}

	public void adjustSpans(Editable target) {
		Object[] spans = target.getSpans(0, target.length(), ForegroundColorSpan.class);
		for (Object span : spans) {
			target.removeSpan(span);
		}
		int index = 0;
		for (SnippetPart part: templateRoot.getParts()) {
			int length = part.getText().length();
			if (part.isTag()) {
				target.setSpan(new ForegroundColorSpan(Color.RED), index, index + length, 0); 
			}
			index += length;
		}
	}
	
	@Override
	/**
	 * Überschrieben, um einen Fehler zu beheben, der bei einem leeren Tag ("{$}") entsteht.
	 */
	protected Object _resolveTagValue(SnippetTag tag, int depth, boolean ignoreParentContext) {
        String[] path = tag.getPath();
        int segment = 0;
        String segmentName = path[segment];
        if (segmentName.length() > 0) {
        	return super._resolveTagValue(tag, depth, ignoreParentContext);
        } else {
        	return null;
        }
	}
}
