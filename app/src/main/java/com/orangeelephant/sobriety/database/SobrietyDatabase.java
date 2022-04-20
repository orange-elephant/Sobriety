package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

/**
 * Access instance using {@link ApplicationDependencies#getSobrietyDatabase()}
 *
 * Singleton which should be the point of access for main database interactions
 * holds instances of each database table file eg. {@link CountersDatabase}
 * singleton-ness should allow for thread safety by maintaining only one database
 * connection throughout the app
 */
public class SobrietyDatabase extends DBOpenHelper {

    private final CountersDatabase countersDatabase;
    private final ReasonsDatabase  reasonsDatabase;

    public SobrietyDatabase(Context context) {
        super(context);

        countersDatabase = new CountersDatabase(this);
        reasonsDatabase  = new ReasonsDatabase(this);
    }

    public CountersDatabase getCountersDatabase() {
        return countersDatabase;
    }

    public ReasonsDatabase getReasonsDatabase() {
        return reasonsDatabase;
    }
}
