package ru.hse.team;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;

import ru.hse.team.database.AppDatabaseAndroid;
import ru.hse.team.googleplayservices.ExtendedGameHelper;
import ru.hse.team.googleplayservices.GameHelper;

public class AndroidLauncher extends AndroidApplication implements GoogleServicesAction {

	private ExtendedGameHelper gameHelper;
    private final static int REQUEST_CODE_UNUSED = 9002;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInFailed() {
				Log.i("Game Helper", "Sign in failed");
			}

			@Override
			public void onSignInSucceeded() {
				Log.i("Game Helper", "Sign in succeeded");
			}
		};

		gameHelper = new ExtendedGameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(true);
		gameHelper.setup(gameHelperListener);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = true;
		config.useCompass = false;
		config.useGyroscope = false;

		initialize(new LaserKittens(
				Room.databaseBuilder(this, AppDatabaseAndroid.class, "database").build(),
				this),
				config);
	}

	@Override
	public void signIn() {
		try {
			runOnUiThread(() -> gameHelper.beginUserInitiatedSignIn());
		} catch (Exception e) {
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()
	{
		try {
			runOnUiThread(() -> gameHelper.signOut());
		} catch (Exception e) {
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame() {
		String str ="https://play.google.com/store/apps/details?id=ru.hse.team";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void submitScore(long score) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_id), score);
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_id)), REQUEST_CODE_UNUSED);
		}
		else {
		}
	}

	@Override
	public void showScores() {
		if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_id)), REQUEST_CODE_UNUSED);
        }
		else {
		}
	}

    @Override
    public void unlockAchievement(String achievementId) {
	    if (isSignedIn()) {
            Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), REQUEST_CODE_UNUSED);
        } else {
        }
    }

    @Override
    public void showAchievements() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), REQUEST_CODE_UNUSED);
        }
    }

	@Override
	public void quickGame(int role) {
		try{
//			runOnUiThread(() -> gameHelper.quickGame(role));
		}
		catch (Exception e){
			Gdx.app.log("CIRUS", "Google Services Logout Failed " + e.getMessage());
		}
	}

	@Override
	public void invitePlayers() {
		runOnUiThread(() -> gameHelper.invitePlayers());
	}

	@Override
	public boolean isSignedIn() {
		return gameHelper.isSignedIn();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}
}
