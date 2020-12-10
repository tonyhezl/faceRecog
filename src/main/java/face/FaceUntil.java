package face;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * 人脸识别工具类
 * @author HZL
 * @date 2020-12-10
 */
public class FaceUntil {

    private final String APP_ID="4YK9qWZik6kKbSHpNuQXCb7byv93UkyAp14UzjkyeSxF";

    private final String SDK_KEY="4GxdLd3SKT3D5KGRfQGKfrrakrK4V6ZMZT9pskmarJkp";

    FaceEngine faceEngine = new FaceEngine("D:\\ArcSoft_ArcFace_Java_Windows_x64_V3.0\\libs\\WIN64");

    /**
     * 初始化人脸识别引擎
     * @return
     */
    public int initFaceEngine(){
        //激活引擎
        int errorCode = faceEngine.activeOnline(APP_ID, SDK_KEY);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);
        return errorCode;
    }

    /**
     * 检测人脸并提取人脸特征然后进行对比
     * @param image
     */
    public void faceRec(BufferedImage image){
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(image);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        //人脸特征提取
        FaceFeature source = new FaceFeature();
        FaceFeature target = new FaceFeature();
        //0---成功
        int detcetCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(),
                imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        if(faceInfoList.size()>0){
            System.out.println("来源人脸信息------->"+faceInfoList);
            int featureCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(),
                    imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), source);
            System.out.println("来源特征值大小：" + source.getFeatureData().length);
        }else{
            System.out.println("未检测到源人脸");
        }
        ImageInfo imageTarget = getRGBData(new File("d:\\ccc.jpg"));
        List<FaceInfo> faceInfoTarget = new ArrayList<FaceInfo>();
        int targetDetectCode = faceEngine.detectFaces(imageTarget.getImageData(), imageTarget.getWidth(),
                imageTarget.getHeight(), imageTarget.getImageFormat(), faceInfoTarget);
        if(faceInfoTarget.size()>0){
            System.out.println("目标人脸信息------->"+faceInfoTarget);
            int targetFeatureCode = faceEngine.extractFaceFeature(imageTarget.getImageData(), imageTarget.getWidth(),
                    imageTarget.getHeight(), imageTarget.getImageFormat(), faceInfoTarget.get(0), target);
            System.out.println("目标特征值大小：" + target.getFeatureData().length);
        }else{
            System.out.println("未检测到目标人脸");
        }
        //对比相似度
        FaceSimilar faceSimilar = new FaceSimilar();
        int similarCode=faceEngine.compareFaceFeature(target,source,
                CompareModel.ID_PHOTO,faceSimilar);
        System.out.println("相似度：" + faceSimilar.getScore());
    }
    /**
     * 获取年龄信息，获取性别信息，获取人脸三维角度信息，获取RGB活体信息对象
     * 需要在调用process(byte[], int, int, ImageFormat, List, FunctionConfiguration)后调用
     * @param image
     */
    public void liveDetect(BufferedImage image){
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(image);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        //设置活体测试
        int errorCode = faceEngine.setLivenessParam(0.5f, 0.7f);
        //人脸属性检测
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        errorCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(),
                imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList, configuration);
        //年龄检测
        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
        errorCode = faceEngine.getAge(ageInfoList);
        if (ageInfoList.size()>0) {
            System.out.println("年龄：" + ageInfoList.get(0).getAge());
        }else{
            System.out.println("未检测到人脸");
        }
        //性别检测
        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
        errorCode = faceEngine.getGender(genderInfoList);
        if (genderInfoList.size()>0) {
            System.out.println("性别：" + genderInfoList.get(0).getGender());
        }else{
            System.out.println("未检测到人脸");
        }
    }

    public void faceRS(BufferedImage image){
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(image);
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        //0---成功
        int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(),
                imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
        System.out.println(faceInfoList);
    }
}
