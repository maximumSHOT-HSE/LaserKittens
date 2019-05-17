package ru.hse.team;

import android.arch.persistence.room.Room;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ru.hse.team.database.AppDatabaseAndroid;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = true;
		config.useCompass = false;
		config.useGyroscope = false;

		initialize(new LaserKittens(Room.databaseBuilder(this, AppDatabaseAndroid.class, "database")
				.build()), config);
	}
}
