package com.lucaspearson.testrssfeed;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class ArticleParser {
	public List<Article> articles = new ArrayList<Article>();
	private String title = "title";
	private String link = "link";
	private String description = "description";
	private String urlString = null;
	private XmlPullParserFactory xmlFactoryObject;
	public volatile boolean parsingComplete = true;

	public ArticleParser(String urlString) {
		this.urlString = urlString;
	}

	public void parseXMLAndCreateArray(XmlPullParser myParser) {
		int event;
		String text = null;
		try {
			event = myParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String name = myParser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					text = myParser.getText();
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("title")) {
						title = text;
					} else if (name.equals("link")) {
						link = text;
					} else if (name.equals("description")) {
						description = text;
					} else if (name.equals("item")) {
						// Log.i("Info", "title: "+ title + " link: " +link+
						// " description: " + description);
						Article article = new Article();
						article.setTitle(title);
						article.setLink(new URL(link));
						article.setDescription(description);
						articles.add(article);
					}
					break;
				}
				event = myParser.next();
			}
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fetchXML(){
		   Thread thread = new Thread(new Runnable(){
		   @Override
		   public void run() {
		      try {
		         URL url = new URL(urlString);
		         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		         conn.setReadTimeout(10000 /* milliseconds */);
		         conn.setConnectTimeout(15000 /* milliseconds */);
		         conn.setRequestMethod("GET");
		         conn.setDoInput(true);
		         // Starts the query
		         conn.connect();
		         InputStream stream = conn.getInputStream();
		         xmlFactoryObject = XmlPullParserFactory.newInstance();
		         XmlPullParser myparser = xmlFactoryObject.newPullParser();
		         myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		         myparser.setInput(stream, null);
		         parseXMLAndCreateArray(myparser);
		         stream.close();
		      } catch (Exception e) {
		      }
		      }
		      });
		      thread.start(); 
		   }

}
