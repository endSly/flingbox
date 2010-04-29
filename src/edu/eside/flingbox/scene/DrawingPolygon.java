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

package edu.eside.flingbox.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;

import edu.eside.flingbox.graphics.RenderBody;
import edu.eside.flingbox.math.PolygonUtils;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.bodies.Body;
import edu.eside.flingbox.bodies.Polygon;

/**
 * Implements drawing methods to {@link StaticScene}
 */
class DrawingPolygon implements DrawingBody {

    private final ArrayList<Vector2D> mDrawingPattern = new ArrayList<Vector2D>();

    /**
     * {@link Renderizable} Object witch handles drawing pattern and show it to
     * OpenGL's space.
     */
    private final RenderBody mDrawingRender = new RenderBody() {

        /** Flag to lock drawing */
        private boolean mDoRender = true;

        /**
         * Renderizes pattern to {@link GL10}.
         */
        public boolean onRender(GL10 gl) {
            // We need two or more points to render
            final int pointsCount = mDrawingPattern.size();
            if (pointsCount < 2 || !mDoRender)
                return false;

            try {
                // Fit points to OpenGL
                FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(
                        4 * 3 * pointsCount).order(ByteOrder.nativeOrder())
                        .asFloatBuffer();

                ShortBuffer indexBuffer = ByteBuffer.allocateDirect(
                        2 * 2 * (pointsCount - 1)).order(
                        ByteOrder.nativeOrder()).asShortBuffer();

                // Fit 2D points into 3D space
                final ArrayList<Vector2D> pattern = mDrawingPattern;
                for (short i = 0; i < (pointsCount - 1);) {
                    vertexBuffer.put(pattern.get(i).i);
                    vertexBuffer.put(pattern.get(i).j);
                    vertexBuffer.put(0.0f);

                    indexBuffer.put(i++); // Set indexes
                    indexBuffer.put(i);
                }
                vertexBuffer.put(pattern.get(pointsCount - 1).i); // Put also
                vertexBuffer.put(pattern.get(pointsCount - 1).j); // last point.
                vertexBuffer.put(0.0f);

                vertexBuffer.position(0);
                indexBuffer.position(0);

                // Draw it to OpenGL's space
                try {
                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                    gl.glDrawElements(GL10.GL_LINES, 2 * (pointsCount - 1),
                            GL10.GL_UNSIGNED_SHORT, indexBuffer);
                } catch (GLException ex) {
                    // Do nothing
                    return false;
                }
            } catch (Exception ex) {
                // Do nothing. Just skip drawing this frame
                return false;
            }
            return true;
        }
    };

    public RenderBody getDrawingRender() {
        return mDrawingRender;
    }

    public void newDrawingPoint(final Vector2D point) {
        mDrawingPattern.add(point);
    }

    public Body finalizeDrawing() {
        final int pointsCount = mDrawingPattern.size();

        Polygon drawedPolygon = null;

        if (pointsCount >= 3) { // if we had points enough
            mDrawingPattern.trimToSize();
            /* Optimize points by Douglas-Peucker algorithm */
            Vector2D[] optimizedPoints = PolygonUtils.douglasPeuckerReducer(
                    mDrawingPattern.toArray(new Vector2D[0]), 5f);// TODO:
                                                                  // mCamera.getAperture().i
                                                                  // / 100f);

            if (optimizedPoints.length >= 3) { // We have points enough
                drawedPolygon = new Polygon(optimizedPoints);
                drawedPolygon.setRandomColor();

            }
        }
        mDrawingPattern.clear();
        return drawedPolygon;
    }

    public void cancelDrawing() {
        mDrawingPattern.clear();
    }

}
