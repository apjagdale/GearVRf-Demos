/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.gearvrf.video.shaders;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterialMap;
//import org.gearvrf.GVRMaterialShaderManager;
//import org.gearvrf.GVRCustomMaterialShaderId;
import org.gearvrf.GVRShader;
import org.gearvrf.GVRShaderData;

public class AdditiveShader extends GVRShader{

    public static final String TEXTURE_KEY = "texture";
    public static final String WEIGHT_KEY = "u_weight";
    public static final String FADE_KEY = "u_fade";

    private static final String VERTEX_SHADER = "" //
            + "precision highp float;\n"
            + "in vec4 a_position;\n" //
            + "in vec2 a_texcoord;\n"

            + "layout (std140) uniform Transform_ubo{\n" +
            "     mat4 u_view;\n" +
            "     mat4 u_mvp;\n" +
            "     mat4 u_mv;\n" +
            "     mat4 u_mv_it;\n" +
            "     mat4 u_model;\n" +
            "     mat4 u_view_i;\n" +
            "     vec4 u_right;\n" +
            "};\n"

            + "layout (std140) uniform Material_ubo{\n" +
            "    vec4 u_mat1;\n" +
            "    vec4 u_mat2;\n" +
            "    vec4 u_mat3;\n" +
            "    vec4 u_mat4;\n" +
            "    vec4 u_eye;\n" +
            "    vec4 u_light;\n" +
            "    vec4 u_color;\n" +
            "    vec4 u_radius;\n" +
            "};"

           // + "uniform mat4 u_mvp;\n" //
            + "out vec2 coord;\n"
            + "void main() {\n" //
            + "  coord = a_texcoord;\n"
            + "  gl_Position = u_mvp * a_position;\n" //
            + "}\n";

    private static final String FRAGMENT_SHADER = "" //
            + "precision highp float;\n"
            + "in vec2  coord;\n" //

            + "layout (std140) uniform Material_ubo{\n" +
            "    vec4 u_mat1;\n" +
            "    vec4 u_mat2;\n" +
            "    vec4 u_mat3;\n" +
            "    vec4 u_mat4;\n" +
            "    vec4 u_eye;\n" +
            "    vec4 u_light;\n" +
            "    vec4 u_color;\n" +
            "    vec4 u_radius;\n" +
            "};"

            + "uniform sampler2D u_texture;\n"
            + "uniform float u_weight;\n" //
            + "uniform float u_fade;\n"
            + "out vec4 FragColor;\n"
            + "void main() {\n"
            + "  vec3 color1 = texture(u_texture, coord).rgb;\n"
            + "  vec3 color2 = vec3(0.0);\n"
            + "  vec3 color  = color1*(1.0-u_weight)+color2*u_weight;\n"
            + "  float alpha = length(color);\n"
            + "  FragColor = vec4( u_fade*color, alpha );\n" //
            + "}\n";

    //private GVRCustomMaterialShaderId mShaderId;
    private GVRMaterialMap mCustomShader = null;

    public AdditiveShader(GVRContext gvrContext) {
        /*
        final GVRMaterialShaderManager shaderManager = gvrContext
                .getMaterialShaderManager();
        mShaderId = shaderManager.addShader(VERTEX_SHADER, FRAGMENT_SHADER);
        mCustomShader = shaderManager.getShaderMap(mShaderId);
        mCustomShader.addTextureKey("texture", TEXTURE_KEY);
        mCustomShader.addUniformFloatKey("u_weight", WEIGHT_KEY);
        mCustomShader.addUniformFloatKey("u_fade", FADE_KEY);*/
        super("float u_weight, float u_fade", "sampler2D u_texture", "float4 a_position, float3 a_normal, float2 a_tex_coord", 300);
        setSegment("FragmentTemplate", FRAGMENT_SHADER);
        setSegment("VertexTemplate", VERTEX_SHADER);
    }

    protected void setMaterialDefaults(GVRShaderData material)
    {
        material.setFloat("u_weight", 1);
        material.setFloat("u_fade", 1);
    }
}