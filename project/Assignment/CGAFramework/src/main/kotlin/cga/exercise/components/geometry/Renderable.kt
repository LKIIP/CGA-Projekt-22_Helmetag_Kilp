package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Renderable(private var meshList: MutableList<Mesh>, _parent : Transformable? = null) : Transformable(parent = _parent), IRenderable {
    override fun render(shaderProgram: ShaderProgram) {
        var name : String = "model_matrix"
        meshList.forEach {mesh ->
            shaderProgram.setUniformMat(name, getWorldModelMatrix(), false)
            mesh.render(shaderProgram)
        }
    }
}