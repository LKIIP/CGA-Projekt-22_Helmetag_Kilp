package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.CubeMap
//import cga.exercise.components.texture.Skybox
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.ARBFramebufferObject.*
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL32.glFramebufferTexture
import org.lwjgl.system.MemoryUtil.NULL
import java.util.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private var depthCubeMap :Int
    private val staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val toonShader: ShaderProgram
    private val shadowShader: ShaderProgram
    private val shaderList: MutableList<ShaderProgram> = arrayListOf()
    private val shadowTransform : MutableList<Matrix4f> = arrayListOf()
    private val levelList: MutableList<Renderable?> = arrayListOf()

    private var depthMap : Int =-1
    private var depthMapFBO : Int = -1
    private val SHADOW_WIDTH :Int
    private val SHADOW_HEIGHT: Int
    private val lightProjection : Matrix4f
    private val lightView : Matrix4f
    private val lightSpaceMatrix : Matrix4f
    private val ShadowProj: Matrix4f

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
    private val skyboxList: MutableList<String> = arrayListOf("assets/textures/skybox/left.png",
        "assets/textures/skybox/right.png",
        "assets/textures/skybox/top.png",
        "assets/textures/skybox/bottom.png",
        "assets/textures/skybox/back.png",
        "assets/textures/skybox/front.png")

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
    private var doNothing : Boolean = false;
    private var tempT : Float = 0f;
    private var x : Int = 0

    private var shaderChange : Boolean = true

    private var firstTime : Boolean = true

    private var standardShader : ShaderProgram? = null


    private var player: Renderable? = ModelLoader.loadModel("assets/Among Us/player.obj", 0f, 0f, 0f)
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



    private var boxingGlove: Renderable? = ModelLoader.loadModel("assets/Boxing Glove/bxglvsp(right).obj",0f,0f,0f)


    private var wall0 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall1 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall2 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall3 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)

    private var wall4 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall5 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall6 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)
    private var wall7 : Renderable? = ModelLoader.loadModel("assets/Among Us/among us.obj", 0f, 0f, 0f)

    private var stone : Renderable? = ModelLoader.loadModel("assets/models/Level/boulder.obj", 0f, 0f, 0f)
    private var tree00 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_raute1.obj", 0f, 0f, 0f)
    private var tree01 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_raute2.obj", 0f, 0f, 0f)
    private var tree02 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_round1.obj", 0f, 0f, 0f)
    private var tree03 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_round2.obj", 0f, 0f, 0f)
    private var tree04 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_square1.obj", 0f, 0f, 0f)
    private var tree05 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_square2.obj", 0f, 0f, 0f)
    private var tree06 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_square3.obj", 0f, 0f, 0f)
    private var tree07 : Renderable? = ModelLoader.loadModel("assets/models/Level/tree_square4.obj", 0f, 0f, 0f)
    private var fence: Renderable? = ModelLoader.loadModel("assets/models/Level/fences.obj", 0f, 0f, 0f)
    private var ground : Renderable? = ModelLoader.loadModel("assets/models/Level/ground.obj", 0f, 0f, 0f)!!

    private var skyboy: Renderable? = ModelLoader.loadModel("assets/models/skybox.obj", 0f, 0f, 0f)
    private var skyboxtex :CubeMap = CubeMap.invoke(skyboxList, true)

    private var xPosition : Double
    private var yPosition : Double

    //scene setup
    init {

        xPosition  = 0.0
        yPosition  = 0.0

        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl", "assets/shaders/geometry.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl", null)
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl", "assets/shaders/geometry.glsl")
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


        //Cubemap-Test // TexUnit Skyboxes ab 10
       skyboxtex.setTexParamsCube( GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_LINEAR, GL_LINEAR)

        skyboy?.scale(Vector3f(30f))

        player?.translate(Vector3f(5f, 0f, 5f))
        player?.hp = 10
        player?.hitbox = 1f

        boxingGlove?.scale(Vector3f(0.2f))
        boxingGlove?.parent = player
        boxingGlove?.translate(Vector3f(0f,1f,-9f))
        boxingGlove?.hitbox

        objects.add(player)
        objects.add(boxingGlove)
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

        pointLight = PointLight(Vector3f(-10f ,2f, -10f), Vector3f(1f, 0f, 0f))
        pointLight1 = PointLight(Vector3f(10f ,2f, -10f), Vector3f(0f, 1f, 0f))
        pointLight2 = PointLight(Vector3f(10f ,2f, 10f), Vector3f(0f, 0f, 1f))
        pointLight3 = PointLight(Vector3f(-10f ,2f, 10f), Vector3f(1f, 0f, 1f))
        pointLight4 = PointLight(Vector3f(0f ,10f, 0f), Vector3f(1f, 1f, 1f))
        pointList.add(pointLight4)
        pointList.add(pointLight)
        pointList.add(pointLight1)
        pointList.add(pointLight2)
        pointList.add(pointLight3)
        spotLight = SpotLight(Vector3f(0f, 1f, -0.5f), Vector3f(0.8f, 0.8f, 0.8f), 20f, 10f, _parent = player)
        spotLight.rotate(Math.toRadians(-20f), 0f, 0f)

        wall0?.translate(Vector3f(40f, 0f, 0f))
        wall1?.translate(Vector3f(-40f, 0f, 0f))
        wall2?.translate(Vector3f(0f, 0f, 40f))
        wall3?.translate(Vector3f(0f, 0f, -40f))
        wall0?.hitbox = 20f
        wall1?.hitbox = 20f
        wall2?.hitbox = 20f
        wall3?.hitbox = 20f

        wall4?.translate(Vector3f(40f, 0f, 40f))
        wall5?.translate(Vector3f(-40f, 0f, 40f))
        wall6?.translate(Vector3f(40f, 0f, -40f))
        wall7?.translate(Vector3f(-40f, 0f, -40f))
        wall4?.hitbox = 20f
        wall5?.hitbox = 20f
        wall6?.hitbox = 20f
        wall7?.hitbox = 20f

        objects.add(wall0)
        objects.add(wall1)
        objects.add(wall2)
        objects.add(wall3)
        objects.add(wall4)
        objects.add(wall5)
        objects.add(wall6)
        objects.add(wall7)



        stone?.translate(Vector3f(-2.5f, 0f, 10f))
        stone?.hitbox = 2f

        objects.add(stone)

        tree00?.translate(Vector3f(-10f,0f,-5f))
        tree00?.hitbox = 1f

        tree01?.translate(Vector3f(-6f,0f,-8f))
        tree01?.hitbox = 1f

        tree02?.translate(Vector3f(3.3f,0f,-9f))
        tree02?.hitbox = 1f

        tree03?.translate(Vector3f(7f,0f,-1.3f))
        tree03?.hitbox = 1f

        tree04?.translate(Vector3f(9f,0f,-8.5f))
        tree04?.hitbox = 1f

        tree05?.translate(Vector3f(12f,0f,12f))
        tree05?.hitbox = 1f

        tree06?.translate(Vector3f(-8f,0f,6f))
        tree06?.hitbox = 1f

        tree07?.translate(Vector3f(-9.7f,0f,1.3f))
        tree07?.hitbox = 1f


        objects.add(tree00)
        objects.add(tree01)
        objects.add(tree02)
        objects.add(tree03)
        objects.add(tree04)
        objects.add(tree05)
        objects.add(tree06)
        objects.add(tree07)


        levelList.add(tree00)
        levelList.add(tree01)
        levelList.add(tree02)
        levelList.add(tree03)
        levelList.add(tree04)
        levelList.add(tree05)
        levelList.add(tree06)
        levelList.add(tree07)
        levelList.add(fence)
        levelList.add(stone)
        levelList.add(ground)



//      Shadows

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
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }



    fun lerp(a: Float, b: Float, c: Float): Float{

        return a + c * (b - a)
    }

    fun hitboxCalc(renderable: Renderable?) : Boolean{
        objects.forEach {
            if (renderable != null) {
                if (it != null) {
                    if(renderable != it){
                        if(!(renderable == boxingGlove && it == player)||(renderable == player && it == boxingGlove) ) {
                            if (renderable.getWorldPosition().x + renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x + renderable.hitbox >= it.getWorldPosition().x - it.hitbox || renderable.getWorldPosition().x - renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x - renderable.hitbox >= it.getWorldPosition().x - it.hitbox) {
                                if (renderable.getWorldPosition().y + renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y + renderable.hitbox >= it.getWorldPosition().y - it.hitbox || renderable.getWorldPosition().y - renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y - renderable.hitbox >= it.getWorldPosition().y - it.hitbox) {
                                    if (renderable.getWorldPosition().z + renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z + renderable.hitbox >= it.getWorldPosition().z - it.hitbox || renderable.getWorldPosition().z - renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z - renderable.hitbox >= it.getWorldPosition().z - it.hitbox) {
                                        if (renderable == boxingGlove) {
                                            it.hp--
                                            println(it.hp)
                                            if(it.hp < 1){
                                                deleteEnemy(it)
                                            }
                                        }
                                        if(renderable != player && renderable.isEnemy){
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

    fun hitboxCalcPlayer(renderable: Renderable?) : Boolean{
        objects.forEach {
            if (renderable != null) {
                if (it != null) {
                    if(renderable != it){
                        if(!(renderable == boxingGlove && it == player)||(renderable == player && it == boxingGlove) ) {
                            if (renderable.getWorldPosition().x + renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x + renderable.hitbox >= it.getWorldPosition().x - it.hitbox || renderable.getWorldPosition().x - renderable.hitbox <= it.getWorldPosition().x + it.hitbox && renderable.getWorldPosition().x - renderable.hitbox >= it.getWorldPosition().x - it.hitbox) {
                                if (renderable.getWorldPosition().y + renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y + renderable.hitbox >= it.getWorldPosition().y - it.hitbox || renderable.getWorldPosition().y - renderable.hitbox <= it.getWorldPosition().y + it.hitbox && renderable.getWorldPosition().y - renderable.hitbox >= it.getWorldPosition().y - it.hitbox) {
                                    if (renderable.getWorldPosition().z + renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z + renderable.hitbox >= it.getWorldPosition().z - it.hitbox || renderable.getWorldPosition().z - renderable.hitbox <= it.getWorldPosition().z + it.hitbox && renderable.getWorldPosition().z - renderable.hitbox >= it.getWorldPosition().z - it.hitbox) {
                                        if(renderable == player && it.isEnemy == true){
                                            return false
                                        }

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
        renderable?.setWorldPosition(Vector3f(0f,0f,0f))
        renderable?.scale(Vector3f(0.00001f))
        enemyCount = enemyCount - 1
        if(enemyCount < 1){
            enemyStatsHp = enemyStatsHp + 1
            enemyStatsSpeed = enemyStatsSpeed + 0.01f
            shaderChange = !shaderChange
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
        renderable?.translate(Vector3f((-1000..1000).random().toFloat(), 0f,(-1000..1000).random().toFloat() ))
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

       // Shadow RenderPass

        GL11.glViewport(0, 0 , SHADOW_WIDTH, SHADOW_HEIGHT)
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, depthMapFBO)
        glClear(GL_DEPTH_BUFFER_BIT)
        shadowShader.use()
        pointLight4.bindList(shadowShader, cam.getCalculateViewMatrix(), 0)
        shadowShader.setUniformFloat("far_plane", 25f)
        for(i in 5 downTo  0) {
            shadowShader.setUniformMat("shadowMatrices[" + i + "]", shadowTransform[i], false)
        }
        GL13.glActiveTexture(11)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, depthCubeMap)

        glBindFramebuffer(GL_FRAMEBUFFER, 0)


        // Normal Renderpass

        glViewport(0, 0, window.windowWidth, window.windowHeight)
        if(shaderChange){
            standardShader = toonShader
        }else{
            standardShader = staticShader
        }

        glViewport(0, 0, window.windowWidth, window.windowHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        standardShader!!.use()
        standardShader!!.setUniformFloat("far_plane", 25f)
          cam.bind(standardShader!!)

          var i = 0
          spotLight.bind(standardShader!!, cam.getCalculateViewMatrix())
          pointList.forEach{
              //it.bindList(staticShader, cam.getCalculateViewMatrix(), i)
              it.bindList(standardShader!!, cam.getCalculateViewMatrix(), i)
              i++
          }

          if(camState == 0) {cam.bind(standardShader!!)}
          if(camState == 1) {cam1.bind(standardShader!!)}
          if(camState == 2) {cam2.bind(standardShader!!)}
          if(camState == 3) {cam3.bind(standardShader!!)}

          if(firstTime == true){

              enemys.forEach {
                  it?.isEnemy = true
                  it?.scale(Vector3f(0.01f))
                  it?.hitbox = 1f
                  spawnRandom(it)
                  objects.add(it)
                  it?.hp = 3 + enemyStatsHp
              }

              firstTime = false

        }

        enemys.forEach { it?.render(standardShader!!) }
        levelList.forEach { it?.render(standardShader!!) }
        player?.render(standardShader!!)

        boxingGlove?.render(standardShader!!)






        //Skybox render
        glDepthFunc(GL_LEQUAL)
        glDisable(GL_CULL_FACE)
        skyboxShader.use()
        cam.bind(skyboxShader)
        skyboxShader.setUniformMat("view_sky", Matrix4f(Matrix3f(cam.getCalculateViewMatrix())), false)
         skyboxtex.bind(10)
        skyboxShader.setUniformInt("sky", 10)
        skyboy?.render(skyboxShader)
        skyboxtex.unbind()
        glEnable(GL_CULL_FACE)
        GL11.glDepthFunc(GL_LESS)
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

        if(doNothing == false) {
            enemys.forEach { enemyWalk(it) }
        }



        if (window.getKeyState(GLFW.GLFW_KEY_W)){player?.translate(Vector3f(0f, 0f, - 0.1f))

            if(hitboxCalcPlayer(player) == false){
                player?.translate(Vector3f(0f, 0f,  0.1f))

                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
                println(player?.hp!!)
                println(t)
                println(tempT)

            }
            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(0f, 0f,  0.1f))
            }
        }

        if (window.getKeyState(GLFW.GLFW_KEY_S)){player?.translate(Vector3f(0f, 0f,  0.05f))

            if(hitboxCalcPlayer(player) == false){
                player?.translate(Vector3f(0f, 0f,  -0.05f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }
            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(0f, 0f,  -0.05f))
            }
        }

        if (window.getKeyState(GLFW.GLFW_KEY_D)){player?.translate(Vector3f(0.05f, 0f,  0f))

            if(hitboxCalcPlayer(player) == false){
                player?.translate(Vector3f(-0.05f, 0f,  0f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }
            if(hitboxCalc(player) == false){
                player?.translate(Vector3f(-0.05f, 0f,  0f))
            }
        }

        if (window.getKeyState(GLFW.GLFW_KEY_A)){player?.translate(Vector3f(-0.05f, 0f,  0f))

            if(hitboxCalcPlayer(player) == false){
                player?.translate(Vector3f(0.05f, 0f,  0f))
                if(invinFrame == false){
                    player?.hp = player?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
            }
        if(hitboxCalc(player) == false){
            player?.translate(Vector3f(0.05f, 0f,  0f))
        }

        }


        if(window.getKeyState(GLFW.GLFW_KEY_SPACE) && pressSpace == true){
            pressSpace = false



            while (x < 25000) {
                boxingGlove?.translate(Vector3f(0f, 0f, - 0.002f))
                if(hitboxCalc(boxingGlove) == false){
                    return
                }
                x++
                println(x)


            }
        }

        if(!window.getKeyState(GLFW.GLFW_KEY_SPACE)){
            pressSpace = true


                boxingGlove?.translate(Vector3f(0f, 0f, x * 0.002f))
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

        if(window.getKeyState(GLFW.GLFW_KEY_P)){
            doNothing = true
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
