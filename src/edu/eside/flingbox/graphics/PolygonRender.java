package edu.eside.flingbox.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.eside.flingbox.objects.PolygonBody;

public abstract class PolygonRender extends PolygonBody implements Renderizable {
	// Buffers needed to allocate graphical polygon
	private FloatBuffer mVertexBuffer;
	private ShortBuffer mIndexBuffer;
	
	private float[] mColor;
	
	public PolygonRender(float[] points) {
		super(points);
		initialize();
	}

	private void initialize() {
		mColor = new float[4];
		
		// Set color for the object
		mColor[0] = 0.0f;
		mColor[1] = 0.0f;
		mColor[2] = 0.0f;
		mColor[3] = 1.0f;
		
		
		// Fill 2D polygon into 3D space
		float[] points3D = new float[3 * mPointsCount];
		for (int i = 0; i < mPointsCount; i++) {
			points3D[3 * i] = mPoints[2 * i];			// x
			points3D[3 * i + 1] = mPoints[2 * i + 1];	// y
			points3D[3 * i + 2] = 0.0f;					// z
		}

		// Fill buffers with correspondent vertex
		mVertexBuffer = ByteBuffer
			.allocateDirect(4 * 3 * mPointsCount)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer()
			.put(points3D);
		mVertexBuffer.position(0);
		
		mIndexBuffer = ByteBuffer
			.allocateDirect(2 * mTriangulationIndexes.length)
			.order(ByteOrder.nativeOrder())
			.asShortBuffer()
			.put(mTriangulationIndexes);
		mIndexBuffer.position(0);
	}
	
	public void setColor(float r, float g, float b, float alpha) {
		mColor[0] = r;
		mColor[1] = g;
		mColor[2] = b;
		mColor[3] = alpha;
	}
	
	/**
	 * Renderizes Polygon into gl
	 */
	public boolean onRender(GL10 gl) {
		//final long time = (mTrianglesCount - 8) * android.os.SystemClock.uptimeMillis() % (5000L);
		//gl.glRotatef(0.072f * ((int) time), 0f, 0f, 1.0f);
		gl.glColor4f(mColor[0], mColor[1], mColor[2], mColor[3]);
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    	gl.glDrawElements(GL10.GL_TRIANGLES, 3 * mTrianglesCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		return true;
	}
}
