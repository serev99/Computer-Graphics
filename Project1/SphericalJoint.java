//Diyang Zhang

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

import javax.vecmath.Tuple3d;

public class SphericalJoint extends GraphNode {

	Tuple3d transl;
	DoubleParameter rotx;
	DoubleParameter roty;
	DoubleParameter rotz;
		
	public SphericalJoint(String name, Tuple3d transl,
						double minRxValue, double maxRxValue,
						double minRyValue, double maxRyValue,
						double minRzValue, double maxRzValue) {
		
		super(name);
		this.transl = transl;
		//"Expose the three rotation parameters by adding them to the GraphNode.dofs collection"
		dofs.add( rotx = new DoubleParameter( name+" rotx", 0, minRxValue, maxRxValue ) );
		dofs.add( roty = new DoubleParameter( name+" roty", 0, minRyValue, maxRyValue ) );
		dofs.add( rotz = new DoubleParameter( name+" rotz", 0, minRzValue, maxRzValue ) );
		
	}
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
         
        //Similar in class FreeJoint
        gl.glTranslated(transl.x, transl.y, transl.z);
		gl.glRotated(rotx.getValue(), 1, 0, 0);
		gl.glRotated(roty.getValue(), 0, 1, 0);
		gl.glRotated(rotz.getValue(), 0, 0, 1);
        
        super.display(drawable);
        gl.glPopMatrix();
	}
}
