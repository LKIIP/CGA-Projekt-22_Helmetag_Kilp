#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 normal;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;
uniform vec2 tcMultiplier;
uniform vec3 lightPos;
uniform vec3 spotLightPos;
//

out struct VertexData
{
    vec3 toCamera;
    vec3 toLight;
    vec3 toLightSP;
    vec3 normal;
    vec2 textureCoord;
    vec4 p;
} vertexData;

void main(){
    vec4 pos = projection_matrix * view_matrix * model_matrix * vec4(position, 1.0f);
    gl_Position = pos;
    vertexData.textureCoord = tcMultiplier * textureCoord;

    vertexData.normal = mat3(transpose(inverse(view_matrix * model_matrix))) * normal;

    //toLight

    vertexData.p = (view_matrix * model_matrix * vec4(position, 1.0f));

    vec4 lp = view_matrix * model_matrix * vec4(lightPos, 1.0f);
    vec4 p = (view_matrix * model_matrix * vec4(position, 1.0f));
    vertexData.toLight = (lp - p).xyz;

    //SpotLightDir
    vertexData.toLightSP =  spotLightPos - p.xyz ;
    //ViewDir
    vertexData.toCamera = -p.xyz;

}
