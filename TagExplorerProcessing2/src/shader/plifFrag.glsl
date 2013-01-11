// 
// Bui Tuong Phong shading model (per-pixel) 
// 
// by 
// Massimiliano Corsini
// Visual Computing Lab (2006)
//

varying vec3 normal;
varying vec3 vpos;

uniform float shininess;

const vec3 Xunitvec = vec3 (1.0, 0.0, 0.0);
const vec3 Yunitvec = vec3 (0.0, 1.0, 0.0);

uniform vec3  BaseColor;
uniform float MixRatio;

uniform sampler2D EnvMap;

varying vec3  Normal;
varying vec3  EyeDir;

void main()
{
	vec3 n = normalize(normal);
	vec4 diffuse = vec4(0.0);
	vec4 specular = vec4(0.0);
	
	// the material properties are embedded in the shader (for now)
	vec4 mat_ambient = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 mat_diffuse = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 mat_specular = vec4(1.0, 1.0, 1.0, 1.0);
	
	// ambient term
	vec4 ambient = mat_ambient * gl_LightSource[0].ambient;
	
	// diffuse color
	vec4 kd = mat_diffuse * gl_LightSource[0].diffuse;
	
	// specular color
	vec4 ks = mat_specular * gl_LightSource[0].specular;
	
	// diffuse term
	vec3 lightDir = normalize(gl_LightSource[0].position.xyz - vpos);
	float NdotL = dot(n, lightDir);
	
	if (NdotL > 0.0)
		diffuse = kd * NdotL;
	
	// specular term
	vec3 rVector = normalize(2.0 * n * dot(n, lightDir) - lightDir);
	vec3 viewVector = normalize(-vpos);
	float RdotV = dot(rVector, viewVector);
	
	if (RdotV > 0.0)
		specular = ks * pow(RdotV, shininess);

	// environment mapping

	// Compute reflection vector   
	vec3 reflectDir = reflect(EyeDir, Normal);

	// this is the version implemented from the OpenGL-1.2 programming guide:
	reflectDir.z += 1.0;
	float inv_m = 0.5/sqrt(dot(reflectDir,reflectDir));
	vec2 index = reflectDir.xy * inv_m + 0.5;
  
  
	// Do a lookup into the environment map.

	vec4 envColor = vec4 (texture2D(EnvMap, index));

	// Add lighting to base color and mix

	//vec3 base = LightIntensity * BaseColor;
	envColor = mix(envColor, gl_Color, MixRatio);

	gl_FragColor = envColor * (ambient + diffuse + specular);
} 

