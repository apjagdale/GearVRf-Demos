package org.gearvrf.complexscene;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRShaderTemplate;

public class ColorShader extends GVRShaderTemplate{

    public static final String COLOR_KEY = "u_color";

    private static final String VERTEX_SHADER = "attribute vec4 a_position;\n"
            + "uniform mat4 u_mvp;\n" //
            + "void main() {\n" //
            + "  gl_Position = u_mvp * a_position;\n" //
            + "}\n";

    private static final String FRAGMENT_SHADER = "precision mediump float;\n"
            + "uniform vec4 u_color;\n" //
            + "void main() {\n" //
            + "  gl_FragColor = u_color;\n" //
            + "}\n";


    public ColorShader(GVRContext gvrContext) {
        super("float4 u_color");
        setSegment("FragmentTemplate", FRAGMENT_SHADER);
        setSegment("VertexTemplate", VERTEX_SHADER);
    }
}