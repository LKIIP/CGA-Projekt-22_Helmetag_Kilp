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
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_CUBE_MAP
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12.*
import java.util.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val skyboxShader: ShaderProgram
    private val toonShader: ShaderProgram
    private val meshG: Mesh
    private val meshSkybox: Mesh
    private val meshHead : Mesh
    private val meshListG : MutableList<Mesh> = arrayListOf()
    private val meshListSkybox : MutableList<Mesh> = arrayListOf()
    private val meshListHead : MutableList<Mesh> = arrayListOf()
    private val cam : TronCamera
    private val pointLight : PointLight
    private val pointLight1 : PointLight
    private val pointLight2 : PointLight
    private val pointLight3 : PointLight
    private val pointLight4 : PointLight
    private val spotLight : SpotLight
    private val pointList: MutableList<PointLight> = arrayListOf()
    private val skyboxList: MutableList<String> = arrayListOf("assets/textures/skybox/back.jpg",
            "assets/textures/skybox/bottom.jpg",
            "assets/textures/skybox/front.jpg",
            "assets/textures/skybox/left.jpg",
            "assets/textures/skybox/right.jpg",
            "assets/textures/skybox/top.jpg")

    val groundRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    val groundMeshList : MutableList<OBJLoader.OBJMesh> = groundRes.objects[0].meshes
    val skyboxRes : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/skybox.obj")
    val skyboxMeshList : MutableList<OBJLoader.OBJMesh> = skyboxRes.objects[0].meshes

    private var bike: Renderable? = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",-90f,89.52f,-0.45f)
    private var groundDiff : Texture2D = Texture2D.invoke("assets/textures/ground_diff.png", true)
    private var groundEmit :Texture2D = Texture2D.invoke("assets/textures/ground_emit.png", true)
    private var groundSpec :Texture2D = Texture2D.invoke("assets/textures/ground_spec.png", true)
    private var groundMaterial = Material(groundDiff, groundEmit, groundSpec, 60f, Vector2f(64f, 64f))
   // private var skyboxTex : Skybox = Skybox.invoke(skyboxList, false)

    val headRes: OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/head.obj")
    val headMeshList : MutableList<OBJLoader.OBJMesh> = headRes.objects[0].meshes

    private var ground : Renderable
    private var skybox : Renderable
    private var head : Renderable
    //private var sphere : Renderable

    private var xPosition : Double
    private var yPosition : Double

    private var texid : Int = 0
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
        val attrPosSkybox =  VertexAttribute(3, GL_FLOAT, 12, 0) //position
        val vertexAttributesSkybox = arrayOf<VertexAttribute>(attrPosSkybox)


        groundEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
        groundDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        groundSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_NEAREST_MIPMAP_NEAREST, GL_NEAREST)
        meshG = Mesh(groundMeshList[0].vertexData, groundMeshList[0].indexData, vertexAttributesG, groundMaterial)
        meshListG.add(meshG)
        ground  = Renderable(meshListG)

        meshHead = Mesh(headMeshList[0].vertexData, headMeshList[0].indexData, vertexAttributesG)
        meshListHead.add(meshHead)

        head = Renderable(meshListHead)

        meshSkybox = Mesh(skyboxMeshList[0].vertexData, skyboxMeshList[0].indexData, vertexAttributesG)
        meshListSkybox.add(meshSkybox)

        skybox = Renderable(meshListSkybox)
        skybox.scale(Vector3f(30f))

        bike?.scale((Vector3f(0.8f)))
        cam = TronCamera( _parent = bike)

        cam.rotate(Math.toRadians(-35f), 0f, 0f)
        cam.translate(Vector3f(0f, 0f, 4f))

        pointLight = PointLight(Vector3f(0f ,3f, 0f), Vector3f(1f, 1f, 1f), _parent = bike)
        pointLight1 = PointLight(Vector3f(15f ,5f, -15f), Vector3f(1f, 0.25f, 0f))
        pointLight2 = PointLight(Vector3f(15f ,5f, 15f), Vector3f(1f, 0.5f, 0f))
        pointLight3 = PointLight(Vector3f(-15f ,5f, 15f), Vector3f(1f, 0.75f, 0f))
        pointLight4 = PointLight(Vector3f(-15f ,5f, -15f), Vector3f(1f, 1f, 0f))
        //pointList.add(pointLight)
        pointList.add(pointLight1)
        pointList.add(pointLight2)
        pointList.add(pointLight3)
        pointList.add(pointLight4)
        spotLight = SpotLight(Vector3f(0f, 1f, -0.5f), Vector3f(1f, 0f, 0f), 20f, 10f, _parent = bike)
        spotLight.rotate(Math.toRadians(-20f), 0f, 0f)

    }

    fun render(dt: Float, t: Float) {
        //glEnable(GL_CULL_FACE); GLError.checkThrow()
        toonShader.use()
        staticShader.setUniformVec3("colorground", Vector3f(0.01f, 1.0f, 0.01f))
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        cam.bind(toonShader)
        spotLight.bind(toonShader, cam.getCalculateViewMatrix())
        //staticShader.use()
        ground.render(toonShader)
        staticShader.setUniformFloat("zeit", t)
        var i = 0
        pointList.forEach{
            it.bindList(staticShader, cam.getCalculateViewMatrix(), i)
            it.bindList(toonShader, cam.getCalculateViewMatrix(), i)
            i++
        }
        head.render(toonShader)
        bike?.render(toonShader)
        staticShader.setUniformFloat("zeit", 1f)

        //glDisable(GL_CULL_FACE); GLError.checkThrow()
       // skyboxShader.use()
        //skyboxTex.bind(3)
        //skyboxShader.setUniformInt("skybox", 3)
        //skybox.render(skyboxShader)

    }

    fun update(dt: Float, t: Float) {

        if(window.getKeyState(GLFW.GLFW_KEY_W) && window.getKeyState(GLFW.GLFW_KEY_A)) {
            bike?.rotate(0f,dt,0f)
            bike?.translate(Vector3f(0f, 0f, -dt*5))}

        if(window.getKeyState(GLFW.GLFW_KEY_W) && window.getKeyState(GLFW.GLFW_KEY_D)) {
            bike?.rotate(0f,-dt,0f)
            bike?.translate(Vector3f(0f, 0f, -dt*5))}

        if(window.getKeyState(GLFW.GLFW_KEY_S) && window.getKeyState(GLFW.GLFW_KEY_A)) {
            bike?.rotate(0f,dt,0f)
            bike?.translate(Vector3f(0f, 0f, dt*5))}

        if(window.getKeyState(GLFW.GLFW_KEY_S) && window.getKeyState(GLFW.GLFW_KEY_D)) {
            bike?.rotate(0f,-dt,0f)
            bike?.translate(Vector3f(0f, 0f, dt*5))}

        if (window.getKeyState(GLFW.GLFW_KEY_W)){bike?.translate(Vector3f(0f, 0f, -dt * 10))}
        if (window.getKeyState(GLFW.GLFW_KEY_S)){bike?.translate(Vector3f(0f, 0f, dt * 10))}
        if (window.getKeyState(GLFW.GLFW_KEY_A)){bike?.rotate(0f,dt,0f)
            bike?.translate(Vector3f(0f, 0f, -dt*2))}
        if (window.getKeyState(GLFW.GLFW_KEY_D)){bike?.rotate(0f,-dt,0f)
            bike?.translate(Vector3f(0f, 0f, -dt*2))}

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {

        var dxpos: Double = xPosition - xpos
        cam.rotateAroundPoint(0.0f, (dxpos.toFloat() * 0.002f),0f, bike!!.getYAxis())
        xPosition = xpos
        yPosition = ypos

    }

    fun cleanup() {
        staticShader.cleanup() // selbst erstellt
    }
}