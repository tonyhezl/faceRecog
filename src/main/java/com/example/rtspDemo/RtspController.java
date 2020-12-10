package com.example.rtspDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @hezl
 * @date 2020-12-8
 */
@Controller
@RequestMapping("/rtsp")
public class RtspController {

    @Autowired
    private WsHandler wsHandler;
    @Autowired
    private StreanTransferVideo transferVideo;

    @RequestMapping("/receive")
    @ResponseBody
    public String receive(HttpServletRequest request, Object response) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            int len = -1;
            while ((len =inputStream.available()) !=-1) {
                byte[] data = new byte[len];
                inputStream.read(data);
                //transferVideo.setFilePath("D:/video.flv");
                //transferVideo.draw(inputStream);
                //transferVideo.printImage(inputStream);
                transferVideo.faceToFaceRecog(inputStream);
                wsHandler.sendVideo(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("over");
        return "1";
    }

}
