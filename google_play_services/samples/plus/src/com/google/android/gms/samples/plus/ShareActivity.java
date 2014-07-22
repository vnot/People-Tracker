/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.plus;

import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusShare;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Example of sharing with Google+ through the ACTION_SEND intent.
 */
public class ShareActivity extends FragmentActivity implements
        View.OnClickListener, View.OnTouchListener {

    protected static final String TAG = ShareActivity.class.getSimpleName();

    private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final int REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR = 1;
    private static final int PRESSED_COLOR_FILTER = Color.argb(30, 0, 0, 0);

    private EditText mEditShareText;
    private ImageButton mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        mShareButton = (ImageButton) findViewById(R.id.share_button);
        mShareButton.setOnClickListener(this);
        mShareButton.setOnTouchListener(this);
        mEditShareText = (EditText) findViewById(R.id.share_prefill_edit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_button:
                final int errorCode = GooglePlusUtil.checkGooglePlusApp(this);
                if (errorCode == GooglePlusUtil.SUCCESS) {
                    // Create an ACTION_SEND intent to share to Google+ with attribution.

                    // Include a deep link in the intent to a resource in your app.
                    // When the user clicks on the deep link, ParseDeepLinkActivity will
                    // immediately route to that resource.
                    Uri thumbnail = Uri.parse(getString(
                            R.string.plus_deep_link_metadata_thumbnail_url));
                    Intent shareIntent = PlusShare.Builder.from(this)
                            .setText(mEditShareText.getText().toString())
                            .setType("text/plain")
                            .setContent(
                                    ShareActivity.TAG,
                                    getString(R.string.plus_deep_link_metadata_title),
                                    getString(R.string.plus_deep_link_metadata_description),
                                    thumbnail)
                            .getIntent();

                    startActivity(shareIntent);
                } else {
                    // Prompt the user to install the Google+ app.
                    DialogFragment fragment = new GooglePlusErrorDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt(GooglePlusErrorDialogFragment.ARG_ERROR_CODE, errorCode);
                    args.putInt(GooglePlusErrorDialogFragment.ARG_REQUEST_CODE,
                            REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR);
                    fragment.setArguments(args);
                    fragment.show(getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
                }
                break;
        }
    }

    /**
     * Updates the opacity of the Google+ share button to indicate a button press.
     *
     * @return false for the parent view to handle the touch event
     *         as it normally would.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mShareButton.setColorFilter(PRESSED_COLOR_FILTER);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Reset the button's tint after the button is pressed.
                mShareButton.setColorFilter(0);
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Unable to recover from missing Google+ app.");
                }
                break;
        }
    }
}
