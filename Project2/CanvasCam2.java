// Diyang Zhang

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.viewer.EasyViewer;
import mintools.viewer.FancyAxis;
import mintools.viewer.FlatMatrix4d;
import mintools.viewer.TrackBallCamera;

/**
 * OpenGL drawing canvas with a 2nd camera view of the world and a primary camera.
 * This canvas uses its own camera, a trackball camera implemented by the mintools jar.
 * @author kry
 */
public class CanvasCam2 implements GLEventListener {

	GLCanvas glCanvas;

	private Scene scene;
	    
	/** Our interactive camera interface */
    TrackBallCamera tbc = new TrackBallCamera();
    
    /** A camera to draw (visualize) from the camera 2 perspective */
    private DOFCamera dofCam;
    	
    /** Display list ID for quickly drawing the static scene geometry */
    private int list = -1;

	public CanvasCam2( Scene scene, DOFCamera dofCam ) {
		this.scene = scene;
		this.dofCam = dofCam;
		GLProfile glp = GLProfile.getDefault();
        GLCapabilities glcap = new GLCapabilities(glp);
        glCanvas = new GLCanvas( glcap );
        glCanvas.setSize( 500, 500 );
        glCanvas.addGLEventListener(this);
        final FPSAnimator animator; 
        animator = new FPSAnimator(glCanvas, 60);
        animator.start();
        tbc.attach( glCanvas );
        tbc.near.setDefaultValue(5.0);
        tbc.near.setValue(5);
	}

    /** 
     * initializes the class for display 
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0f); // Black Background
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
        gl.glEnable( GL2.GL_NORMALIZE ); // normals stay normal length under scale
        // default blending properties for some simple anti-aliasing
        gl.glEnable( GL.GL_BLEND );
        gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL.GL_LINE_SMOOTH );
        gl.glEnable( GL2.GL_POINT_SMOOTH );
    }
        
    @Override
    public void display(GLAutoDrawable drawable) {
    	GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();
        tbc.applyProjectionTransformation(drawable);
        
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
        tbc.applyViewTransformation(drawable);
        
        list = scene.display( drawable, list );
                

        // Edited by Diyang
        
        final FlatMatrix4d P = new FlatMatrix4d();
        final FlatMatrix4d Pinv = new FlatMatrix4d();
        final FlatMatrix4d V = new FlatMatrix4d();
        final FlatMatrix4d Vinv = new FlatMatrix4d();
		
        // Get the V1 and P1 matrices of camera 1, the DOF camera
        gl.glPushMatrix();
        gl.glLoadIdentity();
        dofCam.setupProjection( drawable, 0 );
        gl.glGetDoublev( GL2.GL_MODELVIEW_MATRIX, P.asArray(), 0 );
		P.reconstitute();
		Pinv.getBackingMatrix().invert( P.getBackingMatrix() );
		gl.glLoadIdentity();
        dofCam.setupViewingTransformation( drawable, 0 );
        gl.glGetDoublev( GL2.GL_MODELVIEW_MATRIX, V.asArray(), 0 );
		V.reconstitute();
		Vinv.getBackingMatrix().invert( V.getBackingMatrix() );
		gl.glPopMatrix();
		
		// here is some code to draw a fancy axis
		// EDITED: Draw the axis at the location of DOFcamera
		gl.glPushMatrix();
        gl.glMultMatrixd(Vinv.asArray(), 0);
		final FancyAxis fa = new FancyAxis();
		fa.draw(gl);
		gl.glPopMatrix();
		
		// Added: Frustum & Sensor Plane
		gl.glMultMatrixd(Vinv.asArray(), 0);
		//gl.glMultMatrixd(Pinv.asArray(), 0);
		dofCam.drawSensorPlane(drawable);
		dofCam.drawFocusPlane(drawable);
		gl.glMultMatrixd(Pinv.asArray(), 0);
		// Here is some code to draw a red wire cube of size 2
		gl.glDisable( GL2.GL_LIGHTING );
		gl.glColor3f(1,0,0);
        final GLUT glut = new GLUT();
		glut.glutWireCube(2);
		gl.glEnable( GL2.GL_LIGHTING );
		
		
		gl.glColor3f(1,1,1);
        EasyViewer.beginOverlay(drawable);
        EasyViewer.printTextLines( drawable, "Camera 2 View", 10, 20, 12, GLUT.BITMAP_HELVETICA_18 );
        gl.glEnable( GL2.GL_LIGHTING );
        EasyViewer.endOverlay(drawable);
    }    
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
    	// do nothing
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    	// do nothing, glViewPort already called by the component!
    }
    
}
