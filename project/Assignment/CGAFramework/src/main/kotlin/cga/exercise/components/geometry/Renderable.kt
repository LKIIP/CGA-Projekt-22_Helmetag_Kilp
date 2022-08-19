package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*

class Renderable(private var meshList: MutableList<Mesh>, private var materialList: MutableList<Material>, private var textureList: MutableList<Texture2D>,
                 _parent : Transformable? = null, var hp : Int, var hitbox: Float = 0f, var isEnemy : Boolean = false) : Transformable(parent = _parent), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        var name : String = "model_matrix"


            materialList.forEach{material ->
                val emit :Texture2D = material.emit
                emit.bind(0)
//               emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
                shaderProgram.setUniformInt("emit", 0)


                val diff :Texture2D = material.diff
                diff.bind(1)
//                diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
                shaderProgram.setUniformInt("diff", 1)


                val specular: Texture2D = material.specular
                specular.bind(2)
//                specular.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
                shaderProgram.setUniformInt("spec", 2)
                shaderProgram.setUniformVec2("tcMultiplier", material.tcMultiplier)
                shaderProgram.setUniformFloat("shininess", material.shininess)


            }
        meshList.forEach {mesh ->
            shaderProgram.setUniformMat(name, getWorldModelMatrix(), false)
            mesh.render(shaderProgram)

        }
    }
}