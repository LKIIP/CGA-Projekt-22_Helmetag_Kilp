#version 330 core
out vec4 FragColor;

in vec3 texCoords;

uniform samplerCube sky;
uniform sampler2D emit;
uniform sampler2D diff;
uniform sampler2D spec;



void main()
{


    FragColor = texture(sky, texCoords);
}
