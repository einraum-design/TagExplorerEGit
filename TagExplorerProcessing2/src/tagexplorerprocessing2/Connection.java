package tagexplorerprocessing2;

import toxi.physics.VerletParticle;
import toxi.physics.VerletSpring;

public class Connection extends VerletSpring {
	
	static public enum ConnectionType
	{
	  VERSION, TAGBINDING, FILEBINDING
	}
	
	ConnectionType type; // version, tagBind, fileBind

	public Connection(VerletParticle arg0, VerletParticle arg1, float arg2, float arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	
	public Connection(VerletParticle arg0, VerletParticle arg1, float arg2, float arg3, ConnectionType type) {
		super(arg0, arg1, arg2, arg3);
		this.type = type;
	}

}
