# ifdef GL_ES
precision mediump float;
# endif

uniform float shaderTime;
uniform vec2 res;
uniform vec2 mouse;
uniform vec3 color;


void main()
{
	float r = 0.0;
	r = color.x;
	r = ((sin(shaderTime) / 2.0) + 0.5);
	r *= (-(gl_FragCoord.y / res.y) + 1.0);
	
	float g = color.y;
	float b = color.z;
	
	
	
	
	
	gl_FragColor = vec4(1.0 - r, g, b ,0.3);
}