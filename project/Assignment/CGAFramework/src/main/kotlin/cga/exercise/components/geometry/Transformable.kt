package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.*

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {
    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    fun getModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix);
    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        if(parent == null){
            return getModelMatrix()
        }else{
           return parent!!.getWorldModelMatrix().mul(modelMatrix)

        }
    }

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotate(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotate(pitch, 1f, 0f, 0f)
                   .rotate(yaw, 0f, 1f, 0f)
                   .rotate(roll, 0f, 0f, 1f)
    }

    fun rotateLocal(pitch: Float, yaw: Float, roll: Float){

        modelMatrix.rotateXYZ(pitch, yaw,roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {

        modelMatrix = Matrix4f().translate(altMidpoint).rotateXYZ(pitch,yaw, roll).translate(altMidpoint.negate()).mul(modelMatrix)

    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translate(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun preTranslate(deltaPos: Vector3f) {

            modelMatrix.translateLocal(deltaPos)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scale(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {
        var vec : Vector3f = Vector3f()
        modelMatrix.getColumn(3, vec)
        return vec
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {

            var vec : Vector3f = Vector3f()
         getWorldModelMatrix().getColumn(3, vec)
        return vec

    }

    fun setWorldPosition(newPos : Vector3f){
        var temp : Vector4fc = Vector4f(newPos.x, newPos.y, newPos.z, getWorldModelMatrix().getRowColumn(3,3))
        getWorldModelMatrix().setColumn(3,temp )
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        val vec : Vector3f = Vector3f()
        modelMatrix.getColumn(0, vec)
        vec.normalize()
        return vec
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        val vec : Vector3f= Vector3f()
        modelMatrix.getColumn(1, vec)
        return vec.normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        val vec : Vector3f= Vector3f()
        modelMatrix.getColumn(2, vec)
        return vec.normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {

        val vec : Vector3f= Vector3f()
        val mat : Matrix4f = getWorldModelMatrix()
        mat.getColumn(0, vec)
        vec.normalize()
        return vec

    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {

        val vec : Vector3f= Vector3f()
        val mat : Matrix4f = getWorldModelMatrix()
        mat.getColumn(1, vec)
        vec.normalize()
        return vec

    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {

        val vec : Vector3f= Vector3f()
        val mat : Matrix4f = getWorldModelMatrix()
        mat.getColumn(2, vec)
        vec.normalize()
        return vec

    }
}