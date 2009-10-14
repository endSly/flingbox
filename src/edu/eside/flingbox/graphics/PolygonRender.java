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

package edu.eside.flingbox.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.eside.flingbox.graphics.Render;
import edu.eside.flingbox.math.Point;

/**
 * {@link PolygonRender} handles functions to render {@link Polygon}
 * into OpenGL's space. 
 * Translation and rotation values should be set in {@link AtomicBody}
 * and calculated by physic engine.
 * 
 * This should only be instantiate by {@link Polygon}.
 */
public class PolygonRender extends Render {
	// Buffers needed to allocate graphical polygon
	private final FloatBuffer mVertexBuffer;
	private final ShortBuffer mIndexBuffer;
	private final short mTrianglesCount;
	
	private final Point mPosition;
	private float mAngle;
	
	// Stores polygon's color
	private float[] mColor;

	/**
	 * Default constructor of PolygonRender.
	 * initializes values needed by OpenGL.
	 * @param points	Polygon's points
	 */
	public PolygonRender(final Point[] points, final short[] triangulationIndexes)
	throws IllegalArgumentException{
		if (points == null || points.length < 3 || triangulationIndexes == null)
			throw new IllegalArgumentException();
		
		// Set object's color
		mColor = new float[] {
			0f, 0f, 0f, 1f
		};
		
		mPosition = new Point();
	
		final int pointsCount = points.length;
		
		// Fill 2D polygon into 3D space
		float[] points3D = new float[3 * pointsCount];
		for (int i = 0; i < pointsCount; i++) {
			points3D[3 * i] = points[i].x;			// x
			points3D[3 * i + 1] = points[i].y;		// y
			points3D[3 * i + 2] = 0.0f;				// z
		}

		// Fill buffers with correspondent vertex
		mVertexBuffer = ByteBuffer
			.allocateDirect(4 * 3 * pointsCount)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer()
			.put(points3D);
		mVertexBuffer.position(0);
		
		mIndexBuffer = ByteBuffer
			.allocateDirect(2 * triangulationIndexes.length)
			.order(ByteOrder.nativeOrder())
			.asShortBuffer()
			.put(triangulationIndexes);
		mIndexBuffer.position(0);
		
		mTrianglesCount = (short) (triangulationIndexes.length / 3);
	}

	/**
	 * Initializes values needed by OpenGL.
	 */
	
	/**
	 * Sets color of the polygon
	 * @param r		Red channel [0, 1]
	 * @param g		Green channel [0, 1]
	 * @param b		Blue channel [0, 1]
	 * @param alpha	Alpha channel, sets object's transparency, 0 for transparent
	 * 		1 for opaque.
	 */
	public void setColor(float r, float g, float b, float alpha) {
		mColor[0] = r;
		mColor[1] = g;
		mColor[2] = b;
		mColor[3] = alpha;
	}
	
	/**
	 * Sets object's position to render 
	 * @param position	Point to current position
	 * @param rotation	Rotation angle in Radians
	 */
	public void setPosition(final Point position, final float rotation) {
		// Copy point. Avoid objects creation
		mPosition.x = position.x;
		mPosition.y = position.y;
		
		// Set angle into degrees
		mAngle = rotation * 360.0f / (2f * (float) Math.PI);
	}
	
	/**
	 * Renderizes Polygon into gl
	 */
	public boolean onRender(GL10 gl) {
		// Set color
		gl.glColor4f(mColor[0], mColor[1], mColor[2], mColor[3]);
		
		// First translate object for it's position
		gl.glTranslatef(mPosition.x, mPosition.y, 0f);
		// Then rotate it
		gl.glRotatef(mAngle, 0f, 0f, 1.0f);
		
		// Draw it
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    	gl.glDrawElements(GL10.GL_TRIANGLES, 3 * mTrianglesCount, 
    			GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		return true;
	}
}
