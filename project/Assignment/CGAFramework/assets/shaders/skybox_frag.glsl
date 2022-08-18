#version 330 core
out vec4 FragColor;

in vec3 texCoords;

uniform samplerCube sky;

void main()
{
    FragColor = vec4(vec3(0.13, 0.13, 0.13), 1);
}
