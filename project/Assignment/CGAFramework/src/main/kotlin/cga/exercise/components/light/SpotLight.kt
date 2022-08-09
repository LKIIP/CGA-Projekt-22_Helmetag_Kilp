package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import javax.xml.crypto.dsig.Transform

class SpotLight(lightpos : Vector3f,  lightColor : Vector3f, var outerAngle : Float, var innerAngle : Float, _parent : Transformable?) : PointLight(lightpos, lightColor, _parent = _parent), ISpotLight {

    override var attenuation : Vector3f = Vector3f( 1f, 1f, 1f)


    override fun bindList(shaderProgram: ShaderProgram, viewMatrix: Matrix4f, count: Int) {
        TODO("Not yet implemented")
    }

    init {
        attenuation = Vector3f(0.5f,0.05f,0.0f)
        translate(lightpos)
    }

    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f) {
        var pos = Vector4f(getWorldPosition(),1.0f).mul(viewMatrix)
        shaderProgram.setUniformVec3("spotLightColor", lightCol)
        shaderProgram.setUniformVec3("spotLightPos", Vector3f(pos.x,pos.y,pos.z))
        shaderProgram.setUniformVec3("spotLightDir", getWorldZAxis().negate().mul(Matrix3f(viewMatrix)))
        shaderProgram.setUniformFloat("outerCutOff", Math.cos(Math.toRadians(outerAngle)))
        shaderProgram.setUniformFloat("innerCutOff", Math.cos(Math.toRadians(innerAngle)))
        shaderProgram.setUniformVec3("spotLightAttenuation",attenuation)
    }
}