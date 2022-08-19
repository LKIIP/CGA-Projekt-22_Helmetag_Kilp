#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 textureCoord;

out vec3 texCoords;

uniform mat4 model_matrix;
uniform mat4 view_sky;
uniform mat4 projection_matrix;

void main()
{
    texCoords =  aPos;
    vec4 pos= projection_matrix * view_sky * model_matrix * vec4(aPos, 1.0);
    gl_Position = pos.xyzz;
}