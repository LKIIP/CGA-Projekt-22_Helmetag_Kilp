package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
//import cga.exercise.components.texture.Skybox
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.*
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.ARBFramebufferObject.*
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL32.glFramebufferTexture
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.atan2


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private var depthCubeMap :Int
    private val staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val toonShader: ShaderProgram
    private val ssaoGeoShader: ShaderProgram
    private val shadowShader: ShaderProgram
    private val shaderList: MutableList<ShaderProgram> = arrayListOf()
    private val shadowTransform : MutableList<Matrix4f> = arrayListOf()

    private var depthMap : Int =-1
    private var depthMapFBO : Int = -1
    private val SHADOW_WIDTH :Int
    private val SHADOW_HEIGHT: Int
    private val lightProjection : Matrix4f
    private val lightView : Matrix4f
    private val lightSpaceMatrix : Matrix4f
    private val ShadowProj: Matrix4f
//    private val meshG: Mesh
//    private val meshE : Mesh
    private val meshSkybox: Mesh
    private val meshS : Mesh
    private val meshListS : MutableList<Mesh> = arrayListOf()
    private val meshListTree : MutableList<Mesh> = arrayListOf()
    private val meshListG : MutableList<Mesh> = arrayListOf()
    private val meshListE : MutableList<Mesh> = arrayListOf()
    private val meshListSkybox : MutableList<Mesh> = arrayListOf()

    private val MeshListLevel  : MutableList<Mesh> = arrayListOf()
    private val cam : TronCamera
    private val cam1 : TronCamera
    private val cam2 : TronCamera
    private val cam3 : TronCamera

    private val pointLight : PointLight
    private val pointLight1 : PointLight
    private val pointLight2 : PointLight
    private val pointLight3 : PointLight
    private val pointLight4 : PointLight
    private val spotLight : SpotLight
    private val pointList: MutableList<PointLight> = arrayListOf()
    private val skyboxList: MutableList<String> = arrayListOf("assets/textures/skybox/left.jpg",
        "assets/textures/skybox/right.jpg",
        "assets/textures/skybox/top.jpg",
        "assets/textures/skybox/bottom.jpg",
        "assets/textures/skybox/back.jpg",
        "assets/textures/skybox/front.jpg")

    val groundRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/level/level.obj")
    val groundMeshList : MutableList<OBJLoader.OBJMesh> = groundRes.objects[0].meshes
    val enemyRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/Among Us/among us.obj")
    val enemyMeshList : MutableList<OBJLoader.OBJMesh> = enemyRes.objects[0].meshes
    val skyboxRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/skybox.obj")
    val skyboxMeshList : MutableList<OBJLoader.OBJMesh> = skyboxRes.objects[0].meshes

    private val objects : MutableList<Renderable?> = ArrayList()
    private val enemys : MutableList<Renderable?> = ArrayList()
    private var enemyStatsHp: Int = 0
    private var enemyStatsSpeed: Float = 0f
    private var enemyCount: Int = 12
    private var camState = 0;
    private var pressOk : Boolean = true;
    private var pressSpace : Boolean = true;
    private var invinFrame : Boolean = false;
    private var invinFrameBuffer : Boolean = false;
    private var tempT : Float = 0f;
    private var x : Int = 0

    private var firstTime : Boolean = true

    private var rotationState : Int = 0



    val sphereRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/sphere.obj")
    val sphereMeshList : MutableList<OBJLoader.OBJMesh> = sphereRes.objects[0].meshes

    private var player: Renderable? = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", 0f, 0f, 0f)
    private var enemy00: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy01: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy02: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy03: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy04: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy05: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy06: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy07: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy08: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy09: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy10: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var enemy11: Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)

    private var skyboy: Renderable? = ModelLoader.loadModel("assets/models/skybox.obj", 0f, 0f, 0f)

    private var bulletTest: Renderable? = ModelLoader.loadModel("assets/Boxing Glove/bxglvsp(right).obj",0f,0f,0f)
    private var groundDiff : Texture2D = Texture2D.invoke("assets/textures/ground_diff.png", true)
    private var groundEmit :Texture2D = Texture2D.invoke("assets/textures/ground_emit.png", true)
    private var groundSpec :Texture2D = Texture2D.invoke("assets/textures/ground_spec.png", true)

    private var enemyDiff: Texture2D = Texture2D.invoke("assets/Among Us/Plastic_4K_Diffuse.jpg", true)
    private var enemyEmit :Texture2D = Texture2D.invoke("assets/Among Us/Plastic_4K_Normal.jpg", true)
    private var enemySpec :Texture2D = Texture2D.invoke("assets/Among Us/Plastic_4K_Reflect.jpg", true)
    private var groundMaterial = Material(groundDiff, groundEmit, groundSpec, 240f, Vector2f(64f, 64f))
    private var enemyMaterial = Material(enemyDiff, enemyEmit, enemySpec, 240f, Vector2f(64f, 64f))

    private var ground : Renderable = ModelLoader.loadModel("assets/level/level.obj", 0f, 0f, 0f)!!
