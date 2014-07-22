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

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Example of signing in a user with Google+, and how to make a call to a Google+ API endpoint.
 */
public class SignInActivity extends FragmentActivity implements View.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final int REQUEST_CODE_RESOLVE_FAILURE = 9000;
    private static final int REQUEST_CODE_RESOLVE_MISSING_GP = 9001;

    // Key to save if Google+ API is called.
    private static final String KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT =
            "hasCalledGooglePlusApiEndpoint";

    private static final String OAUTH2_PREFIX = "oauth2:";

    /** OAuth 2.0 scope for writing a moment to the user's Google+ history. */
    private static final String PLUS_WRITE_MOMENT =
            "https://www.googleapis.com/auth/plus.moments.write";

    static final String[] SCOPES = new String[] { Scopes.PLUS_PROFILE, PLUS_WRITE_MOMENT };
    static final String SCOPE_STRING = OAUTH2_PREFIX + TextUtils.join(" ", SCOPES);

    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mStatus;
    private TextView mSignInStatus;

    private boolean mHasCalledGooglePlusApiEndpoint;
    private GetPlusPersonDataTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHasCalledGooglePlusApiEndpoint = savedInstanceState.getBoolean(
                    KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT);
        }
        setContentView(R.layout.sign_in_activity);
        mPlusClient = new PlusClient(this, this, this, SCOPES);

        // Spinner progress bar to be displayed if the user clicks the Google+
        // sign-in button before resolving connection errors.
        mConnectionProgressDialog = new ProgressDialog(SignInActivity.this);
        mConnectionProgressDialog.setMessage(getString(R.string.signing_in_status));
        mConnectionProgressDialog.setOwnerActivity(SignInActivity.this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        mSignInStatus = (TextView) findViewById(R.id.sign_in_status);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_HAS_CALLED_GOOGLE_PLUS_API_ENDPOINT,
                mHasCalledGooglePlusApiEndpoint);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
        mSignInStatus.setText(getString(R.string.loading_status));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
        if (mAsyncTask != null) {
            mAsyncTask.cancel(false);
            mAsyncTask = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button:
                if (!mPlusClient.isConnected()) {
                    if (mStatus == null) {
                        mConnectionProgressDialog.show();
                    } else {
                        try {
                            mStatus.startResolutionForResult(this, REQUEST_CODE_RESOLVE_FAILURE);
                        } catch (SendIntentException e) {
                            // Try connecting again.
                            mStatus = null;
                            mPlusClient.connect();
                        }
                    }
                }
                break;
            case R.id.sign_out_button:
                if (mPlusClient.isConnected()) {
                    resetAccountState();
                    mPlusClient.clearDefaultAccount();
                    mPlusClient.disconnect();
                    mPlusClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult status) {
        resetAccountState();
        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the button already and we are showing a spinner
            // progress dialog. We dismiss the progress dialog and start to
            // resolve connection errors.
            mConnectionProgressDialog.dismiss();

            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(this, REQUEST_CODE_RESOLVE_FAILURE);
                } catch (SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }

        // Save the intent so that we can start an activity when the user clicks
        // the button.
        mStatus = status;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!status.hasResolution()
                && GooglePlayServicesUtil.isUserRecoverableError(status.getErrorCode())
                && fragmentManager.findFragmentByTag(TAG_ERROR_DIALOG_FRAGMENT) == null) {
            DialogFragment fragment = new GooglePlayServicesErrorDialogFragment();
            Bundle args = new Bundle();
            args.putInt(GooglePlayServicesErrorDialogFragment.ARG_ERROR_CODE,
                    status.getErrorCode());
            args.putInt(GooglePlayServicesErrorDialogFragment.ARG_REQUEST_CODE,
                    REQUEST_CODE_RESOLVE_MISSING_GP);
            fragment.setArguments(args);
            fragment.show(fragmentManager, TAG_ERROR_DIALOG_FRAGMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        switch(requestCode) {
            case REQUEST_CODE_RESOLVE_FAILURE:
                if (responseCode == RESULT_OK) {
                    mStatus = null;
                    mPlusClient.connect();
                } else {
                    Log.e(TAG, "Unable to recover from a connection failure.");
                }
                break;
            case REQUEST_CODE_RESOLVE_MISSING_GP:
                if (responseCode == RESULT_OK) {
                    mPlusClient.connect();
                } else {
                    Log.e(TAG, "Unable to install Google Play services.");
                }
                break;
        }
    }

    @Override
    public void onConnected() {
        mSignInStatus.setText(getString(R.string.signed_in_status));
        mConnectionProgressDialog.dismiss();

        // Use the connection to make a call.
        if (mAsyncTask == null && !mHasCalledGooglePlusApiEndpoint) {
            String accountName = mPlusClient.getAccountName();
            mAsyncTask = new GetPlusPersonDataTask(this, accountName) {
                @Override
                protected void onUserRecoverAuthException(final UserRecoverableAuthException e) {
                    // This case will be very rare since we've already authenticated by
                    // connecting the PlusClient to Google Play services.
                    startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLVE_FAILURE);
                }

                @Override
                protected void onPostExecute(JSONObject person) {
                    if (person != null) {
                        final String name = person.optString("displayName", "unknown");
                        String greeting = getString(R.string.greeting_status, name);
                        mSignInStatus.setText(greeting);
                    }

                    mHasCalledGooglePlusApiEndpoint = person != null;
                    mAsyncTask = null;
                }
            };

            mAsyncTask.execute();
        }
    }

    /**
     * Resets any pending calls to Google+ API and the user's sign-in status.
     */
    private void resetAccountState() {
        mSignInStatus.setText(getString(R.string.signed_out_status));

        // Reset cached state of any previous account that was signed in.
        mHasCalledGooglePlusApiEndpoint = false;
        if (mAsyncTask != null) {
            mAsyncTask.cancel(false);
            mAsyncTask = null;
        }
    }

    @Override
    public void onDisconnected() {
        // Do nothing
    }
}
