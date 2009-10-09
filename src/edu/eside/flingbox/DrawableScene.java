package edu.eside.flingbox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.objects.Polygon;

public abstract class DrawableScene extends StaticScene implements OnInputListener {
	
	private class DrawingRender implements Renderizable {

		private final ArrayList<PointF> mDrawingPattern;
		
		public DrawingRender(ArrayList<PointF> drawingPattern) {
			mDrawingPattern = drawingPattern;
		}
		
		@Override
		public boolean onRender(GL10 gl) {
			final int pointsCount = mDrawingPattern.size();
			if (pointsCount < 2)
				return false;

			FloatBuffer vertexBuffer = ByteBuffer
				.allocateDirect(4 * 3 * pointsCount)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();

			ShortBuffer indexBuffer = ByteBuffer
				.allocateDirect(2 *  2 * (pointsCount - 1))
				.order(ByteOrder.nativeOrder())
				.asShortBuffer();
			
			vertexBuffer.position(0);
			indexBuffer.position(0);
			
			for (short i = 0; i < (pointsCount - 1); ) {
				vertexBuffer.put(mDrawingPattern.get(i).x);
				vertexBuffer.put(mDrawingPattern.get(i).y);
				vertexBuffer.put(0.0f);
				
				indexBuffer.put(i++);
				indexBuffer.put(i);
			}
			vertexBuffer.put(mDrawingPattern.get(pointsCount - 1).x);
			vertexBuffer.put(mDrawingPattern.get(pointsCount - 1).y);
			vertexBuffer.put(0.0f);

			vertexBuffer.position(0);
			indexBuffer.position(0);
			
			gl.glColor4f(0.0f, 0.0f, 0.8f, 1.0f);
	    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
	    	gl.glDrawElements(GL10.GL_LINES, 2 * (pointsCount - 1), 
	    			GL10.GL_UNSIGNED_SHORT, indexBuffer);
	    			

	    	return true;
		}
		
	}
	
	private DrawingRender mDrawingRender;
	
	private ArrayList<PointF> mDrawingPattern;
	private boolean mIsDrawing;
	private boolean mIsDrawingLocked;
	
	public DrawableScene(Context c) {
		super(c);
		mIsDrawing = false;
		mIsDrawingLocked = false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onUp(MotionEvent ev) {
		if (mIsDrawing) {
			// Drawing ends
			mIsDrawing = false;
			mIsDrawingLocked = false;
			
			final int pointsCount = mDrawingPattern.size();
			// if we had points enough
			if (pointsCount >= 3) {
				float[] points = new float[pointsCount * 2];
				for (int i = 0; i < pointsCount; i++) {
					points[2 * i] = mDrawingPattern.get(i).x;
					points[2 * i + 1] = mDrawingPattern.get(i).y;
				}
				Polygon drawedPolygon = new Polygon(points);
				drawedPolygon.setRandomColor();
				mOnSceneBodys.add(drawedPolygon);
			}
			mDrawingPattern = null;
			
			mOnSceneBodys.remove(mDrawingRender);
			mDrawingRender = null;
			
			// Good moment to call garbage collector
			System.gc();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent downEv, MotionEvent ev, float distanceX,
			float distanceY) {
		// Drawing can be locked until Up event
		if (mIsDrawingLocked)
			return false;
		
		if (!mIsDrawing) {
			mDrawingPattern = new ArrayList<PointF>();
			mDrawingPattern.add(new PointF(downEv.getX(), downEv.getY()));
			mIsDrawing = true;
			
			mDrawingRender = new DrawingRender(mDrawingPattern);
			mOnSceneBodys.add(mDrawingRender);
		}
		
		mDrawingPattern.add(new PointF(ev.getX(), ev.getY()));
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
