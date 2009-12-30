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
import java.io.Writer;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * Generic exporter in XML
 *
 */
public class XmlExporter {
	/**
	 * Should be implemented by any body that will be serialized
	 */
	public interface XmlSerializable {
		boolean writeXml(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException;
	}
	
	/**
	 * Exports serializable to writer.
	 * 
	 * @param writer
	 * @param exportable
	 * @return
	 */
	public static boolean exportXml(Writer writer, XmlSerializable exportable) {
		XmlSerializer serializer = Xml.newSerializer();

		try {
			serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        
	        serializer.startTag("", "flingbox");
	        exportable.writeXml(serializer);
	        serializer.endTag("", "flingbox");
			
		} catch (Exception ex) {
			/* File cant be written */
			return false;
		}
		return true;
	}
}
