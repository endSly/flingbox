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

package edu.eside.flingbox.bodies;

import java.io.IOException;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;

import edu.eside.flingbox.graphics.RenderPolygon;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicPolygon;
import edu.eside.flingbox.physics.PhysicBody.OnMovementListener;
import edu.eside.flingbox.xml.InvalidXmlException;
import edu.eside.flingbox.xml.XmlExporter.XmlSerializable;
import edu.eside.flingbox.xml.XmlImporter.XmlParseable;

/**
 * Polygon is a general class which handles the physics
 * and render instances of a polygonal Body.
 */
public final class Polygon extends Body implements OnMovementListener, XmlSerializable, XmlParseable {
	private final static String TAG_POLYGON = "polygon";
	private final static String TAG_CONTOUR = "contour";
	private final static String TAG_POSITION = "position";
	private final static String TAG_ANGLE = "angle";
	private final static String ATTRIBUTE_POINTS_COUNT = "pointsCount";
	private final static String TAG_POINT = "point";
	private final static String TAG_FIXED = "fixed";
	
	private Vector2D[] mPoints;
	private short mPointsCount;

	public Polygon() {
		super();
	}
	
	/**
	 * Constructor for a Polygon
	 * @param polygonPoints Array of Polygon's point, this is stored and modified
	 * @throws IllegalArgumentException If not enough points
	 */
	public Polygon(Vector2D[] polygonPoints) throws IllegalArgumentException {
		super();
		
		if (polygonPoints.length < 3) {
			Log.e("Flingbox", "Trying to build a polygon with an insuficient number of points");
			throw new IllegalArgumentException("Not enough points to build a polygon.");
		}
		
		/* 
		 * Set points in Clock-wise order 
		 */
		float polygonArea = PolygonUtils.polygonArea(polygonPoints);
		if (polygonArea > 0) {
			/* If points are in anti-Clock-wise order the
			 * returned area will be positive, else, it 
			 * will be negative.
			 */
			Vector2D temp;
			for (int i = 0, j = polygonPoints.length - 1; i < j; --j, ++i) {
				temp = polygonPoints[i];  // Just swap polygon order
				polygonPoints[i] = polygonPoints[j];
				polygonPoints[j] = temp;
			}	
		} else 
			polygonArea = -polygonArea;

		/* 
		 * Relocate polygon to set the centroid at point (0, 0) 
		 */
		Vector2D centroid = PolygonUtils.polygonCentroid(polygonPoints);
		for (Vector2D p : polygonPoints) 
			p.sub(centroid);

		mPoints = polygonPoints;
		mPointsCount = (short) (polygonPoints.length); 
		mRender = new RenderPolygon(polygonPoints);
		mPhysics = new PhysicPolygon(polygonPoints, polygonArea, centroid, this);
	}
	
	/**
	 * @return Polygon points
	 */
	public Vector2D[] getPoints() {
		return mPoints;
	}
	
	/**
	 * @return Polygon points
	 */
	public void setPoints(Vector2D[] points, Vector2D centroid) {
		mPoints = points;
		mPointsCount = (short) (points.length); 
		mRender = new RenderPolygon(points);
		mPhysics = new PhysicPolygon(points, PolygonUtils.polygonArea(points), centroid, this);
	}

	/**
	 * @return Polygons total points. 
	 */
	public short getPointsCount() {
		return mPointsCount;
	}
	
	/**
	 * Sets random color to polygon
	 */
	public void setRandomColor() {
		Random rnd = new Random();
		((RenderPolygon) mRender).setColor(rnd.nextFloat() ,rnd.nextFloat() ,rnd.nextFloat() , 1.0f);
	}

	/**
	 * Called when movement occurs.
	 */
	public void onMovement(Vector2D position, float rotation) {
		((RenderPolygon) mRender).setPosition(position, rotation);
	}


	/**
	 * XML Writter
	 */
	@Override
	public boolean writeXml(XmlSerializer serializer) {
		try {
			serializer.startTag("", TAG_POLYGON);
				serializer.startTag("", TAG_CONTOUR);
					serializer.attribute("", ATTRIBUTE_POINTS_COUNT, mPoints.length + "");
					for (Vector2D point : mPoints) {
					serializer.startTag("", TAG_POINT);
						serializer.attribute("", "x", point.i + "");
						serializer.attribute("", "y", point.j + "");
					serializer.endTag("", TAG_POINT);
					}
				serializer.endTag("", TAG_CONTOUR);
				
				serializer.startTag("", TAG_POSITION);
					serializer.attribute("", "x", mPhysics.getPosition().i + "");
					serializer.attribute("", "y", mPhysics.getPosition().j + "");
				serializer.endTag("", TAG_POSITION);
				
				serializer.startTag("", TAG_ANGLE);
					serializer.attribute("", "value", mPhysics.getAngle() + "");
				serializer.endTag("", TAG_ANGLE);
				
				serializer.startTag("", TAG_FIXED);
					serializer.attribute("", "value", mPhysics.isFixed() ? "1" : "0");
				serializer.endTag("", TAG_FIXED);
			serializer.endTag("", TAG_POLYGON);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean readXml(XmlPullParser parser) 
	throws XmlPullParserException, IOException, InvalidXmlException {
		boolean readSuccess = true;
		if ((parser.getEventType() != XmlPullParser.START_TAG) 
				|| !(parser.getName().equals(TAG_POLYGON))) 
			throw new InvalidXmlException("polygon start tag expected but " + parser.getName() + " found.");
		
		Vector2D[] points = new Vector2D[0];
		Vector2D centroid = new Vector2D();
		boolean isFixed = false;
		float angle = 0f;
		
		for (int eventType = parser.next()
				; eventType != XmlPullParser.END_TAG
				; eventType = parser.next()) {
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(TAG_CONTOUR)) { 
					/* Parse contour */
					int pointsCount = Integer.parseInt(parser.getAttributeValue(0));
					points = new Vector2D[pointsCount];
					for (int i = 0; i < pointsCount; i++) {
						if (parser.next() != XmlPullParser.START_TAG 
								|| !(parser.getName().equals(TAG_POINT))) 
							throw new InvalidXmlException("point start tag expected but " + parser.getName() + " found.");
							
						points[i] = new Vector2D(Float.parseFloat(parser.getAttributeValue(0)), 
												 Float.parseFloat(parser.getAttributeValue(1)));
						parser.next();
					}
				} else if (parser.getName().equals(TAG_POSITION)) {
					/* Parse position */
					centroid.set(Float.parseFloat(parser.getAttributeValue(0)) , 
							  	 Float.parseFloat(parser.getAttributeValue(1)));
				} else if (parser.getName().equals(TAG_ANGLE)) {
					/* Parse angle */
					angle = Float.parseFloat(parser.getAttributeValue(0));
				} else if (parser.getName().equals(TAG_FIXED)) {
					/* Parse fixed */
					isFixed = Integer.parseInt(parser.getAttributeValue(0)) != 0;
				} else 
					throw new InvalidXmlException("unknown tag found: " + parser.getName());
					
				parser.next();
			} else 
				return false;
		} 
		if (!parser.getName().equals(TAG_POLYGON)) 
			throw new InvalidXmlException("polygon end tag expected but " + parser.getName() + " found.");

		/* Now create the polygon */
		setPoints(points, centroid);
		mPhysics.setAngle(angle);
		mPhysics.setBodyFixed(isFixed);
		setRandomColor();
		return readSuccess;
	}

}
