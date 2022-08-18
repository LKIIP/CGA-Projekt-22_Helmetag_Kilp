#version 330 core
out vec4 FragColor;

in vec3 texCoords;

uniform samplerCube sky;

void main()
{
    FragColor = texture(sky, texCoords);
}
