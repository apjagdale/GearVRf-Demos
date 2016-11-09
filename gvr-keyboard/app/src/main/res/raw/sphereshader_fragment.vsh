#version 300 es
precision mediump float;

in vec2  coord;
in vec3  normal;
in vec3  view;
in vec3  light;

uniform sampler2D HDRI_texture;
uniform sampler2D texture_t;
uniform sampler2D second_texture;

vec2  animOffset;

in vec3  n;
in vec3  v;
in vec3  l;
in vec3  p;

layout (std140) uniform Material_ubo{
    vec4 u_eye;
    vec4 u_light;
    vec4 trans_color;
    vec4 animTexture;
    vec4 blur;
    vec4 u_radius;
};
out vec4 outCol;
void main() {

	vec2 reflect_coord;
	float reflect_intensity = 0.1;
	float division = 7.0;

	vec3  r = normalize(reflect(v,n));
    float b = dot(r,p);
    float c = dot(p,p)-u_radius.x * u_radius.x;
    float t = sqrt(b*b-c);

    if( -b + t > 0.0 ) t = -b + t;
    else t = -b - t;

    vec3 ray = normalize(p+t*r);
    ray.z = ray.z/sqrt(ray.x*ray.x+ray.z*ray.z);

    if( ray.x > 0.0 ) reflect_coord.x =  ray.z + 1.0;
    else              reflect_coord.x = -ray.z - 1.0;

    reflect_coord.x /= 2.0;
    reflect_coord.y  = ray.y;
    reflect_coord.x = 0.5 + 0.6*asin(reflect_coord.x)/1.57079632675;
    reflect_coord.y = 0.5 + 0.6*asin(reflect_coord.y)/1.57079632675;

	animOffset = vec2(animTexture.x,0.0);

	vec4 reflect = texture(HDRI_texture, reflect_coord);
	vec4 color = texture(texture_t, coord) / division;
	vec3 color2 = texture(texture_t, coord ).rgb;
	vec4 color3 = texture(second_texture, coord + animOffset - vec2(1,0.0));

	color += texture(texture_t, (coord * (0.9)) + vec2(0.05,0.05)) / division;
	color += texture(texture_t, (coord * (0.85)) + vec2(0.075,0.075)) / division;
	color += texture(texture_t, (coord * (0.8)) + vec2(0.1,0.1)) / division;
	color += texture(texture_t, (coord * (0.75)) + vec2(0.125,0.125)) / division;
	color += texture(texture_t, (coord * (0.7)) + vec2(0.15,0.15)) / division;
	color += texture(texture_t, (coord * (0.65)) + vec2(0.175,0.175)) / division;

	vec3 finalColor = (color.rgb * blur.x) + (color2 * (1.0-blur.x));
	if(color3.w == 0.0){
		finalColor = (  trans_color.xyz * animTexture.x + (finalColor * (1.0 -animTexture.x))) + color3.rgb;
	}

	else{
		finalColor = color3.rgb;
	}

	vec3  h = normalize(v+l);
	float diffuse  = max ( dot(l,n), 0.2 );

	float specular = max ( dot(h,n), 0.0 );
	specular = pow (specular, 300.0);

	finalColor *= diffuse;
	finalColor += 0.5*(1.0- finalColor)*specular;
	if(reflect.r > 0.5)
		finalColor = (finalColor + reflect.rgb * reflect_intensity);

	outCol = vec4( finalColor * 1.0 - blur.x/2.0, 1.0 );
	outCol.a = 1.0 - min(0.9,blur.x * dot(v,n));
}