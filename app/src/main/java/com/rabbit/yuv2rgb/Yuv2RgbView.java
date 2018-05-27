package com.rabbit.yuv2rgb;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

import android.util.Log;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Yuv2RgbView extends GLSurfaceView
{
    private Yuv2RgbRenderer mRenderer;
    private ByteBuffer mYuvBuffer;
    private ByteBuffer mBufferU;
    private ByteBuffer mBufferV;
    private int mVertCount = 4;

    public Yuv2RgbView(Context context)
    {
        super(context);

        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setEGLContextClientVersion(2);

        mRenderer = new Yuv2RgbRenderer();
        setRenderer(mRenderer);
        this.setKeepScreenOn(true);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class Yuv2RgbRenderer implements GLSurfaceView.Renderer
    {
        @Override
        public void onDrawFrame(GL10 gl) {
            mYuvBuffer.position(0);
            mBufferU.position(0);
            mBufferV.position(0);

            Buffer[] yuvBuffer = {mYuvBuffer, mBufferU, mBufferV};

            GLES20.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            Yuv2RgbFilter.uploadTexture(176, 144, yuvBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertCount);

            Yuv2RgbFilter.disableVertexAttribArray();

            Log.e("YuvRgbRender", "onDrawFrame");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            ShaderManager.compileShader();

            Yuv2RgbFilter.initShader();
            Yuv2RgbFilter.generateTexture();

            getYuvData("foreman.qcif");
            Yuv2RgbFilter.updateVertexParam();
        }

        public void getYuvData(String fileName)
        {
            String res="";
            try{
                InputStream in = getResources().getAssets().open(fileName);

                int length = in.available();
                byte [] buffer = new byte[length];

                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length);
                mYuvBuffer = byteBuffer;

                in.read(buffer);
                in.close();

                mYuvBuffer.put(buffer);
                mYuvBuffer.position(0);

                mBufferU = ByteBuffer.allocateDirect(88*72);
                mBufferV = ByteBuffer.allocateDirect(88*72);

                mBufferU.put(buffer,176*144,88*72);
                mBufferU.position(0);

                mBufferV.put(buffer,176*144+88*72,88*72);
                mBufferV.position(0);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}