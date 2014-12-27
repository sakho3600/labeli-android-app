package com.app.labeli.event;

import java.io.File;
import java.util.concurrent.ExecutionException;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.app.labeli.R;
import com.tools.FileTools;
import com.tools.HTMLTools;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailsActivity extends FragmentActivity{

	private ImageView imageView;
	private TextView textViewName, textViewAuthor, textViewDescription;
	private ProgressDialog pDialog;
	private ItemEvent event;
	
	Animation animFadeIn, animFadeOut;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_event_details);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("D�tail");

		event = this.getIntent().getExtras().getParcelable("event");

		imageView = (ImageView) findViewById(R.id.activity_event_details_picture);
		textViewName = (TextView) findViewById(R.id.activity_event_details_name);
		textViewAuthor = (TextView) findViewById(R.id.activity_event_details_author);
		textViewDescription = (TextView) findViewById(R.id.activity_event_details_description);

		textViewName.setText(event.getName());
		textViewAuthor.setText("par " + event.getAuthor().getFirstName() + " " + event.getAuthor().getLastName());
		textViewDescription.setText(HTMLTools.HTMLToText(event.getDescription()));
		
		if (!event.getPictureURL().equals(""))
			new ImageLoader(event.getPictureURL()).execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.event_details, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public void onBackPressed() {
        super.onBackPressed();
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void prepareImageView(String url){
		Bitmap myBitmap = BitmapFactory.decodeFile(url);
		
		imageView.setImageBitmap(myBitmap);
	}

	private class ImageLoader extends AsyncTask<Void, Void, String>
	{
		String courtUrl;
		RequestQueue q;

		public ImageLoader(String courtUrl){
			this.courtUrl = courtUrl;
			this.q = Volley.newRequestQueue(EventDetailsActivity.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EventDetailsActivity.this);
			pDialog.setMessage("Chargement des images");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(Void... v)
		{
			if (courtUrl != null && !courtUrl.equals("")){
				Bitmap bm = null;
				
				final File dataFile = new File(FileTools.getAbsolutePathLocalFileFromURL(EventDetailsActivity.this, courtUrl));
				String wallpaperURLStr = "http://labeli.org/" + courtUrl;
				String localFile = FileTools.getLocalFileFromURL(courtUrl);

				if (!dataFile.exists()){
					Log.i("Net", "Chargement de " + wallpaperURLStr);

					RequestFuture<Bitmap> f = RequestFuture.newFuture();
					ImageRequest ir = new ImageRequest(wallpaperURLStr.replace(" ", "%20"), f, 0, 0, null, null);
					ir.setRetryPolicy(new DefaultRetryPolicy(
							5000, 
							DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
							DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
					q.add(ir);

					try {
						bm = f.get();
						FileTools.writeBitmapToFile(EventDetailsActivity.this, localFile, bm);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			prepareImageView(FileTools.getAbsolutePathLocalFileFromURL(EventDetailsActivity.this, courtUrl));
			pDialog.dismiss();
		}

	}




}