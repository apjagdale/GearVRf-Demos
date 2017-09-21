precision mediump float;
in vec2  coord;
uniform sampler2D grayScaleTexture;
uniform sampler2D detailsTexture;

@MATERIAL_UNIFORMS

out vec4 outColor;
void main() {
	
	vec4 colorGrayScale;
	vec4 colorDetails;
	
    colorGrayScale = texture(grayScaleTexture, coord);
    colorDetails = texture(detailsTexture, coord);
    
    vec4 colorResult = colorGrayScale * u_color;

	if(colorDetails.a != 0.0){
	
		colorResult = colorResult * (1.0-colorDetails.a) + colorDetails * (colorDetails.a);
        
	}
    		
	outColor = colorResult * u_opacity;
}