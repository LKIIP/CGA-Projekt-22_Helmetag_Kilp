#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 textureCoord;

out vec3 TexCoords;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

void main()
{
    TexCoords = vec3(64.0, 64.0, 64.0) *  aPos;
    gl_Position = projection_matrix * view_matrix * model_matrix * vec4(aPos, 1.0);
}