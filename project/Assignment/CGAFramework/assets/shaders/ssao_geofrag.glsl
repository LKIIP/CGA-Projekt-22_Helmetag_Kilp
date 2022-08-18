#version 330

layout(location = 3) out vec3 gPosition;
layout(location = 4) out vec3 gNormal;
layout(location = 5) out vec4 gAlbedo;

in vec4 fragPos;
in vec3 Normal;

uniform vec3[63] samples;

void main() {
        gPosition = fragPos.xyz;
        gAlbedo.rgb = vec3(0.95);
        gNormal = Normal;

}
