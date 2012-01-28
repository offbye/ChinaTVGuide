package com.offbye.chinatvguide.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RSSHandler extends DefaultHandler {
	private static final String TAG = "RSSHandler";
	RSSFeed _feed;
	RSSItem _item;
	String _lastElementName = "";
	boolean bFoundChannel = false;
	final int RSS_TITLE = 1;
	final int RSS_LINK = 2;
	final int RSS_DESCRIPTION = 3;
	final int RSS_CATEGORY = 4;
	final int RSS_PUBDATE = 5;

	final int RSSFEED_PUBDATE = 6;
	final int RSSFEED_TITLE = 7;

	int depth = 0;
	int currentstate = 0;
	boolean isRsshead = true;

	/*
	 * Constructor
	 */
	RSSHandler() {
	}

	/*
	 * getFeed - this returns our feed when all of the parsing is complete
	 */
	RSSFeed getFeed() {
		return _feed;
	}

	public void startDocument() throws SAXException {
		// initialize our RSSFeed object - this will hold our parsed contents
		_feed = new RSSFeed();
		// initialize the RSSItem object - we will use this as a crutch to grab
		// the info from the channel
		// because the channel and items have very similar entries..
		_item = new RSSItem();

	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		depth++;
		if (localName.equals("channel")) {
			currentstate = 0;
			return;
		}
		if (isRsshead && localName.equals("title")) {
			currentstate = RSSFEED_TITLE;
			return;
		}

		if (isRsshead && localName.equals("pubDate")) {
			currentstate = RSSFEED_PUBDATE;
			return;
		}
//
//		if (localName.equals("image")) {
//			currentstate = RSSFEED_PUBDATE;
//			return;
//		}
//
//		if (localName.equals("pubDate")) {
//			currentstate = RSS_PUBDATE;
//			return;
//		}
		if (localName.equals("item")) {
			// create a new item
			_item = new RSSItem();
			isRsshead = false; // 解析到item说明rss head区结束
			return;
		}
		if (localName.equals("title")) {
			currentstate = RSS_TITLE;
			return;
		}
		if (localName.equals("description")) {
			currentstate = RSS_DESCRIPTION;
			return;
		}
		if (localName.equals("link")) {
			currentstate = RSS_LINK;
			return;
		}
		if (localName.equals("category")) {
			currentstate = RSS_CATEGORY;
			return;
		}
		if (localName.equals("pubDate")) {
			currentstate = RSS_PUBDATE;
			return;
		}
		// if we don't explicitly handle the element, make sure we don't wind up
		// erroneously
		// storing a newline or other bogus data into one of our existing
		// elements
		currentstate = 0;
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		depth--;
		if (localName.equals("item")) {
			// add our item to the list!
			_feed.addItem(_item);
			return;
		}
	}

	// public void ignorableWhitespace (char[] ch, int start, int length){
	// Log.i(TAG,"ignorableWhitespace[" +start+" "+ ch.toString() + "]"+
	// length);
	// }
	public void characters(char ch[], int start, int length) {

		String theString = new String(ch, start, length);
//		Log.i(TAG, "characters[" + theString + "]");
		
        //过滤换行 空格 符号< &等，由于SAX解析不能正确处理换行
		if (theString.trim().equals("") || theString.trim().length()<2)
			return;

		switch (currentstate) {
		case RSSFEED_TITLE:
			_feed.setTitle(theString);
			currentstate = 0;
			break;
		case RSSFEED_PUBDATE:
			_feed.setPubDate(formatDate(theString));
			currentstate = 0;
			break;

		case RSS_TITLE:
			_item.setTitle(theString);
			currentstate = 0;
			break;
		case RSS_LINK:
			_item.setLink(theString);
			currentstate = 0;
			break;
		case RSS_DESCRIPTION:
			_item.setDescription(theString);
			currentstate = 0;
			break;
		case RSS_CATEGORY:
			_item.setCategory(theString);
			currentstate = 0;
			break;
		case RSS_PUBDATE:
			_item.setPubDate(formatDate(theString));
			currentstate = 0;
			break;
		default:

			return;
		}

	}
	
	/**
	 * format date "Wed, 25 Jan 2012 11:07:54 +0800" to "2012-01-25 11:07:54"
	 * @param date "Wed, 25 Jan 2012 11:07:54 +0800"
	 * @return
	 */
    public static String formatDate(String date) {
        if (date.charAt(0) < 9) {
            return date;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            return sdfs.format(sdf.parse(date));
        } catch (ParseException ex) {
            return date;
        }
    }
}
