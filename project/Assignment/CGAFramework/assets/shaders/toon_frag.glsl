#version 330


uniform sampler2D emit;
uniform sampler2D diff;
uniform sampler2D spec;
uniform sampler2D groundToon;
uniform vec3 lightCol;
uniform float shininess;
uniform vec3 spotLightColor;
uniform float outerCutOff;
uniform float innerCutOff;
uniform vec3 pointLightAttenuation;
uniform vec3 spotLightAttenuation;
uniform vec3 spotLightDir;
uniform vec3 colorground;
uniform float zeit;
uniform mat4 view_matrix;


in struct VertexData
{
    vec3 toCamera;
    vec3 toLight;
    vec3 toLightSP;
    vec3 normal;
    vec2 textureCoord;
    vec4 p;
} vertexData;

struct PointLight{
    vec3 lightPos;
    vec3 lightCol;
    vec3 attenuations;
    vec4 lp;
};

#define NR_POINT_LIGHTS 5
uniform PointLight pointLights[NR_POINT_LIGHTS];

out vec4 color;

vec3 calculatePointLight(int i) {

    //geometric data
    vec3 fragmentLight = normalize(pointLights[i].lp - vertexData.p).xyz;

    // get lighting level
    float level = max(0.0, dot(vertexData.normal, fragmentLight));
    // quantize the level into, say, 4 levels
    level = floor(level * 4) / 4;
    vec3 result = pointLights[i].lightCol * vec3(1, 0, 0) * level;

    return result;

}

void main() {
    //ambient
    vec3 temp = 0.2 * vec3(1, 0, 0) ;

    //lighting
    for (int i = 0; i < NR_POINT_LIGHTS; i++) {
        temp += calculatePointLight(i);
    }

    color = vec4(temp, 1.0);
}




