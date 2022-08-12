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
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.*
import org.lwjgl.stb.STBImage
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import java.nio.ByteBuffer
import java.util.*
import kotlin.random.Random


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val toonShader: ShaderProgram

    private val meshG: Mesh
    private val meshSkybox: Mesh
    private val meshS : Mesh
    private val meshListS : MutableList<Mesh> = arrayListOf()
    private val meshListG : MutableList<Mesh> = arrayListOf()
    private val meshListSkybox : MutableList<Mesh> = arrayListOf()
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

    val groundRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    val groundMeshList : MutableList<OBJLoader.OBJMesh> = groundRes.objects[0].meshes
    val skyboxRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/skybox.obj")
    val skyboxMeshList : MutableList<OBJLoader.OBJMesh> = skyboxRes.objects[0].meshes

    private val objects : MutableList<Renderable?> = ArrayList()
    private var enemyCount: Int = 10
    private var enemyCountMax: Int = 10
    private var camState = 0;
    private var pressOk : Boolean = true;
    private var pressSpace : Boolean = true;
    private var invinFrame : Boolean = false;
    private var invinFrameBuffer : Boolean = false;
    private var tempT : Float = 0f;
    private var x : Int = 0



    val sphereRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/sphere.obj")
    val sphereMeshList : MutableList<OBJLoader.OBJMesh> = sphereRes.objects[0].meshes

    private var bike: Renderable? = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",-90f,89.52f,-0.45f)
    private var bikeTest: Renderable? = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",0f,0f,0f)
    private var bulletTest: Renderable? = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",0f,0f,0f)
    private var groundDiff : Texture2D = Texture2D.invoke("assets/textures/ground_diff.png", true)
    private var groundEmit :Texture2D = Texture2D.invoke("assets/textures/ground_emit.png", true)
    private var groundSpec :Texture2D = Texture2D.invoke("assets/textures/ground_spec.png", true)
    private var groundMaterial = Material(groundDiff, groundEmit, groundSpec, 240f, Vector2f(64f, 64f))

    private var ground : Renderable
    private var skybox : Renderable
    private var sphere: Renderable
    //private var sphere : Renderable

    private var xPosition : Double
    private var yPosition : Double

    //scene setup
    init {

        xPosition  = 0.0
        yPosition  = 0.0

        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl")
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl")

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
        loadCube(skyboxShader, skyboxList, 10)

        groundEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        groundSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        meshG = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttributesG, groundMaterial)
        meshListG.add(meshG)
        ground  = Renderable(meshListG, hp = 30000000)

        meshSkybox = Mesh(skyboxMeshList[0].vertexData, skyboxMeshList[0].indexData, vertexAttributesG)
        meshListSkybox.add(meshSkybox)

        meshS = Mesh(sphereMeshList[0].vertexData, sphereMeshList[0].indexData, vertexAttributesG)
        meshListS.add(meshS)
        sphere = Renderable(meshListS)

        sphere.scale(Vector3f(2f))
        sphere.translate(Vector3f(15f, 10f, 15f))

        skybox = Renderable(meshListSkybox)
        skybox.scale(Vector3f(30f))

        bike?.scale((Vector3f(0.8f)))
        bike?.hp = 10
        bike?.hitbox = 1f
        bikeTest?.translate(Vector3f(10f, 0f, 10f))
        bikeTest?.hp = 3
        bikeTest?.hitbox = 1f
        bulletTest?.scale(Vector3f(0.2f))
        bulletTest?.parent = bike
        bulletTest?.translate(Vector3f(0f,1f,-15f))
        bulletTest?.hitbox
        objects.add(bikeTest)
        objects.add(bike)
        objects.add(bulletTest)
        cam = TronCamera( _parent = bike)
        cam1 = TronCamera( _parent = bike)
        cam2 = TronCamera( _parent = bike)
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
        pointLight4 = PointLight(Vector3f(15f ,10f, 15f), Vector3f(0.5f, 0f, 1f))
        pointList.add(pointLight4)
        pointList.add(pointLight)
        pointList.add(pointLight1)
        pointList.add(pointLight2)
        pointList.add(pointLight3)
        //pointList.add(pointLight4)
        spotLight = SpotLight(Vector3f(0f, 1f, -0.5f), Vector3f(1f, 0f, 0f), 20f, 10f, _parent = bike)
        spotLight.rotate(Math.toRadians(-20f), 0f, 0f)

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

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glActiveTexture(texUnit)
        shaderProgram.setUniformInt("sky", texUnit)

    }