//    private var skybox : Renderable
//    private var sphere: Renderable
    //private var sphere : Renderable

    private var xPosition : Double
    private var yPosition : Double

    //scene setup
    init {

        xPosition  = 0.0
        yPosition  = 0.0




        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl", "assets/shaders/geometry.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl", "assets/shaders/geometry_skybox.glsl")
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl", "assets/shaders/geometry.glsl")
        ssaoGeoShader = ShaderProgram("assets/shaders/ssao_geovert.glsl", "assets/shaders/ssao_geofrag.glsl", "assets/shaders/geometry.glsl")
        shadowShader = ShaderProgram("assets/shaders/shadow_vert.glsl", "assets/shaders/shadow_frag.glsl", "assets/shaders/shadow_geometry.glsl")

        shaderList.add(staticShader)
        shaderList.add(toonShader)
        shaderList.add(staticShader)
        shaderList.add(shadowShader)


        //initial opengl state
        glClearColor (0.0f , 0.0f , 0.0f , 1.0f); GLError . checkThrow ()

        //glClearColor(0.6f, 1.0f, 1.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()

        val strideG = 8 * 4
        val attrPosG =  VertexAttribute(3, GL_FLOAT, strideG, 0) //position
        val attrTCG = VertexAttribute(2, GL_FLOAT, strideG, 12) //textureCoordinate
        val attrNormG = VertexAttribute(3, GL_FLOAT, strideG, 20) //normalval
        val vertexAttributesG = arrayOf<VertexAttribute>(attrPosG, attrTCG, attrNormG)

        val strideE = 8 * 4
        val attrPosE =  VertexAttribute(3, GL_FLOAT, strideG, 0) //position
        val attrTCE = VertexAttribute(3, GL_FLOAT, strideG, 12) //textureCoordinate
        val attrNormE = VertexAttribute(3, GL_FLOAT, strideG, 24) //normalval
        val vertexAttributesE = arrayOf<VertexAttribute>(attrPosE, attrTCE, attrNormE)


        //Cubemap-Test // TexUnit Skyboxes ab 10
        loadCube(skyboxShader, skyboxList, 10)

        groundEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        groundSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)

        enemyEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
        enemyDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        enemySpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
//        meshG = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttributesG, groundMaterial)
//        meshE = Mesh(enemyMeshList[0].vertexData, enemyMeshList[0].indexData, vertexAttributesG, enemyMaterial)
//        meshListE.add(meshE)
//        meshListG.add(meshG)
//        ground  = Renderable(meshListG, hp = 30000000)
//        enemy00 = Renderable(meshListE, hp = 0)

        meshSkybox = Mesh(skyboxMeshList[0].vertexData, skyboxMeshList[0].indexData, vertexAttributesG)
        meshListSkybox.add(meshSkybox)

        meshS = Mesh(sphereMeshList[0].vertexData, sphereMeshList[0].indexData, vertexAttributesG)
        meshListS.add(meshS)


