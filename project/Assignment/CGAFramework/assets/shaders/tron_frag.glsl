#version 330 core

//
uniform sampler2D emit;
uniform sampler2D diff;
uniform sampler2D spec;
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
uniform float far_plane;
uniform samplerCube depthMap;

float closestDepth = 0;

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

vec3 calcAmbient(vec3 difftex , vec3 colorAmbient){
    vec3 ambient = (difftex * colorAmbient);
    return ambient;
}

vec3 calcDiffSpec(vec3 normal, vec3 toLight,vec3 toCamera, vec3 difftex, vec3 spectex, float shininess ){

    float cosa = max(dot(normalize(normal),normalize(toLight)),0.0f);
    vec3 diffuse = cosa * difftex;
    vec3 reflect = reflect(normalize(-toLight), normalize(normal));
    vec3 halfway = normalize(toLight + toCamera);
    float phongspec = max(dot(normalize(toCamera), reflect) ,0.0f);
    float phongspecK = pow(phongspec, shininess);
    float blinn = pow(max(dot(normalize(normal), halfway), 0.0), shininess);
    vec3 spec = spectex * blinn;

    return diffuse + spec;
}

float calcAttenuation(float distance, vec3 attenuationValues ){
    float attenuation = 1.0f/(attenuationValues.x + attenuationValues.y * distance + attenuationValues.z * (distance * distance));
    return attenuation;

}

//Intensity
float calcIntensity(vec3 lightDir , vec3 slightDir, float outerCutOff, float innerCutOff ){
    float theta = dot(normalize(lightDir), normalize(-slightDir));
    float intensity = clamp((theta - outerCutOff) / (innerCutOff - outerCutOff) , 0.0f, 1.0f);
    return intensity;
}

vec3 linear(vec3 color, float gamma){
    vec3 linear = pow(color, vec3(gamma));
    return linear;
}

vec3 outgamma(vec3 color, float gamma){
    vec3 outgamma = pow(color, vec3(1.0/gamma));
    return outgamma;
}

vec3 pointLightColor(PointLight light, vec3 normal,vec3 toLight,vec3 toCamera, vec3 difftex, vec3 spectex,
                    float shininess, float distance, vec3 attenuationValues) {

        vec3 lightColor = light.lightCol;
        //diffspec
        float cosa = max(dot(normalize(normal),normalize(toLight)),0.0f);
        vec3 diffuse = cosa * difftex;
        vec3 reflect = reflect(normalize(-toLight), normalize(normal));
        vec3 halfway = normalize(toLight + toCamera);
        float phongspec = max(dot(normalize(toCamera), reflect) ,0.0f);
        //float phongspecK = pow(cosBeta, shininess);
        float blinn = pow(max(dot(normalize(normal), halfway), 0.0), shininess);
        vec3 spec = spectex * blinn;

        float attenuation = 1.0f/(attenuationValues.x + attenuationValues.y * distance + attenuationValues.z * (distance * distance));
        vec3 attencol = attenuation * lightColor;

        vec3 mulpointLightCol =  ((diffuse + spec) * attencol);

        return mulpointLightCol;
}

float shadowCalc(vec3 fragPos){
    vec3 lightToFrag = fragPos - pointLights[0].lp.xyz;
    float lenLightToFrag = length(lightToFrag);
    lightToFrag = normalize(lightToFrag);

    closestDepth = texture(depthMap, lightToFrag).r;
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

void main(){

    vec3 emittexSRGB = texture(emit, fragmentData.textureCoord).rgb;
    vec3 spectexSRGB = texture(spec,fragmentData.textureCoord).rgb;
    vec3 difftexSRGB = texture(diff, fragmentData.textureCoord).rgb;


    float gamma = 2.2;
    vec3 emittex = linear(emittexSRGB, gamma);
    vec3 spectex = linear(spectexSRGB, gamma);
    vec3 difftex = linear(difftexSRGB, gamma);

    //Ambient
    vec3 ambient = calcAmbient(difftex, pointLights[0].lightCol);

    vec3 res = vec3(0, 0 , 0);

     for(int i = 0; i < NR_POINT_LIGHTS; i++) {
        vec3 toLight = (pointLights[i].lp - fragmentData.p).xyz;
        vec3 mulpointLightCol = pointLightColor(pointLights[i], fragmentData.normal, toLight, fragmentData.toCamera,
        difftex,  spectex, shininess, length(toLight), pointLights[i].attenuations);
        res += mulpointLightCol;
     }

    //SpotLight
    float atteunationSL = calcAttenuation(length(fragmentData.toLightSP), spotLightAttenuation);
    vec3 diffSpecSL = calcDiffSpec(fragmentData.normal, fragmentData.toLightSP, fragmentData.toCamera, difftex, spectex, shininess);
    float intensitySL = calcIntensity(spotLightDir,fragmentData.toLightSP, outerCutOff, innerCutOff);
    vec3 spotLightCol = intensitySL* atteunationSL * spotLightColor;

    float shadow = shadows ? shadowCalc(fragmentData.p.xyz) : 1.0;

    // Color  Linear
    //vec3 result = (emittex + ambient + (pointLightColor * diffSpecPL +  diffSpecSL * spotLightCol));
   // vec3 mulRes = (emittex + ambient + res + (diffSpecSL * spotLightCol));
    vec3 mulRes = (ambient + emittex + res + (diffSpecSL * spotLightCol));


    // FragColor Gamma
    color = vec4(outgamma(mulRes.rgb, gamma),1.0f);

    //color = vec4(vec3(closestDepth / far_plane), 1.0);
}
