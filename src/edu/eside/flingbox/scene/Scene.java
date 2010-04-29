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

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import edu.eside.flingbox.BodySettingsDialog;
import edu.eside.flingbox.Preferences;
import edu.eside.flingbox.graphics.SceneRenderer;
import edu.eside.flingbox.graphics.RenderCamera;
import edu.eside.flingbox.input.SceneGestureDetector;
import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;
import edu.eside.flingbox.bodies.Body;
import edu.eside.flingbox.bodies.Polygon;
import edu.eside.flingbox.math.Vector2D;
import edu.eside.flingbox.physics.PhysicBody;
import edu.eside.flingbox.physics.ScenePhysics;
import edu.eside.flingbox.physics.gravity.GravitySource;
import edu.eside.flingbox.xml.InvalidXmlException;
import edu.eside.flingbox.xml.XmlExporter.XmlSerializable;
import edu.eside.flingbox.xml.XmlImporter.XmlParseable;

public class Scene {

    private float mDisplayWidth = 320f;
    private float mDisplayHeight = 480f;

    // Vibrator instance
    private Vibrator mVibrator;

    private RenderCamera mCamera;

    private final SceneRenderer mSceneRenderer;
    private final ScenePhysics mScenePhysics;
    private final SceneGestureDetector mGestureDetector;

    private final ArrayList<Body> mOnSceneBodies = new ArrayList<Body>();

    private final Context mContext;

    private Body mSelectedBody = null;
    private boolean mIsDraggingBody = false;
    
    private DrawingBody mDrawingBody = null;
    
    public final static int SCENE_MODE_PREVIEW = 0;
    public final static int SCENE_MODE_DRAWING = 10;
    public final static int SCENE_MODE_DRAWING_POLYGON = 10;
    public final static int SCENE_MODE_DRAWING_BOX = 11;
    public final static int SCENE_MODE_DRAWING_GROUND = 12;

    private int mMode = SCENE_MODE_PREVIEW;

    public Scene(Context c) {
        mContext = c;

        GravitySource gravity;
        if (Preferences.useAcelerometerBasedGravity)
            try {
                gravity = GravitySource.getAccelerometerBasedGravity(c);
            } catch (Exception ex) {
                /* We don't have accelerometers */
                gravity = GravitySource.getStaticGravity(0f,
                        -SensorManager.GRAVITY_EARTH);
            }
        else
            gravity = GravitySource.getStaticGravity(0f,
                    -SensorManager.GRAVITY_EARTH);

        /* Create list of objects. */
        mSceneRenderer = new SceneRenderer();
        mScenePhysics = new ScenePhysics(gravity);
        /*
         * mSceneRenderer.add(new BackgroundRender( SCENE_LEFT_BORDER,
         * SCENE_RIGHT_BORDER, SCENE_TOP_BORDER, SCENE_BOTTOM_BORDER));
         */
        mGestureDetector = new SceneGestureDetector(c, mInputListener);

        mCamera = mSceneRenderer.getCamera();

        mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);