//        skybox = Renderable(meshListSkybox, null, 30000)
//        skybox.scale(Vector3f(30f))
        skyboy?.scale(Vector3f(30f))

        player?.scale((Vector3f(0.8f)))
        player?.hp = 10
        player?.hitbox = 1f

        bulletTest?.scale(Vector3f(0.2f))
        bulletTest?.parent = player
        bulletTest?.translate(Vector3f(0f,1f,-9f))
        bulletTest?.hitbox
        objects.add(player)
        objects.add(bulletTest)
        enemys.add(enemy00)
        enemys.add(enemy01)
        enemys.add(enemy02)
        enemys.add(enemy03)
        enemys.add(enemy04)
        enemys.add(enemy05)
        enemys.add(enemy06)
        enemys.add(enemy07)
        enemys.add(enemy08)
        enemys.add(enemy09)
        enemys.add(enemy10)
        enemys.add(enemy11)

        cam = TronCamera( _parent = player)
        cam1 = TronCamera( _parent = player)
        cam2 = TronCamera( _parent = player)
        cam3 = TronCamera()

        cam.rotate(Math.toRadians(-15f), 0f, 0f)
        cam.translate(Vector3f(0f, 2f, 0f))
        cam1.rotate(Math.toRadians(-15f), 0f, 0f)
        cam1.translate(Vector3f(0f, 2f, 4f))
        cam2.rotate(Math.toRadians(-15f), 0f, 0f)
        cam2.translate(Vector3f(0f, 2f, 7f))
        cam3.rotate(Math.toRadians(270f), 0f, 0f)
        cam3.translate(Vector3f(0f, 0f, 20f))

        pointLight = PointLight(Vector3f(-50f ,0f, -50f), Vector3f(0f, 0f, 0f))
        pointLight1 = PointLight(Vector3f(50f ,0f, -50f), Vector3f(0f, 0f, 0f))
        pointLight2 = PointLight(Vector3f(50f ,0f, 50f), Vector3f(0f, 0f, 0f))
        pointLight3 = PointLight(Vector3f(-50f ,0f, 50f), Vector3f(0f, 0f, 0f))
        pointLight4 = PointLight(Vector3f(0f ,20f, 0f), Vector3f(0.5f, 0.33f, 0.25f))
        pointList.add(pointLight4)
//        pointList.add(pointLight)
//        pointList.add(pointLight1)
//        pointList.add(pointLight2)
//        pointList.add(pointLight3)
        spotLight = SpotLight(Vector3f(0f, 1f, -0.5f), Vector3f(1f, 0f, 0f), 20f, 10f, _parent = player)
        spotLight.rotate(Math.toRadians(-20f), 0f, 0f)

        // SSAO G-Buffer zeug

