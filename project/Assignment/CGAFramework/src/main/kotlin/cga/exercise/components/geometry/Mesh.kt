package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*

import org.lwjgl.opengl.GL30

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>,var  material: Material? = null) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = indexdata.count()



    init {
        // todo: place your code here
        // todo: generate IDs
        vao = glGenVertexArrays() //Position und Farbe
        vbo = glGenBuffers()    //infos dazu, wie zu verwenden
        ibo = glGenBuffers()    //welche punkte verbinden
        // todo: bind your objects
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        // todo: upload your mesh data
        glBufferData(GL_ARRAY_BUFFER, vertexdata, GL_STATIC_DRAW)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexdata, GL_STATIC_DRAW)


        var i = 0
        attributes.forEach {
            glEnableVertexAttribArray(i)
            glVertexAttribPointer(i, it.n, it.type, false, it.stride, it.offset.toLong())
            i++
        }

    glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)


    }

    /**
     * renders the mesh
     */
    fun render() {
        // todo: place your code here
        // call the rendering method every frame
        glBindVertexArray(vao)  //activate VAO
        //GL11.glDrawElements(GL_TRIANGLES, 9, GL_UNSIGNED_INT, 0) // render call
        GL11.glDrawElements(GL_TRIANGLES, indexcount, GL_UNSIGNED_INT, 0) // render call

        glBindVertexArray(0)  //reset
    }

    fun render(shaderProgram: ShaderProgram){
        render()
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}