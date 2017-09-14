package org.policetracks.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public  class Dao {
    private static final String NAME = "org.policetracks.android.db";
    private static final String TAG = "Dao";
    private static org.policetracks.android.db.WaypointDao waypointDao;
    private static org.policetracks.android.db.DayDao dayDao;
    private static org.policetracks.android.db.InterventionDao interventionDao;
    private static SQLiteDatabase db;

    public static void initialize(Context c) {
        org.policetracks.android.db.DaoMaster.DevOpenHelper helper1 = new org.policetracks.android.db.DaoMaster.DevOpenHelper(c, NAME, null);
        db = helper1.getWritableDatabase();
        org.policetracks.android.db.DaoMaster daoMaster1 = new org.policetracks.android.db.DaoMaster(db);
        org.policetracks.android.db.DaoSession daoSession1 = daoMaster1.newSession();
        waypointDao = daoSession1.getWaypointDao();
        org.policetracks.android.db.DaoSession daoSession2 = daoMaster1.newSession();
        dayDao = daoSession2.getDayDao();
        org.policetracks.android.db.DaoSession daoSession3 = daoMaster1.newSession();
        interventionDao = daoSession3.getInterventionDao();
    }


    public static SQLiteDatabase getDb() { return db; }
    public static org.policetracks.android.db.WaypointDao getWaypointDao() {  return waypointDao; }
    public static org.policetracks.android.db.DayDao getDayDao() {  return dayDao; }
    public static org.policetracks.android.db.InterventionDao getInterventionDao() {  return interventionDao; }
}
