//package cga.exercise.components.texture
//
//import cga.exercise.components.geometry.Mesh
//import cga.exercise.components.shader.ShaderProgram
//import org.lwjgl.BufferUtils
//import org.lwjgl.opengl.*
//import org.lwjgl.opengl.GL11.*
//import org.lwjgl.opengl.GL12.*
//import org.lwjgl.opengl.GL13.*
//import org.lwjgl.stb.STBImage
//import org.lwjgl.stb.STBImage.stbi_image_free
//import org.lwjgl.stb.STBImage.stbi_load
//import java.nio.ByteBuffer
//import java.util.StringJoiner
//
//class Skybox(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) : ITexture{
//
//    private var texID: Int = -1
//        private set
//
//    init {
//        try {
//            processTexture(imageData, width, height, genMipMaps)
//        } catch (ex: java.lang.Exception) {
//            ex.printStackTrace()
//        }
//
//    }
//
//    companion object {
//        //create texture from file
//        //don't support compressed textures for now
//        //instead stick to pngs
//        operator fun invoke(faces: MutableList<String>, genMipMaps: Boolean): Skybox {
//            var i = 0
//            val x = BufferUtils.createIntBuffer(1)
//            val y = BufferUtils.createIntBuffer(1)
//            val readChannels = BufferUtils.createIntBuffer(1)
//            var imageData: ByteBuffer? = STBImage.stbi_load(faces[0], x, y, readChannels, 4)
//            //flip y coordinate to make OpenGL happy
//            STBImage.stbi_set_flip_vertically_on_load(true)
//
//            faces.forEach {
//                imageData = STBImage.stbi_load(it, x, y, readChannels, 4)
//                    ?: throw Exception("Image file \"" + it + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())
//
//
//                try {
//
//                } catch (ex: java.lang.Exception) {
//                    ex.printStackTrace()
//                    throw ex
//                } finally {
//                    STBImage.stbi_image_free(imageData)
//                    i++
//                }
//            }
//            return Skybox(imageData!!, x.get(), y.get(), false)
//
//
//        }
//    }
//
//
//
//    override fun processTexture(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) {
//        texID = GL11.glGenTextures()
//        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
//        GL11.glTexImage2D(GL_TEXTURE_CUBE_MAP,0,GL11.GL_RGBA8,width,height,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,imageData)
//        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
//    }
//
//    override fun setTexParams(wrapS: Int, wrapT: Int, minFilter: Int, magFilter: Int) {
//        TODO("Not yet implemented")
//    }
//
//
//    override fun bind(textureUnit: Int) {
//        GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit)
//        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID)
//    }
//
//    override fun unbind() {
//        TODO("Not yet implemented")
//    }
//
//    override fun cleanup() {
//        TODO("Not yet implemented")
//    }
//
//
//}