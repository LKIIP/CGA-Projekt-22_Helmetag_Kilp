#version 330 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in struct VertexData
{
    vec3 toCamera;
    vec3 toLight;
    vec3 toLightSP;
    vec3 normal;
    vec2 textureCoord;
    vec4 p;
} vertexData[];

out struct FragmentData
{
    vec3 toCamera;
    vec3 toLight;
    vec3 toLightSP;
    vec3 normal;
    vec2 textureCoord;
    vec4 p;
} fragmentData;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

void main() {

    gl_Position =  gl_in[0].gl_Position;
    fragmentData.toCamera = vertexData[0].toCamera;
    fragmentData.toLight = vertexData[0].toLight;
    fragmentData.toLightSP = vertexData[0].toLightSP;
    fragmentData.normal = vertexData[0].normal;
    fragmentData.p = vertexData[0].p;
    fragmentData.textureCoord = vertexData[0].textureCoord;
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    fragmentData.toCamera = vertexData[1].toCamera;
    fragmentData.toLight = vertexData[1].toLight;
    fragmentData.toLightSP = vertexData[1].toLightSP;
    fragmentData.normal = vertexData[1].normal;
    fragmentData.p = vertexData[1].p;
    fragmentData.textureCoord = vertexData[1].textureCoord;
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    fragmentData.toCamera = vertexData[2].toCamera;
    fragmentData.toLight = vertexData[2].toLight;
    fragmentData.toLightSP = vertexData[2].toLightSP;
    fragmentData.normal = vertexData[2].normal;
    fragmentData.p = vertexData[2].p;
    fragmentData.textureCoord = vertexData[2].textureCoord;
    EmitVertex();

    EndPrimitive();

}
