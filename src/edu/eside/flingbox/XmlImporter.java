/*
 *  Flingbox - An OpenSource physics sandbox for Google's Android
 *  Copyright (C) 2009  Jon Ander Peñalba & Endika Gutiérrez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.eside.flingbox;

import java.io.IOException;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlImporter {
	
	public interface XmlParseable {
		boolean readXml(XmlPullParser parser) throws XmlPullParserException, IOException, InvalidXmlException;
	}

	public static boolean importXml(Reader reader, XmlParseable parseable) throws InvalidXmlException {
		boolean success = false;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(reader);
			while (parser.getEventType() != XmlPullParser.START_DOCUMENT) { }
			if (parser.next() != XmlPullParser.END_DOCUMENT)
				success |= parseable.readXml(parser);
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}
}
