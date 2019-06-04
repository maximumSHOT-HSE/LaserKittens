package ru.hse.team;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.Task;

import ru.hse.team.database.DatabaseAndroid;

/**
 * Activity which initialises database and
 * 	starts game class.
 * Provides interface to access google services API
 */
public class AndroidLauncher extends AndroidApplication implements GoogleServicesAction {


	private static final int RC_SIGN_IN = 9001;
    private static final int RC_CODE_UNUSED = 9002;

    private static final String TAG = "LaserKittens";

	private GoogleSignInClient mGoogleSignInClient;
	private GoogleSignInAccount mSignedInAccount = null;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGoogleSignInClient = GoogleSignIn.getClient(this,
				new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
						.requestScopes(Games.SCOPE_GAMES)
						.build());

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = true;
        config.useCompass = false;
        config.useGyroscope = false;
        initialize(new LaserKittens(
        		Room.databaseBuilder(this, DatabaseAndroid.class, "database").build(),
                        this), config);
	}

	/**
	 * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
	 * your Activity's onActivityResult function
	 */
	@Override
	public void signIn() {
		if (!isSignedIn()) {
			startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
		} else {
            runOnUiThread(() -> Toast.makeText(this, "You are already signed in", Toast.LENGTH_SHORT).show());
        }
	}

	@Override
	public void signInSilently() {
		Log.d(TAG, "signInSilently()");

		mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
				task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signInSilently(): success");
						onConnected(task.getResult());
					} else {
						Log.d(TAG, "signInSilently(): failure", task.getException());
					}
				});
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
                runOnUiThread(() -> Toast.makeText(this, "Google Services sign in: successful", Toast.LENGTH_SHORT).show());
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

		String message = "Error" + exception.getMessage();

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
		if (isSignedIn()) {
			System.out.println(score);
			Games.getLeaderboardsClient(this, mSignedInAccount)
                    .submitScore(getString(R.string.leaderboard_id), score);
			showScores();
		}
		else {
            runOnUiThread(() -> Toast.makeText(this, "Sign in first", Toast.LENGTH_SHORT).show());
		}
	}

	@Override
	public void showScores() {
		if (isSignedIn()) {
            Games.getLeaderboardsClient(this, mSignedInAccount)
					.getLeaderboardIntent((getString(R.string.leaderboard_id)))
					.addOnSuccessListener(intent -> startActivityForResult(intent, RC_CODE_UNUSED))
					.addOnFailureListener(e -> handleException(e, "LeaderBoard exception"));
        } else {
            runOnUiThread(() -> Toast.makeText(this, "Sign in first", Toast.LENGTH_SHORT).show());
        }
	}

    @Override
    public void unlockAchievement(String achievementId) {
	    if (isSignedIn()) {
			Games.getAchievementsClient(this, mSignedInAccount).unlock(achievementId);
			showAchievements();
		}
    }


	@Override
	public void showAchievements() {
		if (isSignedIn()) {
			Games.getAchievementsClient(this, mSignedInAccount).getAchievementsIntent()
					.addOnSuccessListener(intent -> startActivityForResult(intent, RC_CODE_UNUSED))
					.addOnFailureListener(e -> handleException(e, "Achievements exception"));
		} else {
            runOnUiThread(() -> Toast.makeText(this, "Sign in first", Toast.LENGTH_SHORT).show());
        }
	}

    @Override
	public boolean isSignedIn() {
		return GoogleSignIn.getLastSignedInAccount(this) != null;

	}
}