//    fun random() :Random{
//
//
//    }

    fun hitboxCalc(renderable: Renderable?) : Boolean{
        objects.forEach {
            if (renderable != null) {
                if (it != null) {
                    if(renderable != it){
                        if(!(renderable == bulletTest && it == bike)||(renderable == bike && it == bulletTest) ) {
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
        renderable?.scale(Vector3f(0.2f))
        enemyCount = enemyCount - 1
        if(enemyCount > 1){
            spawnEnemys()
        }
    }

    fun spawnEnemys(){
        enemyCountMax = enemyCountMax +2
        enemyCount = enemyCountMax
        val tempEnemys : Int = enemyCount
        while (tempEnemys > 0){
            spawnEnemyRandom()
        }
    }

    fun spawnEnemyRandom(){

    }

    fun render(dt: Float, t: Float) {
        staticShader.use()
        staticShader.setUniformVec3("colorground", Vector3f(0.01f, 1.0f, 0.01f))
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        cam.bind(staticShader)

        var i = 0
        spotLight.bind(staticShader, cam.getCalculateViewMatrix())
        pointList.forEach{
            //it.bindList(staticShader, cam.getCalculateViewMatrix(), i)
            it.bindList(staticShader, cam.getCalculateViewMatrix(), i)
            i++
        }
        bike?.render(staticShader)
        ground.render(staticShader)
        sphere.render(staticShader)
        bikeTest?.render(staticShader)
        bulletTest?.render(staticShader)

        //Skybox render
        GL11.glDepthMask(false)
        glDisable(GL_CULL_FACE)
        skyboxShader.use()
        skyboxShader.setUniformMat("view_sky", Matrix4f(Matrix3f(cam.getCalculateViewMatrix())), false)
        cam.bind(skyboxShader)
        skybox.render(skyboxShader)
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

        if(bike?.hp!! <= 0){
            cleanup()
        }


        if (window.getKeyState(GLFW.GLFW_KEY_W)){bike?.translate(Vector3f(0f, 0f, - 0.1f))

            if(hitboxCalc(bike) == false){
                bike?.translate(Vector3f(0f, 0f,  0.1f))

                if(invinFrame == false){
                    bike?.hp = bike?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
                println(bike?.hp!!)
                println(t)
                println(tempT)

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_S)){bike?.translate(Vector3f(0f, 0f,  0.05f))

            if(hitboxCalc(bike) == false){
                bike?.translate(Vector3f(0f, 0f,  -0.05f))
                if(invinFrame == false){
                    bike?.hp = bike?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_D)){bike?.translate(Vector3f(0.05f, 0f,  0f))

            if(hitboxCalc(bike) == false){
                bike?.translate(Vector3f(-0.05f, 0f,  0f))
                if(invinFrame == false){
                    bike?.hp = bike?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }

            }}

        if (window.getKeyState(GLFW.GLFW_KEY_A)){bike?.translate(Vector3f(-0.05f, 0f,  0f))

            if(hitboxCalc(bike) == false){
                bike?.translate(Vector3f(0.05f, 0f,  0f))
                if(invinFrame == false){
                    bike?.hp = bike?.hp?.minus(1)!!
                    invinFrame = true
                    invinFrameBuffer = true
                }
            }}

        if(window.getKeyState(GLFW.GLFW_KEY_SPACE) && pressSpace == true){
            pressSpace = false


            while (x < 250) {
                bulletTest?.translate(Vector3f(0f, 0f, - 0.2f))
                if(hitboxCalc(bulletTest) == false){
                    return
                }
                x++
                println(x)


            }
        }

        if(!window.getKeyState(GLFW.GLFW_KEY_SPACE)){
            pressSpace = true
            bulletTest?.translate(Vector3f(0f, 0f,  x * 0.2f))
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

            bike!!.rotate(0.0f, (dxpos.toFloat() * 0.002f), 0f)

            xPosition = xpos
            yPosition = ypos
        }

        if(camState == 3) {
            var dxpos: Double = xPosition - xpos
            cam3.rotateAroundPoint(0.0f, (dxpos.toFloat() * 0.002f), 0f, bike!!.getYAxis())
            xPosition = xpos
            yPosition = ypos
        }

    }

    fun cleanup() {
        staticShader.cleanup() // selbst erstellt
    }
}