//        for (i in 64 downTo  0){
//
//            var sample  = Vector3f(Random.nextFloat() * 2.0f - 1.0f, Random.nextFloat() * 2.0f - 1.0f, Random.nextFloat())
//            sample = sample.normalize()
//            sample = sample.mul(Random.nextFloat())
//            var scale : Float = i.toFloat() / 64f
//            scale = lerp(0.1f, 1.0f, scale * scale)
//            sample = sample.mul(scale)
//            ssaoGeoShader.setUniformVec3("samples[" + i + "]", sample)
//        }
//
//        var ssaoNoise : MutableList<Vector3f> = arrayListOf()
//        ssaoNoise.add(Vector3f(0f))
//        ssaoNoise.add(Vector3f(0f))
//        for ( i in 16 downTo 0){
//                    ssaoNoise.add(Vector3f(Random.nextFloat() * 2 - 1, Random.nextFloat() * 2 - 1, 0f))
//            }
//
//        var noiseTexture = glGenTextures()
//        GL11.glBindTexture(GL_TEXTURE_2D, noiseTexture)
//        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, 4, 4, 0, GL_RGB, GL_FLOAT, ssaoVec)
//
//        val gBuffer = glGenFramebuffers()
//        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer)
//
//        val gPosition = glGenTextures()
//        glBindTexture(GL_TEXTURE_2D, gPosition);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.framebufferWidth, window.framebufferHeight, 0, GL_RGBA, GL_FLOAT, NULL);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//
//        val gNormal = glGenTextures()
//        glBindTexture(GL_TEXTURE_2D, gNormal);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.framebufferWidth, window.framebufferHeight, 0, GL_RGBA, GL_FLOAT, NULL);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0);
//
//        val gAlbedoSpec  = glGenTextures()
//        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, window.framebufferWidth, window.framebufferHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedoSpec, 0);
//
//        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        // Shadows

        depthMapFBO = glGenFramebuffers() ;  SHADOW_WIDTH = 1024;  SHADOW_HEIGHT = 1024
        lightProjection = Matrix4f().ortho(-10f, 10f, -10f, 10f, 1f, 7.5f)
        lightView = Matrix4f().lookAt(Vector3f(-2f, 4f, -1f),
                                        Vector3f(0f, 0f, 0f),
                                        Vector3f(0f, 1f, 0f))

        lightSpaceMatrix = lightProjection.mul(lightView)

         depthCubeMap = glGenTextures()
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubeMap)
        for (i in 5 downTo 0){
            GL11.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + 1, 0, GL_DEPTH_COMPONENT,
                                1024, 1024, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL)
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        GL13.glActiveTexture(11)
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubeMap)
        toonShader.setUniformInt("depthMap", 11)

        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO)

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthCubeMap, 0)
        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)

        val aspect = SHADOW_WIDTH.toFloat() / SHADOW_HEIGHT.toFloat()
        val near = 1f
        val far = 25f
        ShadowProj = Matrix4f().perspective(Math.toRadians(90f), aspect, near, far)

        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(1f, 0f,0f)),
                                                            Vector3f(0f, -1f, 0f))))
        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(-1f, 0f,0f)),
            Vector3f(0f, -1f, 0f))))
        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(0f, 1f,0f)),
            Vector3f(0f, 0f, 1f))))
        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(0f, -1f,0f)),
            Vector3f(0f, 0f, -1f))))
        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(0f, 0f,1f)),
            Vector3f(0f, -1f, 0f))))
        shadowTransform.add(ShadowProj.mul(Matrix4f().lookAt(pointLight4.lightPos, pointLight4.lightPos.add(Vector3f(0f, 0f,-1f)),
            Vector3f(0f, -1f, 0f))))


        val Status = glCheckFramebufferStatus(GL_FRAMEBUFFER)

        if (Status !== GL_FRAMEBUFFER_COMPLETE) {
            println("FB error, status: 0x%x\n" + Status)

        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }



    fun lerp(a: Float, b: Float, c: Float): Float{

        return a + c * (b - a)
    }
    fun loadCube(shaderProgram: ShaderProgram, faces: MutableList<String>, texUnit : Int){

        val cubeID = glGenTextures()
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, cubeID)

        val a = BufferUtils.createIntBuffer(6)
        val b = BufferUtils.createIntBuffer(6)
        val channels = BufferUtils.createIntBuffer(6)
        var data: ByteBuffer?
        var i = 0
        STBImage.stbi_set_flip_vertically_on_load(false)

        faces.forEach{
            data = STBImage.stbi_load(it, a, b, channels, 4)
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, a.get(), b.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
            STBImage.stbi_image_free(data)
            i++
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        glActiveTexture(texUnit)
        shaderProgram.setUniformInt("sky", texUnit)

    }

    fun hitboxCalc(renderable: Renderable?) : Boolean{
        objects.forEach {
            if (renderable != null) {
                if (it != null) {
                    if(renderable != it){
                        if(!(renderable == bulletTest && it == player)||(renderable == player && it == bulletTest) ) {
                            if (renderable.getWorldPosition().x + renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x + renderable.hitbox >= it.getWorldPosition().x - it.hitbox || renderable.getWorldPosition().x - renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x - renderable.hitbox >= it.getWorldPosition().x - it.hitbox) {
                                if (renderable.getWorldPosition().y + renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y + renderable.hitbox >= it.getWorldPosition().y - it.hitbox || renderable.getWorldPosition().y - renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y - renderable.hitbox >= it.getWorldPosition().y - it.hitbox) {
                                    if (renderable.getWorldPosition().z + renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z + renderable.hitbox >= it.getWorldPosition().z - it.hitbox || renderable.getWorldPosition().z - renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z - renderable.hitbox >= it.getWorldPosition().z - it.hitbox) {
                                        println("Kollision")
                                        if (renderable == bulletTest) {
                                            it.hp--
                                            println(it.hp)
                                            if(it.hp < 1){
                                                deleteEnemy(it)
                                            }
                                        }
                                        if(renderable != player){
                                            if(it == player){
                                                if(invinFrame == false){
                                                    player?.hp = player?.hp?.minus(1)!!
                                                    println(player?.hp!!)
                                                    invinFrame = true
                                                    invinFrameBuffer = true
                                                }
                                            }
                                        }
                                        return false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun deleteEnemy(renderable: Renderable?){
        objects.remove(renderable)
        renderable?.scale(Vector3f(0.00001f))
        enemyCount = enemyCount - 1
        if(enemyCount < 1){
            enemyStatsHp = enemyStatsHp + 1
            enemyStatsSpeed = enemyStatsSpeed + 0.01f
            spawnEnemys()
        }
    }

    fun spawnEnemys(){
        println("respawn eingeleitet")


        enemys.forEach {
            it?.scale(Vector3f(100000f))
            it?.hitbox = 1f
            spawnRandom(it)
            objects.add(it)
            it?.hp = 3 + enemyStatsHp

        }
        enemyCount = 12

    }

    fun spawnRandom(renderable: Renderable?){
        renderable?.translate(Vector3f((-1500..1500).random().toFloat(), 0f,(-1500..1500).random().toFloat() ))
        if(hitboxCalc(renderable) == false){
            spawnRandom(renderable)
        }

    }

    fun enemyWalk(renderable: Renderable?){



        if(player?.getWorldPosition()!!.x > renderable?.getWorldPosition()!!.x)
        {


            renderable.translate(Vector3f( 2f + enemyStatsSpeed, 0f, 0f))
            if(hitboxCalc(renderable) == false){
                renderable.translate(Vector3f(- 2f - enemyStatsSpeed, 0f, 0f))
            }
        }

        if(player?.getWorldPosition()!!.x < renderable.getWorldPosition()!!.x)
        {



            renderable.translate(Vector3f(- 2f - enemyStatsSpeed, 0f, 0f))
            if(hitboxCalc(renderable) == false){
                renderable.translate(Vector3f(2f + enemyStatsSpeed, 0f, 0f))
            }
        }

        if(player?.getWorldPosition()!!.z > renderable.getWorldPosition()!!.z)
        {


            renderable.translate(Vector3f(0f, 0f, 2f + enemyStatsSpeed))
            if(hitboxCalc(renderable) == false){
                renderable.translate(Vector3f(0f, 0f, - 2f - enemyStatsSpeed))
            }
        }

        if(player?.getWorldPosition()!!.z < renderable.getWorldPosition()!!.z)
        {

            renderable.translate(Vector3f(0f, 0f, - 2f - enemyStatsSpeed))
            if(hitboxCalc(renderable) == false){
                renderable.translate(Vector3f(0f, 0f, 2f + enemyStatsSpeed))
            }
        }
    }

    fun render(dt: Float, t: Float) {

//        GL11.glViewport(0, 0 , SHADOW_WIDTH, SHADOW_HEIGHT)
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, depthMapFBO)
//        glClear(GL_DEPTH_BUFFER_BIT)
//        shadowShader.use()
//        pointLight4.bindList(shadowShader, cam.getCalculateViewMatrix(), 0)
//        shadowShader.setUniformFloat("far_plane", 25f)
//        for(i in 5 downTo  0) {
//            shadowShader.setUniformMat("shadowMatrices[" + i + "]", shadowTransform[i], false)
//        }
//        ground.render(shadowShader)
//        player?.render(shadowShader)
//        enemys.forEach { it?.render(shadowShader) }
//
//        glBindFramebuffer(GL_FRAMEBUFFER, 0)
//        glViewport(0, 0, window.windowWidth, window.windowHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        toonShader.use()
        toonShader.setUniformFloat("far_plane", 25f)
        GL13.glActiveTexture(11)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubeMap)

        cam.bind(toonShader)

        var i = 0
        spotLight.bind(toonShader, cam.getCalculateViewMatrix())
        pointList.forEach{
            //it.bindList(staticShader, cam.getCalculateViewMatrix(), i)
            it.bindList(toonShader, cam.getCalculateViewMatrix(), i)
            i++
        }

        if(camState == 0) {cam.bind(toonShader)}
        if(camState == 1) {cam1.bind(toonShader)}
        if(camState == 2) {cam2.bind(toonShader)}
        if(camState == 3) {cam3.bind(toonShader)}

        if(firstTime == true){

            enemys.forEach {
                it?.scale(Vector3f(0.01f))
                it?.hitbox = 1f
                spawnRandom(it)
                objects.add(it)
                it?.hp = 3 + enemyStatsHp
            }

            firstTime = false



        }

        enemys.forEach { it?.render(toonShader) }

        player?.render(toonShader)
        ground.render(toonShader)

        bulletTest?.render(toonShader)

        //Skybox render
        GL11.glDepthMask(false)
        glDisable(GL_CULL_FACE)
        skyboxShader.use()
        skyboxShader.setUniformMat("view_sky", Matrix4f(Matrix3f(cam.getCalculateViewMatrix())), false)
        cam.bind(skyboxShader)
        skyboy?.render(skyboxShader)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        glEnable(GL_CULL_FACE)
        glDepthMask(true)

    }

    fun update(dt: Float, t: Float) {
        if(invinFrame == true && invinFrameBuffer == true){
            tempT = t + 2
            invinFrameBuffer = false
        }

        if(t >= tempT){
            invinFrame = false
        }

        if(player?.hp!! <= 0){
            window.quit()
        }

        enemys.forEach { enemyWalk(it) }

        enemys.forEach {
            var xxx : Vector3f = (player?.getWorldPosition()!!.min(it?.getWorldPosition()) )
            xxx.normalize()
            var yyy : Vector3f = Vector3f(0f,0f,1f).cross(xxx)
            yyy.normalize()
            var zzz : Vector3f = xxx.cross(yyy)

            var chasermat : Matrix4f = Matrix4f()
            chasermat.setRow(0,Vector4f(0f, xxx.x, xxx.y, xxx.z))
            chasermat.setRow(1,Vector4f(1f, yyy.x, yyy.y, yyy.z))
            chasermat.setRow(2,Vector4f(2f, zzz.x, zzz.y, zzz.z))
            chasermat.setRow(3 ,Vector4f(3f, player?.getWorldPosition()!!.x, player?.getWorldPosition()!!.y, player?.getWorldPosition()!!.z))



        }

        if (window.getKeyState(GLFW.GLFW_KEY_W)){player?.translate(Vector3f(0f, 0f, - 0.1f))

            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(0f, 0f,  0.1f))

                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
                println(player?.hp!!)
                println(t)
                println(tempT)

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_S)){player?.translate(Vector3f(0f, 0f,  0.05f))

            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(0f, 0f,  -0.05f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_D)){player?.translate(Vector3f(0.05f, 0f,  0f))

            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(-0.05f, 0f,  0f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_A)){player?.translate(Vector3f(-0.05f, 0f,  0f))

            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(0.05f, 0f,  0f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
            }}

        if(window.getKeyState(GLFW.GLFW_KEY_SPACE) && pressSpace == true){
            pressSpace = false


            while (x < 25000) {
                bulletTest?.translate(Vector3f(0f, 0f, - 0.002f))
                if(hitboxCalc(bulletTest) == false){
                    return
                }
                x++
                println(x)


            }
        }

        if(!window.getKeyState(GLFW.GLFW_KEY_SPACE)){
            pressSpace = true
                bulletTest?.translate(Vector3f(0f, 0f, x * 0.002f))
                x = 0
        }

        if(camState == 3){
            if (window.getKeyState(GLFW.GLFW_KEY_DOWN)){cam3.translate(Vector3f(0f, -dt * 20, 0f)) }
            if (window.getKeyState(GLFW.GLFW_KEY_UP)){cam3.translate(Vector3f(0f, dt * 20, 0f)) }
            if (window.getKeyState(GLFW.GLFW_KEY_LEFT)){cam3.translate(Vector3f(-dt * 20f, 0f, 0f))}
            if (window.getKeyState(GLFW.GLFW_KEY_RIGHT)){cam3.translate(Vector3f(dt * 20f, 0f, 0f))}
        }

        if(window.getKeyState(GLFW.GLFW_KEY_PAGE_DOWN) && pressOk == true){

            pressOk = false
            if(camState != 3){

                camState = camState + 1
            }

        }

        if(!window.getKeyState(GLFW.GLFW_KEY_PAGE_DOWN) && !window.getKeyState(GLFW.GLFW_KEY_PAGE_UP)){pressOk = true}

        if(window.getKeyState(GLFW.GLFW_KEY_PAGE_UP)&& pressOk == true){
            pressOk = false
            if(camState !=0){

                camState = camState - 1
            }

        }

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {

        if(camState != 3) {
            var dxpos: Double = xPosition - xpos

            player!!.rotate(0.0f, (dxpos.toFloat() * 0.002f), 0f)

            xPosition = xpos
            yPosition = ypos
        }

        if(camState == 3) {
            var dxpos: Double = xPosition - xpos
            cam3.rotateAroundPoint(0.0f, (dxpos.toFloat() * 0.002f), 0f, player!!.getYAxis())
            xPosition = xpos
            yPosition = ypos
        }

    }

    fun cleanup() {
        staticShader.cleanup() // selbst erstellt
    }
}
