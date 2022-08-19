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
uniform samplerCube depthMap;
uniform float far_plane;

bool shadows = true;

vec3 offsets[25] = vec3[]
(
    vec3(0,0,0),  vec3(0, 1, 1), vec3(0, -1, 1),
    vec3(0,-1,-1), vec3(0,1,-1), vec3(0,0,-1),
    vec3(0,0,1), vec3(0,-1,0), vec3(0,-1,0),

    vec3(1,0,0),                 vec3(1, -1, 1),
    vec3(1,-1,-1), vec3(1,1,-1), vec3(1,0,-1),
    vec3(1,0,1), vec3(1,-1,0), vec3(1,1,0),

    vec3(-1,0,0),  vec3(-1,1,1), vec3(-1,-1,1),
                    vec3(-1,1,-1), vec3(-1,0,-1),
    vec3(-1,0,1), vec3(-1,-1,0), vec3(-1,1,0)

);



in struct FragmentData
{
    vec3 toCamera;
    vec3 toLight;
    vec3 toLightSP;
    vec3 normal;
    vec2 textureCoord;
    vec4 p;
} fragmentData;

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

    vec3 fragmentLight = normalize(pointLights[i].lp - fragmentData.p).xyz;
    vec3 emit = texture(diff, fragmentData.textureCoord).rgb;

    float level = max(0.0, dot(fragmentData.normal, fragmentLight));
    level = floor(level * 4) / 4;
    vec3 result = (pointLights[i].lightCol * emit * level);

    return result;

}

float shadowCalc(vec3 fragPos){
    vec3 lightToFrag = fragPos - pointLights[0].lp.xyz;
    float lenLightToFrag = length(lightToFrag);
    lightToFrag = normalize(lightToFrag);

    // PCF (Percentage closer filtering)
    float shadow = 0.0;
    float bias = 0.15;
    int samples = 25;

    // größerer radius = mehr blur
    float radius = 1.0 / 500.0;
    radius *= clamp(length(pointLights[0].lp.xyz - fragPos), 0.2, 6);

    for (int i = 0; i < samples; ++i){
        float depth = texture(depthMap, lightToFrag + offsets[i] * radius).r;
        depth *= far_plane;
        shadow += ((depth + bias) < lenLightToFrag) ? 0.0 : 1.0;
    }

    return shadow / float(samples);
}

vec3 calcAmbient(vec3 difftex , vec3 colorAmbient){
    vec3 ambient = (difftex * colorAmbient);
    return ambient;
}

void main() {

    //ambient
    vec3 ambient = calcAmbient(texture(diff, fragmentData.textureCoord).rgb, pointLights[0]. lightCol * 0.1);

    vec3 temp = 0.2 * pointLights[0].lightCol;

    float shadow = shadows ? shadowCalc(fragmentData.p.xyz) : 1.0;

    //lighting
    for (int i = 0; i < NR_POINT_LIGHTS; i++) {
        temp += calculatePointLight(i);
    }

    vec3 res = temp + ambient;

    color = vec4(res, 1.0);
}




