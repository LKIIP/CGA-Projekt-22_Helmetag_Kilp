package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

interface IPointLight {
    fun bind(shaderProgram: ShaderProgram)

    fun bindList(shaderProgram: ShaderProgram, viewMatrix: Matrix4f, count : Int)
}