        if (c instanceof Activity) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) c).getWindowManager().getDefaultDisplay()
                    .getMetrics(dm);
            mDisplayHeight = dm.heightPixels;
            mDisplayWidth = dm.widthPixels;
        }

        System.gc(); // This is a good moment to call to Garbage Collector.
    }

    /**
     * Starts physical simulation
     * 
     * @return true if simulation is started
     */
    public boolean startSimulation() {
        if (mScenePhysics.isSimulating())
            return false;
        mScenePhysics.startSimulation();
        return true;
    }

    /**
     * @return true if success
     */
    public boolean stopSimulation() {
        if (!mScenePhysics.isSimulating())
            return false;
        mScenePhysics.stopSimulation();
        return true;
    }

    /**
     * @return true if simulating
     */
    public boolean isSimulating() {
        return mScenePhysics.isSimulating();
    }

    public void setSceneMode(int mode) {
        mMode = mode;
    }

    public int getSceneMode() {
        return mMode;
    }

    public void add(Body body) {
        mOnSceneBodies.add(body);
        mSceneRenderer.add(body.getRender());
        mScenePhysics.add(body.getPhysics());
    }

    public boolean remove(Body body) {
        boolean removed = mOnSceneBodies.remove(body);
        removed &= mSceneRenderer.remove(body.getRender());
        removed &= mScenePhysics.remove(body.getPhysics());
        return removed;
    }

    /**
     * clear scene
     */
    public void clearScene() {
        final ArrayList<Body> bodies = mOnSceneBodies;
        stopSimulation();
        while (!bodies.isEmpty())
            remove(bodies.get(0));
    }

    public Renderer getSceneRenderer() {
        return mSceneRenderer;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }
    
    /**
     * 
     */
    public boolean onTrackballEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mScenePhysics.isSimulating())
                stopSimulation();
            else
                startSimulation();
            return true;

        case MotionEvent.ACTION_MOVE:

            break;
        }

        return false;
    }

    private OnInputListener mInputListener = new OnInputListener() {

        /**
         * 
         */
        public boolean onDown(MotionEvent e) {
            final Vector2D p = mCamera
                    .project(new Vector2D(e.getX(), e.getY()));

            // Check if user is dragging body
            for (Body object : mOnSceneBodies)
                if (object.getPhysics().contains(p)) {
                    mSelectedBody = object;
                    mIsDraggingBody = true;
                    return true;
                }

            switch (mMode) {
            case SCENE_MODE_PREVIEW:
                // Nothing to Do
                break;
            case SCENE_MODE_DRAWING:
                mSceneRenderer.add(mDrawingBody.getDrawingRender());
                mDrawingBody.newDrawingPoint(p);
                break;

            }
            return true;

        }

        @Override
        public boolean onScroll(MotionEvent downEv, MotionEvent e,
                float distanceX, float distanceY) {
            
            if (mIsDraggingBody) {
                final PhysicBody selectedPhysics = mSelectedBody.getPhysics();
                final Vector2D touchPosition = mCamera.project(new Vector2D(e
                        .getX(), e.getY()));
                if (mScenePhysics.isSimulating()) {
                    Vector2D downPosition = mCamera.project(new Vector2D(downEv
                            .getX(), downEv.getY()));
                    /* Apply force to the position */
                    Vector2D movementImpulse = touchPosition.sub(downPosition)
                            .mul(selectedPhysics.getBodyMass());

                    selectedPhysics.applyImpulse(movementImpulse);
                } else
                    /* Just move the body */
                    selectedPhysics.setPosition(touchPosition);
            }

            switch (mMode) {
            case SCENE_MODE_PREVIEW:
                final Vector2D distance = mCamera.scale(new Vector2D(distanceX,
                        distanceY));
                mCamera.setPosition(mCamera.getPosition().add(distance));
                break;

            case SCENE_MODE_DRAWING:
                final Vector2D p = mCamera.project(new Vector2D(e.getX(), e
                        .getY()));
                mDrawingBody.newDrawingPoint(p);
                break;
            }

            return true;

        }

        /**
         * 
         */
        @Override
        public boolean onUp(MotionEvent e) {
            if (mIsDraggingBody) {
                mSelectedBody = null;
                mIsDraggingBody = false;
            }
            
            switch (mMode) {
            case SCENE_MODE_PREVIEW:
                break;
                
            case SCENE_MODE_DRAWING:
                mSceneRenderer.remove(mDrawingBody.getDrawingRender());
                final Body drawedBody = mDrawingBody.finalizeDrawing();
                add(drawedBody);
                
                System.gc();
                break;
            }
            return true;
        }

        /**
         * Called when fling occurs
         */
        public boolean onFling(MotionEvent onDownEv, MotionEvent e,
                float velocityX, float velocityY) {
            
            
            boolean handled = false;
            if (mSelectedBody != null) {
                final float cameraScale = mCamera.getAperture().i
                        / mDisplayWidth;
                final float vx = (velocityX * cameraScale);
                final float vy = (velocityY * cameraScale);

                mSelectedBody.getPhysics().setVelocity(vx, vy);
                handled = true;
            }

            return handled;
        }

        /**
     * 
     */
        public void onLongPress(MotionEvent e) {
            if (mSelectedBody != null) {
                mVibrator.vibrate(50); // vibrate as haptic feedback
                BodySettingsDialog dialog = new BodySettingsDialog(mContext,
                        mSelectedBody, Scene.this);
                dialog.show();
            }

        }

        @Override
        public boolean onPinch(MotionEvent ev1, MotionEvent ev2, float scale,
                float dX, float dY) {
            final Vector2D distance = mCamera.scale(new Vector2D(dX, dY));
            mCamera.setPosition(mCamera.getPosition().add(distance));
            mCamera.setAperture(mCamera.getAperture().mul(scale));
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

    };


    private final XmlSerializable mSerializer = new XmlSerializable() {
        private final static String TAG_FLINGBOX = "flingbox";
        
        /**
         * Scene exporter
         */
        @Override
        public boolean writeXml(XmlSerializer serializer)
                throws IllegalArgumentException, IllegalStateException,
                IOException {
            boolean writeSuccess = true;
            serializer.startTag("", TAG_FLINGBOX);
            for (Body body : mOnSceneBodies)
                writeSuccess &= body.writeXml(serializer);

            serializer.endTag("", TAG_FLINGBOX);
            return writeSuccess;
        }
    };

    private final XmlParseable mParser = new XmlParseable() {
        private final static String TAG_FLINGBOX = "flingbox";
        private final static String TAG_POLYGON = "polygon";
        
        /**
         * Scene importer
         */
        @Override
        public boolean readXml(XmlPullParser parser)
                throws XmlPullParserException, IOException, InvalidXmlException {
            boolean readSuccess = true;
            if ((parser.getEventType() != XmlPullParser.START_TAG)
                    || !(parser.getName().equals(TAG_FLINGBOX)))
                throw new InvalidXmlException("Scene start tag expected but "
                        + parser.getName() + " found.");

            for (int eventType = parser.next(); eventType != XmlPullParser.END_TAG; eventType = parser
                    .next()) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals(TAG_POLYGON)) {
                        Body newBody = new Polygon();
                        newBody.readXml(parser);
                        add(newBody);
                    } else
                        throw new InvalidXmlException(
                                "Any body start tag expected but "
                                        + parser.getName() + " found.");
                } else
                    throw new InvalidXmlException("Start tag expected but "
                            + parser.getName() + " found.");
            }
            if (!parser.getName().equals(TAG_FLINGBOX))
                throw new InvalidXmlException("Scene end tag expected but "
                        + parser.getName() + " found.");
            ;

            return readSuccess;
        }
    };

    public XmlSerializable getSerializer() {
        return mSerializer;
    }

    public XmlParseable getParser() {
        return mParser;
    }

}
