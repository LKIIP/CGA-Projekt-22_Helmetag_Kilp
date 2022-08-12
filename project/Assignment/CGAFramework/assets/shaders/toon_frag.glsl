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
uniform samplerCube sky;


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
    vec3 fragmentLightBike = normalize(pointLights[0].lp - vertexData.p).xyz;

    // get lighting level
    float level = max(0.0, dot(vertexData.normal, fragmentLight));
    //float levelBike = max(0.0, dot(vertexData.normal, fragmentLightBike));
    // quantize the level into, say, 4 levels
    //levelBike = floor(levelBike * 16)/16;
    level = floor(level * 8) / 8;
    vec3 result = (pointLights[i].lightCol  * vec3(0,1,1) * level) ;

    //if(levelBike <= 0.75){ result *= (pointLights[0].lightCol * vec3(0.5,0,1) * levelBike);}

    if (level <=1){result = pointLights[i].lightCol * vec3(0,1,1 ) * level;}
//    if (level <=0.80){result = pointLights[i].lightCol * vec3(0,1,0 ) * level ;}
    if (level <=0.50){result = pointLights[i].lightCol * vec3(0,0.5,1 ) * level ;}
//    if (level <=0.40){result = pointLights[i].lightCol * vec3(0,0,1) * level;}
//    if (level <=0.20){result = pointLights[i].lightCol * vec3(1,0,1) * level;}

    return result;

}

void main() {
    //ambient
    vec3 temp = 0.2 * vec3(1, 1, 1) ;

    //lighting
    for (int i = 0; i < NR_POINT_LIGHTS; i++) {
        temp += calculatePointLight(i);
    }

    color = vec4(temp, 1.0) * 0.25;
}




