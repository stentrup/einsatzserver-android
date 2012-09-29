package net.tentrup.einsatzserver;

import android.view.View.OnClickListener;

/**
 * Defines the image and the on click action of a button.
 *
 * @author Tentrup
 *
 */
public interface ImageButtonDefinition extends OnClickListener {
	int getImageResourceId();
}
