package edu.eside.flingbox.scene;

import edu.eside.flingbox.bodies.Body;
import edu.eside.flingbox.graphics.RenderBody;
import edu.eside.flingbox.math.Vector2D;

interface DrawingBody {
    public RenderBody getDrawingRender();
    
    public void newDrawingPoint(final Vector2D point);
    
    public Body finalizeDrawing();
    
    public void cancelDrawing();
    
}
