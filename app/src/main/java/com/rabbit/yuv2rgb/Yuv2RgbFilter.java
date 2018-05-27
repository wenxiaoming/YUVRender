package com.rabbit.yuv2rgb;

import android.opengl.GLES20;
import android.util.Log;
import java.nio.*;


/**
 * Created by kevin on 2018/5/8.
 */

public class Yuv2RgbFilter {

    final static int textureCount = 3;
    static int mProgram;
    static int mPositionHandle;
    static int mTexCoordHandle;
    static int[] mTextureHandle = new int[textureCount];
    static int[] mTexture = new int[textureCount];
    static FloatBuffer mVertexBuffer;
    static FloatBuffer mTexCoorBuffer;

    static float[] vertices= { -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,};

    static float[] texCoord= { 0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f};
    /**
     *  initialize yuv2rgb filter
     */
    public static void initShader()
    {
        mProgram = ShaderManager.getShaderProgram();

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
        ShaderManager.checkGlError("glGetAttribLocation");

        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "texcoord");
        ShaderManager.checkGlError("glGetAttribLocation");

        mTextureHandle[0] = GLES20.glGetUniformLocation(mProgram, "samplerY");
        ShaderManager.checkGlError("glGetUniformLocation");
        mTextureHandle[1] = GLES20.glGetUniformLocation(mProgram, "samplerU");
        ShaderManager.checkGlError("glGetUniformLocation");
        mTextureHandle[2] = GLES20.glGetUniformLocation(mProgram, "samplerV");
        ShaderManager.checkGlError("glGetUniformLocation");

    }
    /**
     *  generate texture
     */
    public static void generateTexture()
    {
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glUseProgram(mProgram);

        GLES20.glGenTextures(3, mTexture, 0);

        for(int i = 0; i < 3; i++)
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[i]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }
    /**
     *  upload texture for yuv channel
     */
    public static  void uploadTexture(int width, int height, Buffer[] pixels)
    {
        int[] planes    = { 0, 1, 2 };
        int[] widths    = { width, width/2, width/2 };
        int[] heights  = { height, height/2, height/2 };

        for (int i = 0; i < 3; ++i)
        {
            int plane = planes[i];
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[i]);
            ShaderManager.checkGlError("glBindTexture");

            if(pixels[plane] == null)
                Log.e("YUV2RGBFilter", "pixels[plane] == null");

            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_LUMINANCE,
                    widths[plane],
                    heights[plane],
                    0,
                    GLES20.GL_LUMINANCE,
                    GLES20.GL_UNSIGNED_BYTE,
                    pixels[plane]);

            ShaderManager.checkGlError("glTexImage2D");

            GLES20.glUniform1i(mTextureHandle[i], i);

            ShaderManager.checkGlError("glUniform1i");
        }

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
                false, 0, mVertexBuffer);
        ShaderManager.checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        ShaderManager.checkGlError("glEnableVertexAttribArray");

        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT,
                false, 0, mTexCoorBuffer);
        ShaderManager.checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        ShaderManager.checkGlError("glEnableVertexAttribArray");
    }

    public static void disableVertexAttribArray()
    {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
    }

    public static void updateVertexParam()
    {
        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer texCoordBuffer = ByteBuffer.allocateDirect(texCoord.length * 4);
        texCoordBuffer.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = texCoordBuffer.asFloatBuffer();
        mTexCoorBuffer.put(texCoord);
        mTexCoorBuffer.position(0);
    }
}

