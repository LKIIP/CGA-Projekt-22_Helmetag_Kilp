package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Matrix4f

class Renderable(private var meshList: MutableList<Mesh>, private var materialList: MutableList<Material>, private var textureList: MutableList<Texture2D>,
                 _parent : Transformable? = null, var hp : Int, var hitbox: Float = 0f) : Transformable(parent = _parent), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        var name : String = "model_matrix"
        meshList.forEach {mesh ->
            shaderProgram.setUniformMat(name, getWorldModelMatrix(), false)
            mesh.render(shaderProgram)

        }
        materialList.forEach{material ->
            val emit :Texture2D = material.diff
            val diff :Texture2D = material.emit
            val specular: Texture2D = material.specular
            shaderProgram.setUniformVec2("tcMultiplier", material.tcMultiplier)
            shaderProgram.setUniformFloat("shininess", material.shininess)
            emit.bind(0) // <--- eigentl/ich 0 (TextureUnit)
            diff.bind(1)
            specular.bind(2)
            shaderProgram.setUniformInt("emit", 0)
            shaderProgram.setUniformInt("diff", 1)
            shaderProgram.setUniformInt("spec", 2)


        }
    }
}