#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 normal;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec3[63] samples;

    out vec4 fragPos;
    out vec3 Normal;
void main() {


    fragPos = (view_matrix * model_matrix * vec4(position, 1.0f));
    Normal = mat3(transpose(inverse(view_matrix*model_matrix))) * normal;


}
