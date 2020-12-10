package com.example.rtspDemo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @hezl
 * @date 2020-12-8
 */
@Component
public class ConvertVideoPakcet implements ApplicationRunner {
    public Process process;

    public Integer pushVideoAsRTSP(String id, String serverPath){
        int flag = -1;
        // ffmpeg位置，最好写在配置文件中
        // String ffmpegPath = "E:\\webset\\ffmpeg\\ffmpeg-20200213-6d37ca8-win64-static\\bin\\";
        String ffmpegPath = "H://ffmpeg-N-100214-g95fd790c14-win64-gpl-shared//bin//";
        try {
            // 视频切换时，先销毁进程，全局变量Process process，方便进程销毁重启，即切换推流视频
            if(process != null){
                process.destroy();
                System.out.println(">>>>>>>>>>推流视频切换<<<<<<<<<<");
            }
            // cmd命令拼接，注意命令中存在空格
            String command = ffmpegPath; // ffmpeg位置
            command += "ffmpeg "; // ffmpeg开头，-re代表按照帧率发送，在推流时必须有
            command += " -i \"" + id + "\""; // 指定要推送的视频
            command += " -q 0 -f mpegts -codec:v mpeg1video -s 800x600 " + serverPath; // 指定推送服务器，-f：指定格式
            System.out.println("ffmpeg推流命令：" + command);
            // 运行cmd命令，获取其进程
            process = Runtime.getRuntime().exec(command);
            // 输出ffmpeg推流日志
            BufferedReader br= new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println("视频推流信息[" + line + "]");
            }
            flag = process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ConvertVideoPakcet convertVideoPakcet = new ConvertVideoPakcet();
        convertVideoPakcet.pushVideoAsRTSP("rtsp://admin:Abc.12345@192.168.1.66:1554/cam/h264/ch33/main/av_stream",
                "http://127.0.0.1:8081/rtsp/receive");
    }
}
