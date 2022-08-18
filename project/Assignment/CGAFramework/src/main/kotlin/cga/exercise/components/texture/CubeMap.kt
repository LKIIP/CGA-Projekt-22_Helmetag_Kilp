package cga.exercise.components.texture

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer


class CubeMap(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean): ITexture{
    private var texID: Int = -1
        private set

    init {
        try {
            processTexture(imageData, width, height, genMipMaps)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
    companion object {
        //create texture from file
        //don't support compressed textures for now
        //instead stick to pngs
        operator fun invoke(path: String, genMipMaps: Boolean, i : Int) {
            val x = BufferUtils.createIntBuffer(6)
            val y = BufferUtils.createIntBuffer(6)
            val readChannels = BufferUtils.createIntBuffer(6)
            var i = 0
            //flip y coordinate to make OpenGL happy
            STBImage.stbi_set_flip_vertically_on_load(false)
            val imageData = STBImage.stbi_load(path, x, y, readChannels, 4)
                ?: throw Exception("Image file \"" + path + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())
                return glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, x.get(), y.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData)

                STBImage.stbi_image_free(imageData)

        }
    }

    override fun processTexture(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) {

        texID = GL11.glGenTextures()
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
    }

    override fun setTexParams(wrapS: Int, wrapT: Int, minFilter: Int, magFilter: Int) {
        TODO("Not yet implemented")
    }


    fun setTexParamsCube(wrapS: Int, wrapT: Int, wrapR: Int, minFilter: Int, magFilter: Int) {
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
        glTexParameteri(ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, magFilter)
        glTexParameteri(ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, minFilter)
        glTexParameteri(ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, wrapS)
        glTexParameteri(ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, wrapT)
        glTexParameteri(ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, wrapR)
        unbind()
    }

    override fun bind(textureUnit: Int) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit)
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
    }

    override fun unbind() {
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
    }

    override fun cleanup() {
        unbind()
        if (texID != 0) {
            GL11.glDeleteTextures(texID)
            texID = 0
        }
    }
}