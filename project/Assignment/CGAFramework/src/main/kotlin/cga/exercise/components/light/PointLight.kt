package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import java.util.*

open class PointLight(var lightPos: Vector3f, var lightCol: Vector3f, _parent : Transformable? = null) : Transformable(parent = _parent), IPointLight{

    open var attenuation : Vector3f = Vector3f(1f, 1f, 1f)

    init{
        translate(lightPos)
        attenuation = Vector3f(0.5f, 0.25f, 0.01f)
    }

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.use()
        shaderProgram.setUniformVec3("lightPos", getWorldPosition())
        shaderProgram.setUniformVec3("lightCol", lightCol)
        shaderProgram.setUniformVec3("pointLightAttenuation", attenuation)
    }

    override fun bindList(shaderProgram: ShaderProgram, viewMatrix: Matrix4f, count : Int) {

        var lp : Vector4f = Vector4f(getWorldPosition(), 1.0f).mul(viewMatrix)
        shaderProgram.use()
        shaderProgram.setUniformVec4("pointLights["+ count +"].lp", lp)
        shaderProgram.setUniformVec3("pointLights["+ count +"].lightPos", getWorldPosition())
        shaderProgram.setUniformVec3("pointLights["+ count +"].lightCol", lightCol)
        shaderProgram.setUniformVec3("pointLights["+ count +"].attenuations", attenuation)
    }

}