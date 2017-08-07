package rickhutten.rembrandtcollege;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class Parser{

    final private static String FILE_NAME = "XML";

    private ArrayList<ArrayList<String>> entries = new ArrayList<>();
    private Context context;

    Parser(Context context) {
        this.context = context;
    }

    ArrayList<ArrayList<String>> parseXml() {
        try {
            entries = getEntries();
        } catch (Exception e) {
            // getEntries() throws XmlPullParserException and IOException
            e.printStackTrace();
        }
        return entries;
    }

    private ArrayList<ArrayList<String>> getEntries() throws XmlPullParserException, IOException {

        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        InputStream is = new FileInputStream(file);
        try {
            XmlPullParserFactory xml_pull_parser_factory = XmlPullParserFactory.newInstance();
            xml_pull_parser_factory.setValidating(false);
            xml_pull_parser_factory.setFeature(Xml.FEATURE_RELAXED, true);
            XmlPullParser parser = xml_pull_parser_factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the title tag
                if (name.equals("item")) {
                    ArrayList<String> item = getItem(parser);
                    entries.add(item);
                } else {
                    skip(parser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return entries;
    }

    private ArrayList<String> getItem(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        ArrayList<String> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title;
        String guid;
        String content;
        String pub_date;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = getFromTag(parser, "title");
                    items.add(title);
                    break;
                case "guid":
                    guid = getFromTag(parser, "guid");
                    items.add(guid);
                    break;
                case "content:encoded":
                    content = getFromTag(parser, "content:encoded");
                    items.add(content);
                    break;
                case "pubDate":
                    pub_date = getFromTag(parser, "pubDate");
                    items.add(pub_date);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return items;
    }

    private String getFromTag(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return text;
    }

    // Copied from "http://developer.android.com/training/basics/network-ops/xml.html"
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Copied from "http://developer.android.com/training/basics/network-ops/xml.html"
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.nextToken()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
