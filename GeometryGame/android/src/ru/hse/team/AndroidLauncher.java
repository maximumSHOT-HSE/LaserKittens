package ru.hse.team;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ru.hse.team.database.AppDatabaseAndroid;
import ru.hse.team.googleplayservices.GameHelper;

public class AndroidLauncher extends AndroidApplication implements GoogleServicesAction {

	private GameHelper gameHelper;

	private static final int RC_SIGN_IN = 9001;
    private static final int RC_CODE_UNUSED = 9002;

    private static final String TAG = "LaserKittens";

	private GoogleSignInClient mGoogleSignInClient;
	private ProgressDialog mLoadingDialog = null;

	// The currently signed in account, used to check the account has changed outside of this activity when resuming.
	GoogleSignInAccount mSignedInAccount = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGoogleSignInClient = GoogleSignIn.getClient(this,
				new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
						.build());


        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = false;
        config.useGyroscope = false;
        initialize(new LaserKittens(Room.databaseBuilder(this, AppDatabaseAndroid.class, "database").build(),
                        this), config);
	}


	/**
	 * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
	 * your Activity's onActivityResult function
	 */
	@Override
	public void signIn() {
		startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
	}

	@Override
	public void signInSilently() {
		Log.d(TAG, "signInSilently()");

//		mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
//				task -> {
//					if (task.isSuccessful()) {
//						Log.d(TAG, "signInSilently(): success");
//						onConnected(task.getResult());
//					} else {
//						Log.d(TAG, "signInSilently(): failure", task.getException());
//					}
//				});
	}

	@Override
	public void signOut() {
		Log.d(TAG, "signOut()");

		mGoogleSignInClient.signOut().addOnCompleteListener(this,
				task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signOut(): success");
					} else {
						handleException(task.getException(), "signOut() failed!");
					}
				});
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == RC_SIGN_IN) {

			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				onConnected(account);
			} catch (ApiException apiException) {
				String message = apiException.getMessage();
				if (message == null || message.isEmpty()) {
					message = "Sign in another Error";
				}
				new AlertDialog.Builder(this)
						.setMessage(message)
						.setNeutralButton(android.R.string.ok, null)
						.show();
			}
		}

    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");

		signInSilently();
	}


	@Override
    public void onStart() {
	    super.onStart();

    }

    @Override
    public void onStop() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
        super.onStop();

    }

	private void onConnected(GoogleSignInAccount googleSignInAccount) {
		Log.d(TAG, "onConnected(): connected to Google APIs");
		if (mSignedInAccount != googleSignInAccount) {
			mSignedInAccount = googleSignInAccount;
		}
	}

	private void handleException(Exception exception, String details) {
		int status = 0;

		if (exception instanceof ApiException) {
			ApiException apiException = (ApiException) exception;
			status = apiException.getStatusCode();
		}

		String message = "Error";

		new AlertDialog.Builder(this)
				.setMessage(message)
				.setNeutralButton(android.R.string.ok, null)
				.show();

	}

	@Override
	public void rateGame() {
		String str ="https://play.google.com/store/apps/details?id=ru.hse.team";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void submitScore(long score) {
//		if (isSignedIn()) {
//			Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_id), score);
//			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_id)), REQUEST_CODE_UNUSED);
//		}
//		else {
//		}
	}

	@Override
	public void showScores() {
//		if (isSignedIn()) {
//            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_id)), REQUEST_CODE_UNUSED);
//        }
//		else {
//		}
	}

    @Override
    public void unlockAchievement(String achievementId) {
//	    if (isSignedIn()) {
//            Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
//            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), REQUEST_CODE_UNUSED);
//        } else {
//        }
    }

    @Override
    public void showAchievements() {
		if (isSignedIn()) {
			Games.getAchievementsClient(this, mSignedInAccount).getAchievementsIntent()
					.addOnSuccessListener(intent -> startActivityForResult(intent, RC_CODE_UNUSED))
					.addOnFailureListener(e -> handleException(e, "Achievements exception"));
		}
	}

    @Override
	public boolean isSignedIn() {
		return GoogleSignIn.getLastSignedInAccount(this) != null;
	}
}
