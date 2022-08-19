
#version 330 core
in vec4 FragPos;

uniform float far_plane;
uniform samplerCube depthMap;

struct PointLight{
    vec3 lightPos;
    vec3 lightCol;
    vec3 attenuations;
    vec4 lp;
};

#define NR_POINT_LIGHTS 1
uniform PointLight pointLights[NR_POINT_LIGHTS];
void main()
{
    // get distance between fragment and light source
    float lightDistance = length(FragPos.xyz - pointLights[0].lightPos);

    // map to [0;1] range by dividing by far_plane
    lightDistance = lightDistance / far_plane;

    // write this as modified depth
    gl_FragDepth = lightDistance;
}  