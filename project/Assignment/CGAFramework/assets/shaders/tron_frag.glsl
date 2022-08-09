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

void main(){

    vec3 emittexSRGB = texture(emit, vertexData.textureCoord).rgb;
    vec3 spectexSRGB = texture(spec,vertexData.textureCoord).rgb;
    vec3 difftexSRGB = texture(diff, vertexData.textureCoord).rgb;

    emittexSRGB *= colorground;
    vec3 colorg = colorground;
    vec3 bikecol = emittexSRGB;
    if (zeit != 1 && (emittexSRGB.g > 0 || emittexSRGB.r > 0 || emittexSRGB.b > 0)){
        bikecol =  vec3(0.7+(sin(zeit)/2), 0 , 0.7+(sin(zeit)/2));
        emittexSRGB = bikecol;

    }

    float gamma = 2.2;
    vec3 emittex = linear(emittexSRGB, gamma);
    vec3 spectex = linear(spectexSRGB, gamma);
    vec3 difftex = linear(difftexSRGB, gamma);

    //Ambient
    vec3 ambient = calcAmbient(difftex, pointLights[0].lightCol*0.01);

    // Bike Point Light
    vec3 toLight = (pointLights[0].lp - vertexData.p).xyz;
    float attenuationPL = calcAttenuation(length(toLight), pointLights[0].attenuations);
    vec3 diffSpecPL = calcDiffSpec(vertexData.normal, toLight,vertexData.toCamera,  difftex,  spectex, shininess );
    vec3 pointLightCol = attenuationPL * (vec3(sin(zeit)/2, 0, sin(zeit)/2));
    vec3 res = diffSpecPL * pointLightCol;

     for(int i = 1; i < NR_POINT_LIGHTS; i++) {
        vec3 toLight = (pointLights[i].lp - vertexData.p).xyz;
        vec3 mulpointLightCol = pointLightColor(pointLights[i], vertexData.normal, toLight, vertexData.toCamera,
        difftex,  spectex, shininess, length(toLight), pointLights[i].attenuations);
        res += mulpointLightCol;
     }

    //SpotLight
    float atteunationSL = calcAttenuation(length(vertexData.toLightSP), spotLightAttenuation);
    vec3 diffSpecSL = calcDiffSpec(vertexData.normal, vertexData.toLightSP, vertexData.toCamera, difftex, spectex, shininess);
    float intensitySL = calcIntensity(spotLightDir,vertexData.toLightSP, outerCutOff, innerCutOff);
    vec3 spotLightCol = intensitySL* atteunationSL * spotLightColor;

    // Color  Linear
    //vec3 result = (emittex + ambient + (pointLightColor * diffSpecPL +  diffSpecSL * spotLightCol));
    vec3 mulRes = (emittex + ambient + res + (diffSpecSL * spotLightCol));

    // FragColor Gamma
    color = vec4(outgamma(mulRes.rgb, gamma),1.0f);

}
