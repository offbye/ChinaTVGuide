package com.offbye.chinatvguide.util;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 *
 * File通用操作工具类
 *
 * @author Kelvin Van
 * @version [ME WOYOUClient_Handset V100R001C04SPC002, 2011-10-17]
 */
public final class FileUtil
{

    private static final String TAG = FileUtil.class.getSimpleName();
    /**
     * 缓冲区大小
     */
    private static final int BUFFER_SIZE = 100 * 1024;

    private FileUtil()
    {
    }

    /**
     *
     * 文件转化byte[]操作
     *
     * @param file 需要转化为byte[]的文件
     * @return 文件的byte[]格式
     * @throws IOException IO流异常
     */
    public static byte[] fileToByte(File file) throws IOException
    {
        InputStream in = new FileInputStream(file);
        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] barr = new byte[1024];
            while (true)
            {
                int r = in.read(barr);
                if (r <= 0)
                {
                    break;
                }
                buffer.write(barr,
                    0,
                    r);
            }
            return buffer.toByteArray();
        }
        finally
        {
            closeStream(in);
        }
    }

    /**
     *
     * 将文件的byte[]格式转化成一个文件
     *
     * @param b 文件的byte[]格式
     * @param fileName 文件名称
     * @return 转化后的文件
     */
    public static File byteToFile(byte[] b, String fileName)
    {
        BufferedOutputStream bos = null;
        File file = null;
        try
        {
            file = new File(fileName);
            if (!file.exists())
            {
                File parent = file.getParentFile();
                if (!parent.mkdirs())
                {
                    // 创建不成功的话，直接返回null
                    return null;
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
        }
        catch (IOException e)
        {
            Log.e(TAG,
                "byteToFile error",
                e);
        }
        finally
        {
            closeStream(bos);
        }
        return file;
    }

    /**
     * 判断文件是否是图片格式
     *
     * @param fileName 文件名称
     * @return true 表示是图片格式 false 表示不是图片格式
     */
    public static boolean isPictureType(String fileName)
    {
        int index = fileName.lastIndexOf(".");
        if (index != -1)
        {
            String type = fileName.substring(index).toLowerCase();
            if (".png".equals(type) || ".gif".equals(type)
                || ".jpg".equals(type) || ".bmp".equals(type)
                || ".jpeg".equals(type))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param path 路径
     * @return 是否即时删除成功
     */
    public static boolean deleteFile(String path)
    {
        if (path == null || path.trim().length() < 1)
        {
            return false;
        }
        try
        {
            File file = new File(path);
            if (file != null && file.exists() && file.isFile())
            {
                try
                {
                    return file.delete();
                }
                catch (Exception e)
                {
                    Log.e(TAG,
                        "delete file error",
                        e);
                    file.deleteOnExit();
                }
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 专门用来关闭可关闭的流
     *
     * @param beCloseStream 需要关闭的流
     * @return 已经为空或者关闭成功返回true，否则返回false
     */
    public static boolean closeStream(java.io.Closeable beCloseStream)
    {
        if (beCloseStream != null)
        {
            try
            {
                beCloseStream.close();
                return true;
            }
            catch (IOException e)
            {
                Log.e(TAG,
                    "close stream error",
                    e);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileLength(String filePath)
    {
        if (filePath == null || filePath.trim().length() < 1)
        {
            return 0;
        }
        try
        {
            File file = new File(filePath);
            return file.length();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /**
     * 复制文件
     *
     * @param origin 原始文件
     * @param dest 目标文件
     * @return 是否复制成功
     */
    public static boolean copyFile(File origin, File dest)
    {
        if (origin == null || dest == null)
        {
            return false;
        }
        if (!dest.exists())
        {
            File parentFile = dest.getParentFile();
            if (!parentFile.exists())
            {
                boolean succeed = parentFile.mkdirs();
                if (!succeed)
                {
                    Log.i(TAG,
                        "copyFile failed, cause mkdirs return false");
                    return false;
                }
            }
            try
            {
                dest.createNewFile();
            }
            catch (Exception e)
            {
                Log.i(TAG,
                    "copyFile failed, cause createNewFile failed");
                return false;
            }
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try
        {
            in = new FileInputStream(origin);
            out = new FileOutputStream(dest);
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
            int length = BUFFER_SIZE;
            while (true)
            {
                if (inC.position() == inC.size())
                {
                    return true;
                }
                if ((inC.size() - inC.position()) < BUFFER_SIZE)
                {
                    length = (int) (inC.size() - inC.position());
                }
                else
                {
                    length = BUFFER_SIZE;
                }
                inC.transferTo(inC.position(),
                    length,
                    outC);
                inC.position(inC.position() + length);
            }
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            closeStream(in);
            closeStream(out);
        }
    }
}
