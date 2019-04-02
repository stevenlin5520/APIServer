package com.steven.util;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 * 
 * @Description 二维码生成器
 * @author Steven
 * @date 2019年3月27日 上午9:49:51
 */
public class QrCodeUtil {

	/*public static Bitmap createQRImage(String data, int QR_WIDTH, int QR_HEIGHT) {
		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	
	public static String createQrCode(String data, int weight){
		
		/*int width = 240, height = 240;
        int margin = 5;//边框值
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
        }*/
		
		return "";
	}
	
	
	public static void main(String[] args) {
		createQrCode("test",240);
	}
	
}
