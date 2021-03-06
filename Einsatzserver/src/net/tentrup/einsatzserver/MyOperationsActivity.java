package net.tentrup.einsatzserver;

import java.util.List;

import net.tentrup.einsatzserver.comm.CommunicatorSingleton;
import net.tentrup.einsatzserver.config.PreferenceKeys;
import net.tentrup.einsatzserver.model.BookingState;
import net.tentrup.einsatzserver.model.Operation;
import net.tentrup.einsatzserver.model.ResultWrapper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Activity showing my operations.
 * 
 * @author Tentrup
 *
 */
public class MyOperationsActivity extends AbstractOperationsActivity {

	@Override
	protected ResultWrapper<List<Operation>> getActivityResult() {
		return CommunicatorSingleton.getCommunicator(getApplicationContext()).getMyOperations();
	}

	@Override
	protected void updateStateTextView(Operation operation, TextView textView) {
		textView.setVisibility(View.GONE);
	}

	@Override
	protected void updateStateImageView(Operation operation, ImageView imageView) {
		BookingState bookingState = operation.getBookingState();
		if (bookingState == BookingState.CONFIRMED) {
			imageView.setImageResource(R.drawable.state_user);
		} else if (bookingState == BookingState.REQUESTED) {
			imageView.setImageResource(R.drawable.state_user);
			Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		    animation.setDuration(500); // duration - half a second
		    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		    imageView.startAnimation(animation);
		} else {
			imageView.setImageResource(R.drawable.state_unknown);
		}
	}

	@Override
	protected boolean showItem(Operation operation) {
		// no filtering
		return true;
	}

	@Override
	protected String[] getSwitchableColumnNames() {
		return new String[] {getString(R.string.configuration_ui_showDayOfWeek)};
	}

	@Override
	protected String[] getSwitchableColumnPreferenceKeys() {
		return new String[] {PreferenceKeys.CONFIGURATION_UI_DAY_OF_WEEK};
	}

	@Override
	protected boolean[] getSwitchableColumnPreferenceDefaultValues() {
		return new boolean[] {true};
	}
}
