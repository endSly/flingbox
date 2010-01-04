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

import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;

/**
 * {@link RenderPolygon} handles functions to render {@link Polygon}
 * into OpenGL's space. 
 * Translation and rotation values should be set in {@link AtomicBody}
 * and calculated by physic engine.
 * 
 * This should only be instantiate by {@link Polygon}.
 */
public class RenderPolygon implements RenderBody {
	/** Buffer with vertex, for OpenGL */
	private final FloatBuffer mVertexBuffer;
	/** Buffer with triangulation indexes, for OpenGL */
	private final ShortBuffer mIndexBuffer;
	/** Triangles count */
	private final short mTrianglesCount;
	
	/** Position to draw polygon */
	private final Vector2D mPosition = new Vector2D();
	/** Angle of the polygon */
	private float mAngle = 0f;
	
	/** Stores polygon's color */
	private float[] mColor = new float[] { 0f, 0f, 0f, 1f };

	/**
	 * Default constructor of PolygonRender.
	 * initializes values needed by OpenGL.
	 * 
	 * @param points	Polygon's points, with centroid at 0,0
	 */
	public RenderPolygon(final Vector2D[] points) {
		final int pointsCount = points.length;
		
		/* Fill 2D polygon into 3D space */
		float[] points3D = new float[3 * pointsCount];
		for (int i = 0; i < pointsCount; i++) {
			points3D[3 * i + 0] = points[i].i;		// x
			points3D[3 * i + 1] = points[i].j;		// y
			points3D[3 * i + 2] = 0f;				// z
		}
		
		mVertexBuffer = ByteBuffer // Fill buffers with correspondent vertex
			.allocateDirect(4 * 3 * pointsCount)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer()
			.put(points3D);
		mVertexBuffer.position(0);
		
		/* Set point drawing order by triangulation */
		short[] triangulationIndexes = PolygonUtils.triangulatePolygon(points);
		mIndexBuffer = ByteBuffer
			.allocateDirect(2 * triangulationIndexes.length)
			.order(ByteOrder.nativeOrder())
			.asShortBuffer()
			.put(triangulationIndexes);
		mIndexBuffer.position(0);
		
		mTrianglesCount = (short) (triangulationIndexes.length / 3);
	}
	
	/**
	 * Sets color of the polygon
	 * 
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
	 * 
	 * @param position	Point to current position
	 * @param rotation	Rotation angle in radiants
	 */
	public void setPosition(final Vector2D position, final float rotation) {
		/* Copy point. Avoid objects creation */
		mPosition.set(position);
		
		/* Set angle into degrees */
		mAngle = rotation * 360.0f / (2f * (float) Math.PI);
	}
	
	/**
	 * Renderizes Polygon into gl
	 */
	public boolean onRender(GL10 gl) {
		/* Set color */
		gl.glColor4f(mColor[0], mColor[1], mColor[2], mColor[3]);
		
		/* First translate object for it's position */
		gl.glTranslatef(mPosition.i, mPosition.j, 0f);
		/* Then rotate it */
		gl.glRotatef(((int) mAngle) % 360, 0f, 0f, 1.0f);
		try {
			/* Draw it */
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLES, 3 * mTrianglesCount, 
					GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		} catch (Exception ex) {
			return false; // Body couldn't be rendered
		}
		return true; // Body render succeed
	}
}
