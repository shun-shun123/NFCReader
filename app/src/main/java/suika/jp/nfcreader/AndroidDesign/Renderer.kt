package suika.jp.nfcreader.AndroidDesign

import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig


class GLRenderer : GLSurfaceView.Renderer {
    internal var triangle: Triangle = Triangle()
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        triangle = Triangle()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

    }

    override fun onDrawFrame(unused: GL10) {
        //背景色(R,G,B,ALPHA)
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        triangle.draw()
    }
}

class Triangle {
    //シンプルなシェーダー
    val vertexShaderCode: String =
    "attribute  vec4 vPosition;" +
    "void main() {" +
    "  gl_Position = vPosition;" +
    "}";
    //シンプル色は自分で指定(R,G,B ALPHA)指定
    val fragmentShaderCode: String =
    "precision mediump float;" +
    "void main() {" +
    "  gl_FragColor =vec4(1.0, 0.0, 0.0, 1.0);" +
    "}";
    private fun loadShader(type: Int, shaderCode: String): Int{
        val shader: Int = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private var shaderProgram: Int = 0;

    init{
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);
    }

    fun draw(){
        GLES20.glUseProgram(shaderProgram);
        val positionAttrib: Int = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionAttrib);
        val vertices: FloatArray = floatArrayOf(
                0.0f, 0.5f, 0.0f,          //三角形の点A(x,y,z)
                -0.5f, -0.5f, 0.0f,        //三角形の点B(x,y,z)
                0.5f, -0.5f, 0.0f          //三角形の点C(x,y,z)
            )
            val bb: ByteBuffer = ByteBuffer.allocateDirect (vertices.size * 4);
            bb.order(ByteOrder.nativeOrder());
            val vertexBuffer: FloatBuffer = bb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(positionAttrib, vertices.size, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 3);

            GLES20.glDisableVertexAttribArray(positionAttrib);
        }
}