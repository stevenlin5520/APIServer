package com.steven.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 * 
 * @Description 生成二维码的请求
 * @author Steven
 * @date 2019年3月27日 上午10:03:53
 */
@Controller
@RequestMapping("/qrcode")
public class QrCodeController {

	@RequestMapping("/create")
	public void createImage(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int width = 240, height = 240;
        int margin = 0;//边框值
        int type = 1;
        if(type==1){
            //第一种写法==================================
//            EncodeHintType.MARGIN: 边框
            ByteArrayOutputStream out = QRCode.from("https://www.sunjs.com").withHint(EncodeHintType.MARGIN, margin).to(ImageType.PNG).withSize(width, height).stream();
            OutputStream outStream = response.getOutputStream();
            outStream.write(out.toByteArray());
            outStream.flush();
            outStream.close();
        }else{
            //第二种写法==========================================
            ServletOutputStream stream = null;  
            try {
                stream = response.getOutputStream();  
                Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
                hints.put(EncodeHintType.MARGIN, margin);
                BitMatrix bm = new MultiFormatWriter().encode("https://www.sunjs.com", BarcodeFormat.QR_CODE, width, height, hints);
                MatrixToImageWriter.writeToStream(bm, "png", stream);
            } catch (WriterException e) {
                e.printStackTrace();
            } finally{
                if (stream != null) {  
                    stream.flush();  
                    stream.close();  
                }  
            }
        }
	}
}
