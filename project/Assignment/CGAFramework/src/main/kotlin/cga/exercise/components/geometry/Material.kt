package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.lwjgl.opengl.GL11

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.use()
        shaderProgram.setUniformVec2("tcMultiplier", tcMultiplier)
        shaderProgram.setUniformFloat("shininess", shininess)
        emit.bind(0) // <--- eigentlich 0 (TextureUnit)
        diff.bind(1)
        specular.bind(2)
        shaderProgram.setUniformInt("emit", 0)
        shaderProgram.setUniformInt("diff", 1)
        shaderProgram.setUniformInt("spec", 2)
    }


}