package cga.exercise.components.camera

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class TronCamera(var fOV : Float = Math.toRadians(90.0).toFloat(), var aspectRatio : Float = 16f/9f, var nearPlane : Float = 0.1f,var farPlane : Float = 100f, _parent : Transformable? = null) : Transformable(parent =  _parent), ICamera {
    override fun getCalculateViewMatrix(): Matrix4f {
        return Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()), getWorldYAxis())

    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fOV, aspectRatio, nearPlane, farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniformMat("view_matrix", getCalculateViewMatrix(), false)
        shader.setUniformMat("projection_matrix", getCalculateProjectionMatrix(), false)
    }
}