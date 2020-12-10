package com.example.rtspDemo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import face.FaceUntil;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;

/**
 * @hezl
 * @date 2020-12-9
 * javacv-流转视频文件、图片
 */
@Component
public class StreanTransferVideo {
    /**
     * 流地址
     */
    private String streamURL;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 视频流
     */
    private InputStream inputStream;

    /**
     * 视频帧图片存储路径
     */
    public static String videoFramesPath = "D:/image";

    private FaceUntil faceUntil = new FaceUntil();
    /**
     * 拉流录制视频
     * @param inputStream
     */
    public void draw(InputStream inputStream) {
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();
            Frame frame = grabber.grabFrame();
            if (frame != null) {
                File outFile = new File(filePath);
                if (!outFile.isFile()) {
                    try {
                        outFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
                recorder = new FFmpegFrameRecorder(filePath, 1080, 1440, 1);
                //直播流格式
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setFormat("flv");// 录制的视频格式
                recorder.setFrameRate(25);// 帧数
                //百度翻译的比特率，默认400000，但是我400000贼模糊，调成800000比较合适
                recorder.setVideoBitrate(800000);
                recorder.start();
                while ((frame != null)) {
                    //录制
                    recorder.record(frame);
                    //获取下一帧
                    frame = grabber.grabFrame();
                }
                recorder.record(frame);
                // 停止录制
                /*recorder.stop();
                grabber.stop();*/
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        } finally {
            if (null != grabber) {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拉流存为图片
     * @param inputStream
     */
    public void printImage(InputStream inputStream) {
        //Frame对象
        Frame frame = null;
        Random random=new Random();
        int rd=random.nextInt(10000);
        //标识
        int flag = 0;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(inputStream);
        try {
            fFmpegFrameGrabber.start();
            //getFrameRate()方法：获取视频文件信息,总帧数
            int ftp = fFmpegFrameGrabber.getLengthInFrames();
            //System.out.println("时长 " + ftp / fFmpegFrameGrabber.getFrameRate() / 60);
            System.out.println("============抽帧保存图片开始============");
            //文件绝对路径+名字
            String fileName = videoFramesPath + "/img_" + String.valueOf(rd) + ".jpg";
            //文件储存对象
            File outPut = new File(fileName);
            //获取帧
            frame = fFmpegFrameGrabber.grabImage();
            if (frame != null) {
                ImageIO.write(FrameToBufferedImage(frame), "jpg", outPut);
            }
            System.out.println("============保存单帧结束============");
            //结束图片保存
            //fFmpegFrameGrabber.stop();
        } catch (IOException E) {

        }
    }

    /**
     * 人脸识别对比
     * @param inputStream
     */
    public void faceToFaceRecog(InputStream inputStream){
        //Frame对象
        Frame frame = null;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(inputStream);
        try {
            fFmpegFrameGrabber.start();
            //获取帧
            frame = fFmpegFrameGrabber.grabImage();
            faceUntil.initFaceEngine();
            faceUntil.faceRec(FrameToBufferedImage(frame));
            //结束图片保存
            //fFmpegFrameGrabber.stop();
        } catch (IOException E) {

        }
    }


    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
