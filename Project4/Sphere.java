// Diyang Zhang

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
    	// Note that t = (-dp [+/-] sqrt((dp)^2-(dd)(pp-rr)))/dd
    	Vector3d d = new Vector3d(ray.viewDirection);
    	Vector3d p = new Vector3d(ray.eyePoint);
    	p.sub(center);
    	double sqr = (d.dot(p))*(d.dot(p)) - d.dot(d)*(p.dot(p)-radius*radius);	// sqr=(dp)^2-(dd)(pp-rr)
    	if (sqr>=0) {
    		double tminus = (-(d.dot(p))-Math.sqrt(sqr)) / (d.dot(d)); 
    		if (tminus>0 && tminus<result.t) {
    			result.t = tminus;
    			result.p.scaleAdd(tminus, ray.viewDirection, ray.eyePoint);
    			result.n.sub(result.p, center);
    			result.n.normalize();
    			result.material = material;
    		}
       	}
	    
    }
}
